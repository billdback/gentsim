package org.gentsim.test

import org.gentsim.framework.*

/**
 * The test simulation provides a harness for testing simulation components.  It eliminates the 
 * coordiantion and allows calls to be sent directly to entities and services to test their behavior.
 * Services and entities created with the harness to not automatically get connected, so if that is needed
 * for a test, then the author will need to manually connect the classes or, ideally, provide mock behavior.
 * Note that this overwrites many of the simulation container methods, so care must be taken to 
 * keep this class coordinated with the SimulationContainer
 */
class SimulationTestHarness extends SimulationContainer {

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
    def ed = super.entityDescriptions[entityType]
    if (ed == null) throw new IllegalArgumentException("No entity type ${entityType}.")

    def entity = new ContainedEntity(ed, super.nextID++, this, attrs)
    super.storeEntity(entity)
    entity
  }

  /**
   * Creates a service and stores in the list of services.
   * @param type The type of service to create.
   * @return The service that was created.
   */
  def newService (type) {
    def svc = new Service(super.serviceDescriptions[type])
    super.services[type] = svc
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
    def evt = new Event(new EventDescription("time-update"), 1)
    evt.time = time
    entity.handleEvent(evt)
  }

}

