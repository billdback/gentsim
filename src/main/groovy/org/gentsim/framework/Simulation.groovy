/*
Copyright © 2009 William D. Back
This file is part of gentsim.

    gentsim is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    gentsim is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with gentsim.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gentsim.framework

import org.gentsim.util.Trace
import org.gentsim.util.Log4JTraceWriter
import org.gentsim.util.Statistics
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import org.gentsim.jms.JMSTraceWriter
import org.gentsim.jms.JMSEventPublisher
import org.gentsim.jms.JMSConstants
import org.gentsim.serialize.ThingToJSONSerializer
import org.gentsim.jms.JMSJSONSystemCommandReceiver

/**
 * The simulation class is the main driver for the simulation, combining the container with the 
 * execution.
 * @author Bill Back
 */
class Simulation extends SimulationContainer {

  /** flag to stop the simulation.  */
  protected final shouldStop = false

  /* Event queues store events for processing.  System event queues go first, then user defined, then all other events. */

  /** event queues for system events */
  protected final EventQueueList systemEventQueues = new EventQueueList()
  /** event queues for user defined events */
  protected final EventQueueList userEventQueues = new EventQueueList()
  /** event queue for all events not captured in other queues */
  protected final TimeEventQueue allOtherEventsQueue = new TimeEventQueue()

  /** Description loader to use to load descriptions. */
  protected final DescriptionLoader descriptionLoader = new FileSystemDescriptionLoader()

  /** Defines a minimum length (in milliseconds) for each cycle, mainly for user in the loop. */
  protected int cycleLength = 0
  
  /** Indicates that the simulation is paused.  */
  protected boolean paused = false

  /** Determines if this is a time stepped simulation or not.  If true it is. */
  private boolean timeStepped = false

  /** Current simulation time. */
  private int currentTime = 1

  /**
   * Creates a new time stepped simulation, loading descriptions from the given location.
   * @param timeStepped Flag to make this time stepped if true.
   */
  Simulation (timeStepped = false) {
    setupSimulation(timeStepped)
  }

  /**
   * Creates a new simulation, loading descriptions from the given location.
   * @param location The location of the scripts to load.
   * @param timeStepped Flag to make this time stepped if true.
   */
  Simulation (String location, timeStepped = false) {
    setupSimulation(false)
    this.descriptionLoader.loadDescriptionsFromLocation(location, this)
  }

  /**
   * Creates a new simulation, loading desriptions from multiple locations.
   * @param locations A list of locations to load from.
   * @param timeStepped Flag to make this time stepped if true.
   */
  Simulation (List locations, timeStepped = false) {
    setupSimulation(timeStepped)
    this.descriptionLoader.loadDescriptionsFromLocations(locations, this)
  }

  /**
   * Performs common actions needed to initialize the simulation.
   */
  private setupSimulation(timeStepped = false) {
    // TODO:  Make this configurable.
    Trace.addTraceWriter(new Log4JTraceWriter())
    addStatistics()

    // Set up the initial system queues.
    this.systemEventQueues.addEventQueue("system.control.*", new TimeEventQueue())
    this.systemEventQueues.addEventQueue("system.status.status", new TimeEventQueue())
    this.systemEventQueues.addEventQueue("system.status.*", new TimeEventQueue())
    this.systemEventQueues.addEventQueue("entity-.*", new TimeEventQueue())

    loadDefaultDescriptions()

    if (timeStepped) this.timeStepped()
  }

  /**
   * Sets the order of user defined events.  These are handled after system events.
   * All other events are handled after these.
   * @param eventTypes An ordered list of event types to handle.
   */
  def setEventOrder (List eventTypes) {
    eventTypes.each { et ->
      this.userEventQueues.addEventQueue(et, new TimeEventQueue())
    }
  }

  /**
   * Loads the default descriptions available to simulations.
   */
  private loadDefaultDescriptions() {
    // Note:  due to a constraint in the JarURLConnection, directories as resources aren't handled.
    //        Each file to be loaded must be listed.
    // Load system events.
    this.descriptionLoader.loadDescriptionsFromLocations ( [
        "/framework/events/EntityCreatedEvent.groovy",
        "/framework/events/EntityDestroyedEvent.groovy",
        "/framework/events/EntityStateChangedEvent.groovy",
        "/framework/events/SystemControlShutdown.groovy",
        "/framework/events/SystemControlStart.groovy",
        "/framework/events/SystemControlPause.groovy",
        "/framework/events/SystemStatusShutdown.groovy",
        "/framework/events/SystemStatusStartup.groovy",
        "/framework/events/SystemStatusStatus.groovy",
        "/framework/events/TimeUpdateEvent.groovy"
      ], this
    )
    // Load system entities
    this.descriptionLoader.loadDescriptionsFromLocations ( [
        "/framework/entities/SystemCommandJMSJSONReceiver.groovy",
        "/framework/entities/SystemEventJMSJSONPublisher.groovy"
      ], this
    )
  }

