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

class ContainedEntity extends Entity {

  /** The container of the entity. */
  private SimulationContainer container

  /** 
   * Creates a new contained entity.
   * @param ed The entity description of the entity.
   * @param id The unique id for the entity.
   * @param container The simulation container of the entity.
   * @param attrs Attribute values to override the defaults.
   */
  ContainedEntity (EntityDescription ed, id, container, Map attrs = null) {
    super(ed, id, attrs)
    this.container = container
  }

  /*********** wrapper methods to make life easier **********/

  /**
   * Creates a new entity of the given type.
   * @param type The type of the entity type to create.
   * @param attrs Attribute values to override the defaults.
   */
  def newEntity (type, Map attrs = null) {
    container.newEntity(type, attrs)
  }

  /**
   * Destroys the entity, removing it from the container.
   */
  def destroy () {
    container.removeEntity(this.id)
  }

  /**
   * Posts an event to the container.
   * @param event The event to send.
   * @return this to allow for method chaining
   */
  def sendEvent (Event event) {
    container.sendEvent(event)
    this
  }

  /**
   * Creates a new event with the given attributes.
   * @param type The type of event being created.
   * @param attrs Optional attribute values to set on the event.
   * @return The new event.
   */
  def newEvent (type, Map attrs = null) {
    container.newEvent(type, attrs)
  }

  /**
   * Creates a command.
   * @param type The type of command to create.
   * @param tgt The target of the command.
   * @param attrs Attributes to set on the command.
   * @return The command that was creatcd.
   */
  def newCommand (type, tgt, Map attrs = null) {
    container.newCommand(type, tgt, attrs)
  }

}

