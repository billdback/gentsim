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

import org.gentsim.serialize.ThingToStringSerializer

/**
 * Provides the base definition of a thing in the simulation.
 * @author Bill Back
 */
class Thing implements Serializable {

  /** Unique id for the thing. */
  final id

  /** Description of the thing. */
  final Description description

  /** Attribute values for this thing. */
  final attributes = [:] as ObservableMap

  /** Parameters for all things. */
  static parameters = [:] // [entityName : parameterName : parameterValue]

  /**
   * Creates an instance of a thing.
   * @param td The descriptor for the thing.
   * @param id The unique id of the thing.
   * @param attrs Map of attributes and values.  This allows overwriting of the defaults.
   */
  Thing (Description td, id, Map attrs = null) {
    if (td == null) throw new IllegalArgumentException ("Attempting to create a thing without a description")

    this.description = td
    this.id   = id

    // If this is the first of the type, then create the parameters.
    def parameterMap = Thing.parameters[this.type]
    if (parameterMap == null) { // new parameter
      parameterMap = [:] as ObservableMap
      Thing.parameters[this.type] = parameterMap
    }

    setAttributes(td)
    setParameters(td)

    if (attrs) setAttributes(attrs)
  }

  /**
   * This will attempt to return the value for a property.  First, it will check for attributes 
   * and return it if found.  Next, it will check for a parameter.  If that is not found, return null.
   * @param name The name of the property.
   */
  def propertyMissing (String name, value) {
    if (value != null) { // setting property
      if (this.getAttribute(name))      this.setAttribute(name, value)
      else if (this.getParameter(name)) this.setParameter(name, value)
      else                              this.setAttribute(name, value)
      return value // just to make this conform with accepted assignment protocol
    }
    else { // getting the property
      if (this.getAttribute(name) != null) {
        return getAttribute(name) 
      }
      else {
        return this.getParameter(name)
      }
    }
  }

  /**
   * Adds the attributes and sets the initial values.
   * @param td The thing's description.
   */
  private def setAttributes (Description td) {
    for (a in td?.attributes) {
      this.setAttribute(a.key, a.value)
    }
  }

  /**
   * Sets the attributes given in construction.
   * @param attrs The attributes to set.
   */
  private def setAttributes (Map attrs) {
    for (a in attrs) {
      this.setAttribute(a.key, a.value)
    }
  }

  /**
   * Adds the parameters and sets the initial values.
   * @param td The thing's description.
   */
  private def setParameters (Description td) {
    for (a in td?.parameters) {
      this.setParameter(a.key, a.value)
    }
  }

  /**
   * Returns the thing's type.
   */
  def getType () {
    return this.description.type
  }

  /**
   * Sets the attribute value for the thing.
   * @param name The name of the attribute to set.
   * @param value The value to set the attribute to.  
   * If this is a closure, it will be evaluated and the result set as the value.  
   *   No values are passed to the closure.
   */
  def setAttribute (name, value) {
    if (value instanceof Closure) {
      value = value()
    }

    attributes[name] = value
  }

  /**
   * Returns the value of an attribute.  Note:  this can also be retrieved using <thing>.attribute.name
   * @param name The name of the attribute to get.
   * @return The value of the attribute.
   */
  def getAttribute (name) {
    return attributes[name]
  }

  /**
   * Sets the parameter value for the entity.
   * @param name The name of the parameter to set.
   * @param value The value to set the parameter to.
   */
  def setParameter (name, value) {
    def parameterMap = Thing.parameters[this.type]
    if (value instanceof Closure) {
      value = value()
    }

    parameterMap[name] = value
  }

  /**
   * Returns the value of an parameter.  Note:  this can also be retrieved using <entity>.parameters.name
   * @param name The name of the parameter to get.
   * @return The value of the parameter.
   */
  def getParameter (name) {
    try {
      return Thing.parameters[this.type][name]
    }
    catch (Exception e) {
      //e.printStackTrace()
    }
    return null
  }

  private stringSerializer = new ThingToStringSerializer()

   /**
    * Returns a readable version of the Thing that shows parameters and attributes.  Useful for trace and log messages.
    * @return A readable version of the Thing that shows parameters and attributes.
   */
  String toString() {
    this.stringSerializer.serializeThing(this)
  }
  
}

