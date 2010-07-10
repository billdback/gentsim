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
import org.gentsim.util.Statistics

/**
 * A simulation container owns all of the simulation entities.
 * @author Bill Back
 */
class SimulationContainer {

  /** Keeps track of the next ID for this container. */
  protected nextID = 1

  /** Keeps track of all of the entity descriptions */
  protected entityDescriptions = [:]

  /** All of the entities mapped by id. */
  protected entitiesById = [:]
  // TODO Add additional mappings based on how the entities are looked up.

  /** Keeps track of all of the service descriptions */
  protected serviceDescriptions = [:]

  /** Only a single instance of a service is stored and it is always returned. */
  protected services = [:]

  /** All of the event descriptions mapped by type. */
  protected eventDescriptions = [:]

  /**
   * This creates a new simulation container.  
   */
  SimulationContainer () {
    Trace.on("system") // start with on by default.
    setupStats()
    expandClasspath()
  }

  /**
   * Sets up the statistics used by the container.
   */
  def setupStats() {
    Statistics.instance.number_entity_descriptions  = 0
    Statistics.instance.number_service_descriptions = 0
    Statistics.instance.number_event_descriptions   = 0
    Statistics.instance.number_entities_created     = 0
    Statistics.instance.number_entities_removed     = 0
    Statistics.instance.number_services_created     = 0
    Statistics.instance.number_events_created       = 0
    Statistics.instance.number_commands_created     = 0
  }

  /**
   * Expands the classpath to add predefined locations and .jars
   */
  def expandClasspath () {
    Trace.trace("system", "Expanding the classpath")
    def cl = this.class.classLoader?.rootLoader
    if (cl) {
      File f = new File(".")
      def lib = f.absolutePath + "/lib/"
      Trace.trace("system", "Adding ${lib} to the classpath")
      cl.addURL(new URL("file://${lib}"))
    }

    File f = new File ("./lib")
    def jarPattern = ~/.*\.jar/
    if (cl && f.exists()) f.eachFileMatch(jarPattern) {
      def url = "file://${it.absolutePath}"
      Trace.trace("system", "Adding ${url} to the classpath")
      cl.addURL(new URL(url))
    }
    // show the URLs in the classpath
    if (cl) cl.getURLs().each { Trace.trace("system", "url:  ${it}")}
  }

  /**
   * Adds the entity description for creating entities.
   * @param ed The entity description.
   */
  def addEntityDescription (ed) {
    entityDescriptions[ed.type] = ed
    Statistics.instance.number_entity_descriptions += 1
    ed
  }

  /**
   * Returns all of the existing entity descriptions.
   */
  def Map getEntityDescriptions () {
    this.entityDescriptions
  }

  /**
   * Returns the entity description with the given type.
   * @param type The type of the entity.
   * @return The entity description of null if it doesn't exist.
   */
  def getEntityDescription(type) {
    //for (def n in entityDescriptions) println "${n}"
    return entityDescriptions[type]
  }

  /**
   * Returns true if the entity description exists.
   * @param type The type of the entity.
   * @return True if the entity exists, false otherwise.
   */
  def hasEntityDescription(type) {
    return entityDescriptions[type] != null
  }

  /**
   * Returns all of the entities who handle the type of event.
   * @param type The type of event.
   * @return All of the entities who handle the type of event.
   */
  def getEntitiesWhoHandleEvent (event) {
    //StopWatch watch = new LoggingStopWatch("getEntitiesWhoHandleEvent(${event.type}")
    // TODO improve the performance.
    def entities = []
    this.entityDescriptions.values().findAll({ it.handlesEvent(event) } ).each {
      entities.addAll(this.getEntitiesOfType(it.type))
    }
    this.serviceDescriptions.values().findAll({ it.handlesEvent(event) } ).each {
      entities << this.getService(it.type) // only one service of a given type
    }

    //watch.stop()
    
    entities
  }

  /**
   * Creates a new entity of the given type.
   * @param entityType The type of the entity to create.
   * @param attrs Optional attributes to override the defaults.
   * @throws IllegalArgumentException Thrown if the entity type doesn't exist.
   * @return A new entity of the given type.
   */
  def newEntity (entityType, Map attrs = null) throws IllegalArgumentException {
    EntityDescription ed = entityDescriptions[entityType]
    if (ed == null) throw new IllegalArgumentException("No entity type ${entityType}.")

    def entity = new ContainedEntity(ed, nextID++, this, attrs)
    try { entity.init() } catch (MissingMethodException mme) { /* ignore - not implemented */ }
    this.storeEntity(entity)
    Statistics.instance.number_entities_created += 1

    // N*N, but both are probably small N.
    this.services.keySet().each { svcName ->
      ed.services.each { edSvcName ->
        if (svcName == edSvcName) {
          entity.setService(services[svcName])
        }
      }
    }

    Trace.trace("entities", "Created entity of type ${entityType} with id ${entity.id} ${entity.attributes}")
    entity
  }

