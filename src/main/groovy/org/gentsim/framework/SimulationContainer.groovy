package org.gentsim.framework

import org.perf4j.*

/**
 * A simulation container owns all of the simulation entities.
 */
class SimulationContainer {

  /** Map of statistics about the simulation execution. */
  public statistics = ["number_entity_descriptions" : 0,
                       "number_service_descriptions": 0,
                       "number_event_descriptions"  : 0,
                       "number_entities_created"    : 0,
                       "number_entities_removed"    : 0,
                       "number_services_created"    : 0,
                       "number_events_created"      : 0,
                       "number_commands_created"    : 0
                      ]

  /** Prints the statistics for the simulation. */
  def printStatistics() { statistics.each { Log.info "${it.key}: ${it.value}" } }

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
  }

  /**
   * This creates a new simulation container.  
   * @param scriptName A file that contains entity descriptions to load.
   */
  SimulationContainer (String scriptName) {
    this.loadDescriptionsFrom(scriptName)
  }

  /**
   * Creates a new simulation container and loads entities from the given scripts.
   * @param scripts List of scripts to load from.
   */
  SimulationContainer (List scripts) {
    this.loadDescriptionsFrom (scripts)
  }

  /**
   * Loads entity descriptions from a variety of scripts.
   * @param scripts The list of script names to load the entity descriptions from.
   */
  def loadDescriptionsFrom (List scripts) {
    scripts.each { loadDescriptionsFrom(it) } 
  }

  /**
   * Loads entity descriptions from a script.  The script can be a directory, in which case an attempt is
   * made to load from children directories.  It can also be any valid name of a resource, in which case that is
   * used.
   * @param scriptName A script name to load from. 
   * @throws FileNotFoundException if the file doesn't exist.
   */
  def loadDescriptionsFrom (String scriptName) {
    Log.info "Attempting to load descriptions from ${scriptName}"

    File file = new File(scriptName)
    def ris = getClass().getResourceAsStream(scriptName)

    if (file.exists() && !file.isDirectory()) { // single file, load it.
      loadDescriptionUsingGroovyScriptEngine(new GroovyScriptEngine(
                                                   file.absolutePath, new GroovyClassLoader()), 
                                             file.name)
    }
    else if (file.exists()) { // directory of files - load each (note, can recurse if child is dir.)
      for (def f in file.list()) {
        loadDescriptionsFrom(scriptName + File.separator + f)
      }
    }
    else if (new File("./${scriptName}").exists()) {  // child of current directory, so load it.
      loadDescriptionsFrom ("./${scriptName}") 
    }
    else if (ris != null) { // valid script name, either file or directory.
      if (scriptName.endsWith("groovy")) { // assume this is a single file
        loadDescriptionUsingGroovyScriptEngine(new GroovyScriptEngine(new ScriptEngineConnector(), 
                                               new GroovyClassLoader()), scriptName)
      }
      // NOTE:  the following does not work with jars and throws an exception.
      else { // treat like a directory.  This will only load immediate resources with .groovy extensions.  No recurse.
        def br = new BufferedReader(new InputStreamReader(ris))
        for (def line = br.readLine(); line != null; line = br.readLine()) {
          if (line.endsWith("groovy")) loadDescriptionsFrom (scriptName + File.separator + line)
        }
      }
    }
    else { // exhausted all possibilities.
      throw new FileNotFoundException ("No file or directory named ${scriptName}")
    }
  }

  /**
   * Determines if any of the standard description locations exist. 
   * Standard locations are entities, services, and events.
   * @param path The location to search.
   * @return true if any of the standard locations exist.
   */
  def standardLocationsExist (String path) {
    new File(path + File.separator + "entities").exists() ||
    new File(path + File.separator + "events").exists() ||
    new File(path + File.separator + "services").exists()
  }

  /**
   * Load from the standard locations.  Standard locations are entities, services, and events.
   * @param path The location to search.
   */
  def loadFromStandardLocations (String path) {
    if (new File("{path}${File.separator}entities").exists()) loadDescriptionsFrom "${path}{File.separator}entities"
    if (new File("{path}${File.separator}events").exists())   loadDescriptionsFrom "${path}{File.separator}events"
    if (new File("{path}${File.separator}services").exists()) loadDescriptionsFrom "${path}{File.separator}services"
  }

  /**
   * Loads a descriptions from a resource.  For this to work, all of the entity descriptions in an
   * individual file must be assigned to individual variables, e.g. myEd = new <type>Description("mytype").
   * This is due to the way the GroovyScriptEngine passes back variables.
   * Note that by using the GroovyScriptEngine, it is theoretically possible to change the entity 
   * description at runtime and see the change, however, this has not been tried.
   * @param scriptName A resource name to load from.  
   * @throws FileNotFoundException if the file doesn't exist.
   */
  private loadDescriptionUsingGroovyScriptEngine(GroovyScriptEngine gse, String scriptName) {
    try {
      Log.info "Running script ${scriptName}"
      Binding b = new Binding()
      gse.run(scriptName, b)
      // search the binding for any entity descriptions that got created.
      for (def var in b.variables) {
        def desc = var.getValue()
        if (desc instanceof ServiceDescription) {
          this.addServiceDescription(desc)
          Log.info "Adding service description ${desc.type}"
        }
        else if (desc instanceof EntityDescription) {
          Log.info "Adding entity description ${desc.type}"
          this.addEntityDescription(desc)
        }
        else if (desc instanceof EventDescription) {
          Log.info "Adding event description ${desc.type}"
          this.addEventDescription(desc)
        }
      }
    }
    catch (InstantiationException ie) { /* ignore */ }
    catch (Exception e) {
      Log.error (e, "Error loading script from ${scriptName}")
    }
  }

  /**
   * Adds the entity description for creating entities.
   * @param ed The entity description.
   */
  def addEntityDescription (ed) {
    entityDescriptions[ed.type] = ed
    statistics.number_entity_descriptions += 1
    ed
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
    def ed = entityDescriptions[entityType]
    if (ed == null) throw new IllegalArgumentException("No entity type ${entityType}.")

    def entity = new ContainedEntity(ed, nextID++, this, attrs)
    this.storeEntity(entity)
    statistics.number_entities_created += 1

    // N*N, but both are proably small N.
    this.services.keySet().each { svcName ->
      ed.services.each { edSvcName ->
        if (svcName == edSvcName) {
          entity.setService(services[svcName])
        }
      }
    }

    entity
  }

  /**
   * Stores entities for retrieval.
   * @param entity The entity to store.
   */
  private def storeEntity (entity) {
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
    for (def ent in this.entitiesById.values()) {
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
    statistics.number_entities_removed += 1

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
   * Adds the service description for creating services.
   * @param sd The service description.
   */
  def addServiceDescription (sd) {
    serviceDescriptions[sd.type] = sd
    statistics.number_service_descriptions += 1
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
    statistics.number_services_created += 1
    svc
  }

  /**
   * Adds the event description for creating events.  
   * @param ed The event description.
   * @return The event description.
   */
  def addEventDescription (ed) {
    eventDescriptions[ed.type] = ed
    statistics.number_event_descriptions += 1
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
    statistics.number_events_created += 1
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
    statistics.number_commands_created += 1
    cmd
  }
}