  /**
   * Sets up the simulation to support the use of JMS.
   * @param url The URL to the JMS instance to connect to.
   */
  def useJMS (String url) {
    Trace.addTraceWriter(new JMSTraceWriter(url))
    Trace.on "jms"
    def pub = newEntity("system.jms.json.publisher")
    pub.jms_connection = new JMSEventPublisher(url, JMSConstants.JMSSystemStatusTopic, new ThingToJSONSerializer())
    def recvr = newEntity("system.jms.json.commandreceiver")
    recvr.jms_connection = new JMSJSONSystemCommandReceiver(url, JMSConstants.JMSSystemControlQueue, this)
  }

  /** Adds statistics to the parent class. */
  def addStatistics () {
    Statistics.instance.start_time         = 0
    Statistics.instance.end_time           = 0
    Statistics.instance.elapsed_time       = 0
    Statistics.instance.number_cycles      = 0
    Statistics.instance.number_events_sent = 0
  }

  /**
   * Sets the simulation to be time stepped. 
   */
  def timeStepped() {
    this.timeStepped = true
    this.systemEventQueues.addEventQueue("time-update", new TimeUpdateEventQueue(newEvent("time-update")))
  }

  def sendStartupEvent () {
    def evt = newEvent("system.status.startup")
    evt.state = (this.paused) ? "paused" : "running"
    evt.time = this.currentTime
    sendEventToEntities(evt)
  }

  /**
   * Continues to run the simulation until told to stop.
   */
  def run() {
    Trace.trace("system", "starting the simulation")
    Statistics.instance.start_time = new Date().getTime()
    sendStartupEvent()
    Thread.start {
      while (!this.shouldStop) {
        if (paused) Thread.sleep 100
        else cycle()
      }
    }
  }

  /**
   * Runs the simulation for the given number of cycles and then shuts down.
   * @param nbrCycles The number of cycles to run before shutting down.
   */
  def run(int nbrCycles) {
    Trace.trace("system", "starting the simulation for ${nbrCycles} cycles")
    Statistics.instance.start_time = new Date().getTime()
    Thread.start {
      sendStartupEvent()
      int cnt = 0
      while (cnt < nbrCycles && !this.shouldStop) {
        if (paused) Thread.sleep 100
        else {
          cycle()
          cnt++
        }
      }
      if (!this.shouldStop) stop() // perform any shutdown.
    }
  }

  /**
   * Performs a single tick of the simulation.  This is separate from process events because
   * other variants of the simulation may want to perform different activities.
   */
  def cycle () {
    def start_time = System.currentTimeMillis()
    Statistics.instance.number_cycles += 1

    Trace.trace "system", "system time: ${this.currentTime}"
    processCurrentEvents()
    sendEntityUpdates()
    sendSystemStatusEvent()
    
    this.currentTime++

    def stop_time = System.currentTimeMillis()
    if ((stop_time - start_time) < cycleLength) Thread.sleep (cycleLength - (stop_time - start_time))
  }

  /**
   * Sends a system status update event.  This is always sent at the end of the current cycle.
   */
  def sendSystemStatusEvent() {

    def statusEvent = newEvent("system.status.status", [
            "state"           : this.paused ? "paused" : "running",
            "number_entities" : this.numberEntities,
            "number_services" : this.numberServices,
            "cycle_length"    : this.cycleLength,
            "timestep"        : this.timeStepped
    ])
    statusEvent.time = currentTime
    this.sendEventToEntities(statusEvent)
  }

