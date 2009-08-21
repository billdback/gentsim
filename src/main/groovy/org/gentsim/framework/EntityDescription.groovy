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

import groovy.lang.Closure

/**
 * This class is used to describe simulation entities.
 */
class EntityDescription extends Description {

  final methods                   = [:]   // name : Closure

  /*
   * type : the type of event to handle
   * conditions : Condition objects to evaluate against the event
   * Closure : The actual event to call
   */
  final eventHandlers             = [:]   // type : [[conditions] : Closure]
  final services                  = []    // type of service
  
  /**
   * Creates a new entity that can be instantiated in the simulation.
   * @param type The unique type of the entity.
   * @param attrs Optional set of attributes.
   * @throws IllegalArgumentException Thrown if the type is invalid.
   */
  EntityDescription (String type, Map attrs = null) throws IllegalArgumentException {
    super(type, attrs)
  }

  /**
   * Defines a method for the entity.  This is necessary because the entity
   * is not really a class.
   * @param name The name of the method.
   * @param c The body of the method.  
   */
  def method (String name, Closure c) {
    this.methods[name] = c
  }

  /**
   * Defines a handler for events.
   * @param type The type of the event to handle.
   * @param closure The closure to be called.  This must accept an event.
   */
  def handleEvent(String type, Closure c) {
    this.handleEvent(type, (Condition)null, c)
  }

  /**
   * Defines a handler for events.
   * @param type The type of the event to handle.
   * @param conditions A map of conditions that must all be satisfied.
   * @param closure The closure to be called.  This must accept an event.
   */
  def handleEvent(String type, Map conditions, Closure c) {
    def cond = new Condition().matchAll(conditions)
    //c.delegate = delegate // sets the delegate on the closure to be the same as this delegate.
    //c.setResolveStrategy(Closure.DELEGATE_ONLY)
    this.handleEvent(type, cond, c)
  }

  /**
   * Defines a handler for events.
   * @param type The type of the event to handle.
   * @param conditions The condition to match on the event to be called.
   * @param closure The closure to be called.  This must accept an event.
   */
  def handleEvent(String type, Condition condition, Closure c) {
    if (this.eventHandlers[type] == null) this.eventHandlers[type] = []
    this.eventHandlers[type] << [condition, c] // add another condition
  }

  def handleTimeUpdate (Closure c) {
    handleEvent ("time-update", c)
    this.handleEvent("time-update")  { evt -> 
      c.delegate = delegate // sets the delegate on the closure to be the same as this delegate.
      c.setResolveStrategy(Closure.DELEGATE_ONLY)
      c(evt.time) 
    }
  }

  /**
   * Returns true if the entity handles the given event.  This matches against the appropriate 
   * event handlers and conditions to tell. 
   * TODO add similar code to entity to handle the event.
   */
  def handlesEvent (Event event) {
    def allthem = this.eventHandlers.findAll { evth -> event.type.matches(evth.key) }
    def result = false
    for (listOfHandlers in allthem.values()) {
      for (pair in listOfHandlers) {
        def conditions = pair[0]
        // Closure c = pair[1]
        if (conditions == null) {
          return true // just interested in the event.
        }
        else {
          for (Condition condition in conditions) {
            if (condition.matches(event)) {
              result = true
              break
            }
          }
        }
      }
    }
    return result
  }

  /**
   * Defines a handler for newly created entities.
   * @param type The type of the entity to handle.
   * @param closure The closure to be called.  This must accept an entity.
   */
  def handleEntityCreated(String type, Closure c) {
    // entity handlers expect the actual entity (or proxy)
    this.handleEvent("entity-created", ["entity_type": type]) { evt -> 
      c.delegate = delegate // sets the delegate on the closure to be the same as this delegate.
      c.setResolveStrategy(Closure.DELEGATE_ONLY)
      c(evt.entity) 
    }
  }

  /**
   * Defines a handler for destroyed entities.
   * @param type The type of the entity to handle.
   * @param closure The closure to be called.  This must accept an entity.
   */
  def handleEntityDestroyed(String type, Closure c) {
    this.handleEvent("entity-destroyed", ["entity_type": type]) { evt -> 
      c.delegate = delegate // sets the delegate on the closure to be the same as this delegate.
      c.setResolveStrategy(Closure.DELEGATE_ONLY)
      c(evt.entity) 
    }
  }

  /**
   * Defines a handler for entity state changes where it doesn't matter which attribute.
   * @param type The type of the entity to handle.
   * @param closure The closure to be called.  This must accept an entity.
   */
  def handleEntityStateChanged(String type, Closure c) {
    this.handleEvent("entity-state-changed", ["entity_type": type], { evt -> c(evt.entity) })
  }

  /**
   * Defines a handler for entity state changes.
   * @param type The type of the entity to handle.
   * @param attributeName The type of the entity attribute.
   * @param closure The closure to be called.  This must accept an entity.
   */
  def handleEntityStateChanged(String type, String attributeName, Closure c) {
    this.handleEntityStateChanged(type, [attributeName], c)
  }

  /**
   * Defines a handler for entity state changes.
   * @param type The type of the entity to handle.
   * @param attributeNames List of the type of the entity attribute.
   * @param closure The closure to be called.  This must accept an entity.
   */
  def handleEntityStateChanged(String type, List attributeNames, Closure c) {
    def cond = new Condition().match({ evt ->
                                       evt.attributes.entity_type.matches(type) && 
                                       evt.attributes.changed_attributes.findAll { a -> attributeNames.findAll { av -> a.matches(av) } } != []
                                     })
    this.handleEvent("entity-state-changed", cond) { evt -> 
      c.delegate = delegate // sets the delegate on the closure to be the same as this delegate.
      c.setResolveStrategy(Closure.DELEGATE_ONLY)
      c(evt.entity) 
    }
  }

  /**
   * Indicates that an entity will be using a particular service.
   * @param service The type of the service to use.
   */
  def usesService (service) {
    this.services << service
  }

}

