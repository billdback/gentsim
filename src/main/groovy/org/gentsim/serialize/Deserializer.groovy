/*
Copyright © 2010 William D. Back
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
package org.gentsim.serialize

import org.gentsim.framework.Event
import org.gentsim.framework.Simulation

/**
 * Base class for classes that deserialize to simulation things.  To create a simulation thing requires descriptions
 * from the simulation, so classes need to have a reference to the simulation.
 * @author Bill Back
 */
public abstract class Deserializer {

  /**
   * The simulation that the deserializer uses to create things.
   */
  protected Simulation simulation;

  /**
   * Converts a formatted version of an event to a simulation event.
   * Currently there is no rule specified on how the ID or time is set, if at all, in the serialized format.  Unless otherwise
   * stated it is most likely not set and this event should be sent to the simulation (setting a new ID and time) as normal.
   * @param s The serialized format of the event.
   * @return A new event.
   */
  abstract Event deserializeEvent(String s) throws DeserializationException;
}