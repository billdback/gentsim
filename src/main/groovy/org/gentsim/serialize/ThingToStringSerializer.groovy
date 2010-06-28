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

import org.gentsim.framework.Thing
import org.gentsim.framework.Event

/**
 * Converts a thing to a formatted string.
 * @author Bill Back
 */
class ThingToStringSerializer implements Serializer {

  /**
   * Serializes an event to a String.
   * @param e The event to serialize.
   * @return The Event as a serialized String.
   */

  String serializeEvent(Event e) {
    this.serializeThing(e)
  }

  /**
   * Generically serializes a Thing.  Currently all Things are converted to Strings the same way.
   * @param t The Thing to serialize.
   * @return A String version of the Thing.
   */
  String serializeThing (Thing t) {
    GString str = "$t.description.type($t.id) -"

    str += " parameters: [" + nameValues(t.parameters[t.type]) + "]"
    str += " attributes: [" + nameValues(t.attributes) + "]"

    str.toString()
  }

  /**
   * Converts the name value pairs to the proper format for the string.
   * @param nameValues The name values to convert.  Must support .each
   * @return A string representation of the name value pairs.
   */
  String nameValues (def nameValues) {
    String str = ""
    boolean first = true
    nameValues.each { name, value ->
      if (!first) {
        str += ", "
      }
      else first = false

      str += "${name} : ${value}"
    }
    str
  }
}
