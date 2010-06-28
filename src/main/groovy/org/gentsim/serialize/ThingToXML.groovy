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
 * Converts a thing to XML.
 * @author Bill Back
 */
class ThingToXMLSerializer implements Serializer {

  /**
   * Converts an event to an XML formatted string.
   * @param e The event to convert.
   * @return A string containing XML for the event.
   */
  String serializeEvent(Event e) {
    String xml = "<event type='${e.type}'>"

    xml += "<time>${e.time}</time>"
    xml += parametersAsXML(e)
    xml += attributesAsXML(e)

    xml += "</event>"

    xml
  }

   /**
    * Returns an XML version of the thing's parameters.
    * @param t The Thing with the parameters.
    * @return An XML version of the thing's parameters.
    */
  String parametersAsXML(Thing t) {
    String xml = "<parameters>"

    t.parameters[t.type].each { name, value ->
      xml += "<parameter><name>${name}</name><value>${value}</value></parameter>"
    }

    xml += "</parameters>"
    xml
  }

  /**
   * Returns an XML version of the thing's attributes.
   * @param t The Thing with the attributes.
   * @return An XML version of the thing's attributes.
   */
 String attributesAsXML(Thing t) {
   String xml = "<attributes>"

   t.attributes.each { name, value ->
     xml += "<attribute><name>${name}</name><value>${value}</value></attribute>"
   }

   xml += "</attributes>"
   xml
 }
}
