package org.gentsim.framework

/**
 * This class is used to describe simulation entities.
 */
class Description {

  final type
  final attributes                = [:]   // name : value
  final parameters                = [:]   // name : value
  
  /**
   * Creates a new description that can be instantiated in the simulation.
   * @param type The unique type of the base.
   * @param attrs Optional map of attributes.
   * @throws IllegalArgumentException Thrown if the type is invalid.
   */
  Description (type, Map attrs = null) throws IllegalArgumentException {
    if (type == null || type == "") {
      throw new IllegalArgumentException("All descriptions must have a non-blank type.")
    }
    this.type = type
    if (attrs != null) this.setAttributes(attrs)
  }

  /**
   * Defines an attribute for the base.
   * @param name The name of the attribute.
   * @param value An optional, initial value for the attribute.  This will be the
   *   default value for all new instances of this type.  This can be a closure that
   *   will be called when setting the value.
   */
  def attribute (name, value = null) {
    attributes[name] = value
    this
  }

  /**
   * This will either set or get an attribute.  This is a shortcut to make the descriptions more readable.
   */
  def propertyMissing (String name, value = null) {
    (value == null) ? attributes[name] : (attributes[name] = value)
  }

  /**
   * Adds several attributes.
   * @param m A map of attributes with key being the name and value being the value.
   */
  def setAttributes (Map m) {
    m.each { k, v -> this.attribute (k, v) }
    attributes
  }

  /**
   * Defines a parameter for the base.
   * @param name The name of the parameter.
   * @param value The value of the parameter.  This will be the same value for all
   *   instances of the base.
   */
  def parameter (name, value) {
    parameters[name] = value
    this
  }

}