  /**
   * Adds an event to the event queue.
   * @param event The event to send.
   * @return this to allow for method chaining.
   */
  def sendEvent (Event event) {
    // entity state change events are buffered to only have one update per cycle.
    if (event.type == "entity-state-changed") {
      bufferEntityChangeEvent(event)
    }
    else {
      // Tries each event queue by priority.
      if (!this.systemEventQueues.addEvent(event))
        if (!this.userEventQueues.addEvent(event))
          this.allOtherEventsQueue.add(event)

      // check for system commands that take place immediately.
      switch (event.description.type) {
        case "system.control.shutdown":
          stop()
          break
        case "system.control.start":
          this.paused = false
          break
        case "system.control.pause":
          this.paused = true
          break
      }
    }
    this
  }

  /** Keeps track of the entity change events. */
  private entityChangeEvents = [:]

  /**
   * Keeps track of all the entity change events so that there is only one for each entity.
   * @param event An entity change event.
   */
  private bufferEntityChangeEvent(event) {
    def ece = entityChangeEvents[event.entity.id]
    if (ece != null) {  // previous state change, so combine all of the updates into a single set.  The most recent value counts.
      event.changed_attributes.addAll(ece.changed_attributes)
      event.changed_attributes = event.changed_attributes.unique()

    }
    entityChangeEvents[event.entity.id] = event
  }

  def sendEntityUpdates () {
    entityChangeEvents.each { id, evt -> this.systemEventQueues.addEvent (evt) }
    entityChangeEvents.clear()
  }

  /**
   * Processes events for the current time, sending to entities and services who requested the event.
   */
  def processCurrentEvents () {
    // this could be done by event changes to a map based on the entity and update the changes.  Then
    // at the end of the cycle send the entity changed events.
    //StopWatch watch = new LoggingStopWatch("Simulation.processCurrentEvents")

    def sendEventsFromQueue = { queue ->
      queue.getEventsForTime(currentTime).each { evt ->
        sendEventToEntities(evt)
      }
    }

    // Send all of the system events first.
    this.systemEventQueues.eachQueue { queue ->
      sendEventsFromQueue(queue)
    }
    // Send the user defined events next.
    this.userEventQueues.eachQueue { queue ->
      sendEventsFromQueue(queue)
    }
    // Send any remaining events in any order.
    sendEventsFromQueue(this.allOtherEventsQueue)
    
    //watch.stop()
  }

  /**
   * Sends the event to all of the entities that are interested.
   * @param event The event to send to interested entities.
   */
  protected sendEventToEntities (event) {
    Trace.trace("events", "Sending ${event.type} ${event.attributes} to entities at time ${event.time}")
    //StopWatch watch = new LoggingStopWatch("Simulation.sendEventToEntities")
    this.getEntitiesWhoHandleEvent (event).each { ent ->
      ent.handleEvent(event)
      Statistics.instance.number_events_sent += 1

    }
    //watch.stop()
  }

  /**
   * Creates a new entity of the given type and sends out the notice to other entities.
   * @param entityType The type of the entity to create.
   * @param attrs Optional attributes to override the defaults.
   * @throws IllegalArgumentException Thrown if the entity type doesn't exist.
   * @return A new entity of the given type.
   */
  def newEntity (entityType, Map attrs = null) throws IllegalArgumentException {
    def entity = super.newEntity(entityType, attrs)

    // Sends event to entities who are interested in this new entity
    def evt = this.newEvent("entity-created", ["entity_type": entity.type, "entity": entity])
    sendEvent(evt)

    entity
  }

  /**
   * Removes the given entity.
   * @param entityId The ID for the entity to remove.
   * @return The entity that was removed or null if it didn't exist.
   */
  def removeEntity (int entityId) {
    def entity = super.removeEntity(entityId)

    // Sends event to entities who are interested in this new entity
    def evt = this.newEvent("entity-destroyed", ["entity_type": entity.type, "entity": entity])
    sendEvent(evt)

    entity 
  }

  /*
   * Stops the simulation.
   */
  def stop() {
    Trace.trace("system", "stopping the simulation")
    Statistics.instance.end_time = new Date().getTime()
    Statistics.instance.elapsed_time = Statistics.instance.end_time - Statistics.instance.start_time
    Statistics.instance.printStatistics()
    def evt = newEvent("system.status.shutdown")
    evt.time = this.currentTime
    sendEventToEntities(evt)
    Trace.trace("system", "exit") // provides a signal for any connections to be closed.
    this.shouldStop = true
  }

  /**
   * Returns true if the simulation is stopped.
   * @return true if the simulation is stopped.
   */
  def isStopped () {
    return this.shouldStop
  }

}
