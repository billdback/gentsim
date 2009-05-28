package org.gentsim.framework

import org.perf4j.*

/**
 * The simulation class is the main driver for the simulation, combining the container with the 
 * execution.
 */
class Simulation extends SimulationContainer {

  /** flag to stop the simulation.  */
  protected final shouldStop = false

  /** event queue for events. */
  protected final eventQueue = new TimeEventQueue()

  /**
   * Creates a new time stepped simulation, loading descriptions from the given location.
   */
  Simulation () {
    super()
    setupSimulation()
  }

  /**
   * Creates a new simulation, loading descriptions from the given location.
   * @param location The location of the scripts to load.
   */
  Simulation (String location) {
    super(location)
    setupSimulation()
  }

  /**
   * Creates a new simulation, loading desriptions from multiple locations.
   * @param locations A list of locations to load from.
   */
  Simulation (List locations) {
    super(locations)
    setupSimulation()
  }

  /**
   * Performs common actions needed to initialize the simulation.
   */
  private setupSimulation() {
    addStatistics()
    loadDefaultDescriptions()
  }

  /**
   * Loads the default descriptions available to simulations.
   */
  def loadDefaultDescriptions() {
    // Note:  due to a constraint in the JarURLConnection, directories as resources aren't handled.
    //        Each file to be loaded must be listed.
    this.loadDescriptionsFrom ( [
        "/framework/events/EntityCreatedEvent.groovy",
        "/framework/events/EntityDestroyedEvent.groovy",
        "/framework/events/EntityStateChangedEvent.groovy",
        "/framework/events/SystemShutdown.groovy",
        "/framework/events/SystemStartup.groovy",
        "/framework/events/TimeUpdateEvent.groovy"
      ]
    )
  }

  /** Adds statistics to the parent class. */
  def addStatistics () {
    statistics.putAll(["start_time"         : 0,
                       "end_time"           : 0,
                       "elapsed_time"       : 0,
                       "number_cycles"      : 0,
                       "number_events_sent" : 0
                      ])
  }

  /**
   * Continues to run the simulation until told to stop.
   */
  def run() {
    Log.info "starting the simulation"
    statistics.start_time = new Date().getTime()
    Thread.start {
      sendEventToEntities(newEvent("system.startup"))
      while (!this.shouldStop) {
        cycle()
      }
    }
  }

  /**
   * Runs the simulation for the given number of cycles and then shuts down.
   * @param nbrCycles The number of cycles to run before shutting down.
   */
  def run(int nbrCycles) {
    Log.info "starting the simulation for ${nbrCycles} cycles"
    statistics.start_time = new Date().getTime()
    Thread.start {
      sendEventToEntities(newEvent("system.startup"))
      for (int cnt = 0; cnt < nbrCycles && !this.shouldStop; cnt++) {
        cycle()
      }
      if (!this.shouldStop) stop() // perform any shutdown.
    }
  }

  /**
   * Performs a single tick of the simulation.  This is separate from process events because
   * other variants of the simulation may want to perform different activities.
   */
  def cycle () {
    statistics.number_cycles += 1
    processCurrentEvents()
  }

  /**
   * Adds an event to the event queue.
   * @param event The event to send.
   * @return this to allow for method chaining.
   */
  def sendEvent (Event event) {
    this.eventQueue << event
    this
  }

  /**
   * Processes events for the current time, sending to entities and services who requested the event.
   */
  def processCurrentEvents () {
    // TODO figure out how to aggregate changes to the entity to reduce the number of messages being sent.
    //StopWatch watch = new LoggingStopWatch("Simulation.processCurrentEvents")
    this.eventQueue.nextTimeEvents.each { evt ->
      sendEventToEntities (evt)
    }
    //watch.stop()
  }

  /**
   * Sends the event to all of the entities that are interested.
   * @param event The event to send to interested entities.
   */
  protected sendEventToEntities (event) {
    //StopWatch watch = new LoggingStopWatch("Simulation.sendEventToEntities")
    def old_attributes = [:]
    super.getEntitiesWhoHandleEvent (event).each { ent ->
      old_attributes.clear()
      old_attributes.putAll(ent.attributes)
      ent.handleEvent(event)
      statistics.number_events_sent += 1

      // see if anything changed.
      def changed_attr = []
      old_attributes.each { attr -> // doesn't really handle adding attributes
        if (old_attributes[attr.key] != ent.attributes[attr.key]) changed_attr << attr.key
      }
      if (changed_attr != []) {
        def escevt = super.newEvent("entity-state-changed", 
          ["entity_type": ent.type, "entity": ent, "changed_attributes": changed_attr])
        sendEvent(escevt)
      }
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
    def evt = super.newEvent("entity-created", ["entity_type": entity.type, "entity": entity])
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
    def evt = super.newEvent("entity-destroyed", ["entity_type": entity.type, "entity": entity])
    sendEvent(evt)

    entity 
  }

  /**
   * Stops the simulation. 
   */
  def stop() {
    Log.info "stopping the simulation"
    statistics.end_time = new Date().getTime()
    statistics.elapsed_time = statistics.end_time - statistics.start_time
    printStatistics()
    sendEventToEntities(newEvent("system.shutdown"))
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
