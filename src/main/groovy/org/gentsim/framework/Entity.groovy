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

import java.beans.*

class Entity extends Thing {

  /** Service instances used by this entity. */
  final services = [:] as ObservableMap

  /**
   * Creates an instance of a base entity.
   * @param ed The entity descriptor for the entity.
   * @param id The unique id of the entity.
   * @param attrs Map of attributes and values.  This allows overwriting of the defaults.
   */
  Entity (EntityDescription ed, id, Map attrs = null) {
    super(ed, id, attrs)
  }

  /**
   * Called when a method isn't defined.  This will call a method on the entity.
   * @param name The name of the method to call.
   * @param args The arguments to the method.
   * @throws MissingMethodException if called for an unknown class.
   */
  def invokeMethod(String name, args) {
    Closure c = this.description.methods[name]
    if (c == null) {
      throw new MissingMethodException (name, this.class, [])
    }
    c.delegate = this
    c.setResolveStrategy(Closure.DELEGATE_ONLY)
    if (args.size() == 1) args = args[0] // Prevents the handler from needing to unpack and looks more natural.
    c(args)
  }

  /**
   * Called to allow the entity to handle an event.
   * @param type The type of the event to handle.
   * @throws MissingMethodException Thrown if there is no handler for the given type.
   */
  def handleEvent (Event event) {
    Closure c = null
    def allthem = description.eventHandlers.findAll { evth -> event.type.matches(evth.key) }
    def result = false
    for (listOfHandlers in allthem.values()) {
      for (pair in listOfHandlers) {
        def conditions = pair[0]
        if (conditions == null) {
          c = pair[1]
        }
        else {
          for (Condition condition in conditions) {
            if (condition.matches(event)) {
              c = pair[1]
              break
            }
          }
        }
      }
    }

    if (c) {
      c.delegate = this
      c.setResolveStrategy(Closure.DELEGATE_ONLY)
      c(event)
    }
    else {
      throw new NoSuchMethodException ("Entity ${this.type} does not handle event ${event.type}")
    }
  }

  /**
   * Sets a service that the entity uses.
   * @param The service to use.
   */
  def setService (service) {
    services[service.type] = service
  }

  /**
   * This will attempt to return the value for a property.  If the super class doesn't find it, check to 
   * see if it's a service.
   */
  def propertyMissing (String name, value = null) {
    def val = super.propertyMissing(name, value)

    if (val == null) { // not found so far, check services
      val = services[name]
    }
    val
  }

}

