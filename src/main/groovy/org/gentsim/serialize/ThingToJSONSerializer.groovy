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
 * Converts a thing to JSON.
 * @author Bill Back
 */
class ThingToJSONSerializer implements Serializer {

  /**
   * Converts an event to an XML formatted string.
   * @param e The event to convert.
   * @return A string containing XML for the event.
   */
  String serializeEvent(Event e) {
    String json = '{"type" : "' + e.type + '" , "id" : ' + e.id + ", "

    json += '"time" : ' + e.time + ', '
    json += parametersAsJSON(e) + ", "
    json += attributesAsJSON(e)

    json += "}"

    json
  }

   /**
    * Returns an XML version of the thing's parameters.
    * @param t The Thing with the parameters.
    * @return An XML version of the thing's parameters.
    */
  String parametersAsJSON(Thing t) {
    if (t.parameters[t.type] == null || t.parameters[t.type].size() == 0) {
      '"parameters" : null'
    }
    else {
      '"parameters" : {' + jsonizeNameValues(t.parameters[t.type]) + "}"
    }
  }

  /**
   * Returns an XML version of the thing's attributes.
   * @param t The Thing with the attributes.
   * @return An XML version of the thing's attributes.
   */
  String attributesAsJSON(Thing t) {
    if (t.attributes.size() == 0) {
      '"attributes" : null'
    }
    else {
      '"attributes" : {' + jsonizeNameValues(t.attributes) + "}"
    }
  }



  /**
   * Converts a collection of name / value pairs into the json version.
   * @param collection The collection to convert.
   * @return a JSON representation of the collection of name / value pairs.
   */
  String jsonizeNameValues (def nvPairs) {
    String json = ""

    boolean first = true
    nvPairs.each { name, value ->
      if (!first) {
        json += ', '
      }
      else first = false

      json += '"' + name + '" : ' + jsonValue(value)
    }

    json
  }

  /**
   * Formats a value into the correct JSON format.
   * @param value The value to format.
   * @return A string with the correctly formatted value.
   */
  String jsonValue (def value) {
    switch (value) {
      case Number:
        return "" + value
        break
      default:
        return '"' + value + '"'
    }
  }
}