  /**
   * Stores entities for retrieval.
   * @param entity The entity to store.
   */
  protected def storeEntity (entity) {
    this.entitiesById[entity.id] = entity
  }

  /**
   * Returns all of the entities of the given type.
   * TODO optimize
   * @param type The type.
   * @return A list of entities with the given type.
   */
  def getEntitiesOfType (type) {
    def ents = []
    for (ent in this.entitiesById.values()) {
      if (ent.type == type) {
        ents << ent
      }
    }
    ents
  }

  /**
   * Returns an entity with the given id.
   * @param id for an entity.
   * @return A entity or null if the entity doesn't exist.
   */
  def getEntityWithId (id) {
    this.entitiesById[id]
  }

  /**
   * Returns true if there is an entity with the given id.
   * @param id for an entity.
   * @return true if the entity exist.
   */
  def hasEntityWithId (id) {
    getEntityWithId(id) != null
  }

  /**
   * Removes the given entity.
   * @param entityId The ID for the entity to remove.
   * @return The entity that was removed or null if it didn't exist.
   */
  def removeEntity (int entityId) {
    def entity = this.entitiesById[entityId]
    if (entity == null) throw new IllegalArgumentException ("Attempting to remove unknown entity with id ${entityId}")
    this.entitiesById.remove(entityId)
    Statistics.instance.number_entities_removed += 1

    Trace.trace ("entities", "Removing entity of type ${entity.description.type} and id ${entity.id}")
    entity
  }

  /**
   * Removes the given entity.
   * @param entity The entity to remove.
   * @return The entity that was removed or null if it didn't exist.
   */
  def removeEntity (entity) {
    this.removeEntity(entity.id)
  }


  /**
   * Returns the number of entities currently being stored in the container.
   * @return The number of entities currently being stored in the container.
   */
  def getNumberEntities () {
    this.entitiesById.size()
  }

  /**
   * Adds the service description for creating services.
   * @param sd The service description.
   */
  def addServiceDescription (sd) {
    serviceDescriptions[sd.type] = sd
    Statistics.instance.number_service_descriptions += 1
    sd
  }

  /**
   * Returns the service description with the given type.
   * @param type The type of the service.
   * @return The service description or null if it doesn't exist.
   */
  def getServiceDescription(type) {
    serviceDescriptions[type]
  }

  /**
   * Returns the service with the given type or null if it doesn't exist.
   * @param type The type of the service.
   */
  def getService(type) {
    def svc = services[type]
    if (!svc) svc = newService(type)
    svc
  }

  /**
   * Returns all of the existing service descriptions.
   */
  def Map getServiceDescriptions () {
    this.serviceDescriptions
  }


  /**
   * Returns the number of services currently contained.
   */
  def getNumberServices () {
    this.serviceDescriptions.size()
  }

  /**
   * Creates a service and stores in the list of services.  
   * @param type The type of service to create.
   * @return The service that was created.
   */
  def newService (type) {
    def svc = services[type]
    if (!svc) {
      svc = new Service(serviceDescriptions[type])
      services[type] = svc

      // Connect this service with all of the entities who want it. 
      entityDescriptions.values().each { ed ->
        if (ed.services.contains(type)) {
          this.getEntitiesOfType (ed.type).each { e ->
            e.setService(svc)
          }
        }
      }
    }
    Statistics.instance.number_services_created += 1
    svc
  }

  /**
   * Adds the event description for creating events.  
   * @param ed The event description.
   * @return The event description.
   */
  def addEventDescription (ed) {
    eventDescriptions[ed.type] = ed
    Statistics.instance.number_event_descriptions += 1
    ed
  }

  /**
   * Returns the event description with the given type.
   * @param type The type of the event.
   * @return The event description or null if it doesn't exist.
   */
  def getEventDescription(type) {
    eventDescriptions[type]
  }

  /**
   * Returns true if the simulation container has a description for the given event type.
   * @param type The type of event.
   * @return True if the event description exists.
   */
  def hasEventDescription(type) {
    this.getEventDescription(type) != null
  }

  /**
   * Creates a event.
   * @param type The type of the event.
   * @param attrs Attributes to set on the event.
   * @return The event that was created.
   */
  def newEvent (type, Map attrs = null) {
    def evt = new Event(this.getEventDescription(type), nextID++, attrs)
Trace.trace "debug", "adding to number_events_created with current value ${Statistics.instance}"
    Statistics.instance.number_events_created += 1
    evt
  }

  /**
   * Creates a command.
   * @param type The type of command to create.
   * @param tgt The target of the command.
   * @param attrs Attributes to set on the command.
   * @return The command that was creatcd.
   */
  def newCommand (type, tgt, Map attrs = null) {
    def cmd = new Command(this.getEventDescription(type), nextID++, tgt, attrs)
    Statistics.instance.number_commands_created += 1
    cmd
  }

}


