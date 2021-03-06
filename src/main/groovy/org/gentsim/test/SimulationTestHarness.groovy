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
package org.gentsim.test

import org.gentsim.framework.*

/**
 * The test simulation provides a harness for testing simulation components.  It eliminates the 
 * coordiantion and allows calls to be sent directly to entities and services to test their behavior.
 * Services and entities created with the harness to not automatically get connected, so if that is needed
 * for a test, then the author will need to manually connect the classes or, ideally, provide mock behavior.
 * Note that this overwrites many of the simulation container methods, so care must be taken to 
 * keep this class coordinated with the SimulationContainer
 * @author Bill Back
 */
class SimulationTestHarness extends Simulation {

  /**
   * Creates a new simulation test harness.
   */
  SimulationTestHarness () {
    super()
  }

  /**
   * Creates a new simulation test harness.
   */
  SimulationTestHarness (String location) {
    super(location)
  }

  /**
   * Creates a new simulation test harness, loading desriptions from multiple locations.
   * @param locations A list of locations to load from.
   */
  SimulationTestHarness (List locations) {
    super(locations)
  }

  /**
   * Creates a new entity of the given type.
   * @param entityType The type of the entity to create.
   * @param attrs Optional attributes to override the defaults.
   * @throws IllegalArgumentException Thrown if the entity type doesn't exist.
   * @return A new entity of the given type.
   */
  def newEntity (entityType, Map attrs = null) throws IllegalArgumentException {
    def ed = super.getEntityDescription(entityType)
    if (ed == null) throw new IllegalArgumentException("No entity type ${entityType}.")

    ContainedEntity entity = super.newEntity(entityType, attrs)
    entity.owner = this
    this.storeEntity(entity)
    entity
  }

  /**
   * Creates a service and stores in the list of services.
   * @param type The type of service to create.
   * @return The service that was created.
   */
  def newService (type) {
    def svc = new Service(this.getServiceDescription(type))
    this.services[type] = svc
    svc
  }

  /**
   * Sends an event to an entity under test.
   * @param entity The test entity to send the event to.
   * @param type The type of event to send.
   * @param attrs A map of attributes to send as part of the event.
   */
  def sendEventTo (entity, type, attrs = null) {
    def evtd = new EventDescription(type, attrs)
    def evt = new Event(evtd, 1)

    entity.handleEvent(evt)
  }

  /**
   * Invokes the entity created handler with the new entity.  
   * @param entity The entity to recieve the new entity.
   * @param newEntity The new entity to use.
   */
  def sendCreatedEntityTo (entity, newEntity) {
    entity.handleEvent(new Event(new EventDescription("entity-created", 
                                                      ["entity_type": newEntity.type, "entity" : newEntity]), 
                                 1))
  }

  /**
   * Invokes the entity destroyed handler with the new entity.  
   * @param entity The entity to recieve the new entity.
   * @param destroyedEntity The destroyed entity.
   */
  def sendDestroyedEntityTo (entity, destroyedEntity) {
    entity.handleEvent(new Event(new EventDescription("entity-destroyed", 
                                                      ["entity_type": destroyedEntity.type, "entity" : destroyedEntity]), 
                                 1))
  }

  /**
   * Invokes the entity changed handler with the new entity.  
   * @param entity The entity to recieve the new entity.
   * @param changedEntity The changed entity.
   * @param changedAttrs A list of attributes that were changed.
   */
  def sendChangedEntityTo (entity, changedEntity, List changedAttrs) {
    entity.handleEvent(new Event(new EventDescription("entity-state-changed", 
                                                      ["entity_type": changedEntity.type, 
                                                       "entity" : changedEntity, 
                                                       "changed_attributes": changedAttrs
                                                      ]
                                                     ), 
                                 1))
  }

  /**
   * Sends a time update event to the entity.
   * @param time The tie to send in the update.
   */
  def sendTimeUpdateTo (entity, time) {
    def evt = new Event(new EventDescription("system.time-update"), 1)
    evt.time = time
    entity.handleEvent(evt)
  }

  /**
   * Map of all events that were sent.  The key is the type of event and the
   * value is a list of events of that type that were sent.
   */
  private sentEvents = [:]

  /**
   * Adds an event to the event queue.
   * @param event The event to send.
   * @return this to allow for method chaining.
   */
  def sendEvent (Event event) {
    super.sendEvent(event)
    def events = sentEvents[event.description.type]
    if (events == null) {
      events = []
      sentEvents[event.description.type] = events
    }
    events << event
  }

  /**
   * Return true if an event with the given name was sent.  This is 
   * doesn't currently provide a lot of detail, but is helpful for limited testing.
   * @param eventType The type of event.
   * @return true if an event of the given type was ever sent.
   */
  boolean eventSent (String eventType) {
    sentEvents[eventType] != null
  }
}

