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
import org.gentsim.util.Trace
import org.gentsim.framework.Event
import org.gentsim.framework.Thing

/**
 * Tests the ThingToJSONSerializer.
 * @author Bill Back
 */
class TestThingToJSONSerializer extends Specification {

  def setup() {
    // Parameters are static, so they need to be reset between tests.
    Thing.parameters = [:]
  }

  def "Test serializing an Event with no attributes or parameters"() {
    setup:
      ThingToJSONSerializer tjs = new ThingToJSONSerializer()
      EventDescription ed = new EventDescription("jsonevent")
      Event evt = new Event(ed, 1)
      evt.time = 12
      String res = tjs.serializeEvent(evt)

    expect:
      res == '{"simtype" : "event", "type" : "jsonevent" , "id" : 1, "time" : 12, "parameters" : null, "attributes" : null}'
  }

  def "Test serializing an Event with just attributes"() {
    setup:
      ThingToJSONSerializer tjs = new ThingToJSONSerializer()
      EventDescription ed = new EventDescription("jsonevent")
      ed.attr1 = "attribute1"
      ed.attr2 = "attribute2"
      Event evt = new Event(ed, 1)
      evt.time = 12
      String res = tjs.serializeEvent(evt)

    expect:
      res == '{"simtype" : "event", "type" : "jsonevent" , "id" : 1, "time" : 12, "parameters" : null, "attributes" : {"attr1" : "attribute1", "attr2" : "attribute2"}}'
  }

  def "Test serializing an Event with just parameters"() {
    setup:
      ThingToJSONSerializer tjs = new ThingToJSONSerializer()
      EventDescription ed = new EventDescription("jsonevent")
      ed.parameter "param1", "parameter 1"
      ed.parameter "param2", 2.0
      Event evt = new Event(ed, 1)
      evt.time = 12
      String res = tjs.serializeEvent(evt)

    expect:
      res == '{"simtype" : "event", "type" : "jsonevent" , "id" : 1, "time" : 12, "parameters" : {"param1" : "parameter 1", "param2" : 2.0}, "attributes" : null}'
  }

  def "Test serializing an Event with attributes and parameters"() {
    setup:
      ThingToJSONSerializer tjs = new ThingToJSONSerializer()
      EventDescription ed = new EventDescription("jsonevent")
      ed.attr1 = "attribute1"
      ed.attr2 = "attribute2"
      ed.parameter "param1", "parameter 1"
      ed.parameter "param2", 2.0

      Event evt = new Event(ed, 1)
      evt.time = 12
      String res = tjs.serializeEvent(evt)

    expect:
      res == '{"simtype" : "event", "type" : "jsonevent" , "id" : 1, "time" : 12, "parameters" : {"param1" : "parameter 1", "param2" : 2.0}, "attributes" : {"attr1" : "attribute1", "attr2" : "attribute2"}}'
  }

}
