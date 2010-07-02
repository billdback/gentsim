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

import spock.lang.*
import org.gentsim.framework.EventDescription
import org.gentsim.framework.Event
import org.gentsim.framework.Simulation

/**
 * Tests the JSONToThingSerializer.
 * @author Bill Back
 */
class TestJSONToThingDeserializer extends Specification {

  Simulation sim = new Simulation()

  JSONToThingDeserializer jtd

  def setup() {
    EventDescription ed = new EventDescription("jsonevent")
    ed.attr1 = null
    ed.attr2 = 0
    ed.attr3 = 1.1

    ed.parameter "param1", "parameter 1"
    ed.parameter "param2", 2.0

    sim.addEventDescription(ed)

    jtd = new JSONToThingDeserializer(sim)
  }

  def "Test converting from a JSON event to an event"() {
    setup:
      // Use the serializer to get a currently valid format.
      ThingToJSONSerializer tjs = new ThingToJSONSerializer()

      Event toEvt = sim.newEvent("jsonevent")
      toEvt.time = 12
      toEvt.attr1 = "attribute1"
      toEvt.attr2 = 2
      toEvt.attr3 = 3.3

      String json = tjs.serializeEvent(toEvt)

      Event fromEvt = jtd.deserializeEvent(json)

    expect:
      fromEvt.type == "jsonevent"
      fromEvt.attr1 == "attribute1"
      fromEvt.attr2 == 2
      fromEvt.attr3 == 3.3

  }

  def "Test getting a parsing error" () {
    when:
      jtd.deserializeEvent("skj")

    then:
      DeserializationException de = thrown()

  }

  def "Test not an event error" () {
    setup:
      String str = '{"simtype" : "xeventx", "type" : "jsonevent" , "id" : 1, "time" : 12, "parameters" : {"param1" : "parameter 1", "param2" : 2.0}, "attributes" : {"attr1" : "attribute1", "attr2" : "attribute2"}}'
    when:
      jtd.deserializeEvent(str)
    then:
      DeserializationException de = thrown()
  }

  def "Test getting an unknown event type error" () {
    setup:
      String str = '{"simtype" : "event", "type" : "notjsonevent" , "id" : 1, "time" : 12, "parameters" : {"param1" : "parameter 1", "param2" : 2.0}, "attributes" : {"attr1" : "attribute1", "attr2" : "attribute2"}}'
    when:
      jtd.deserializeEvent(str)
    then:
      DeserializationException de = thrown()
  }

}
