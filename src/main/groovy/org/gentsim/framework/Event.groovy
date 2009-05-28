package org.gentsim.framework

class Event extends Thing {

  int time = 0

  /**
   * Creates an instance of an event.
   * @param ed The event description for the event.
   * @param id The unique id of the event.
   * @param attrs Map of attributes and values.  This allows overwriting of the defaults.
   */
  Event (EventDescription ed, id, Map attrs = null) {
    super(ed, id, attrs)
  }

  /**
   * Creates an instance of an event.
   * @param ed The event description for the event.
   * @param id The unique id of the event.
   * @param time The time the event is to occur.
   * @param attrs Map of attributes and values.  This allows overwriting of the defaults.
   */
  Event (Description ed, id, int time, Map attrs = null) {
    this(ed, id, attrs)
    this.time = time
  }

}

