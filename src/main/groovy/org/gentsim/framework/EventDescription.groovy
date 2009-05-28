package org.gentsim.framework

/**
 * This class is used to describe simulation entities.
 */
class EventDescription extends Description {

  /**
   * Creates a new event that can be instantiated in the simulation.
   * @param type The unique type of the event.
   * @param attrs Optional attributes.
   * @throws IllegalArgumentException Thrown if the type is invalid.
   */
  EventDescription (type, Map attrs = null) throws IllegalArgumentException {
    super(type, attrs)
  }

}

