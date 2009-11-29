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

class Event extends Thing implements Serializable {

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

