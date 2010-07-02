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
import net.sf.json.groovy.JsonSlurper
import org.gentsim.framework.Simulation
import net.sf.json.JSON
import net.sf.json.JSONObject

/**
 * Deserializes from a JSON representation of a thing to the specific thing.
 */
class JSONToThingDeserializer extends Deserializer {

  /**
   * The slurper to use to convert from a string to a usable JSON format.
   */
  JsonSlurper slurper = new JsonSlurper()

  /**
   * Creates a new deserializer with the simulation to use to create events.
   * @param sim The simulation to use creating events.
   */
  JSONToThingDeserializer(Simulation sim) {
    super.simulation = sim
  }

  /**
   * Creates an event from the JSON formatted version.
   * Time and ID are ignored if present.
   * Parameters are ignored if present, i.e. this won't allow parameters to be globally changed from JSON events.
   * @param str The JSON version of the event.
   * @return An event that can be sent to the simulation.
   */
  def Event deserializeEvent(String str) throws DeserializationException {

    JSONObject json
    Event evt
    
    try {
      json = slurper.parseText(str)
    }
    catch (Exception e) {
      throw new DeserializationException(e.getMessage())
    }

    // make sure the JSON thing is really an event.
    if ("event" != json.get("simtype")) throw new DeserializationException("${str} is not a JSON represenation of an event.")

    // get the event type.
    String evtType = json.get("type")

    try {
      evt = super.simulation.newEvent(evtType)
    }
    catch (Exception e) {
      throw new DeserializationException(e.getMessage())
    }

    JSONObject attributes = json.get("attributes")
    attributes.keys()?.each { key ->
      evt.setAttribute(key, attributes.get(key))
    }

    return evt;
  }
}
