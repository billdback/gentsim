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
import org.gentsim.framework.Event
import org.gentsim.framework.EventDescription
import org.gentsim.framework.Thing

/**
 * Tests the ThingToXMLSerializer.
 * @author Bill Back
 */
class TestThingToXMLSerializer extends Specification {

  def cleanup() {
    Thing.parameters = [:]
  }

  def "Test serializing an Event with no attributes or parameters"() {
    setup:
      ThingToXMLSerializer txs = new ThingToXMLSerializer()
      EventDescription ed = new EventDescription("jsonevent")
      Event evt = new Event(ed, 1)
      evt.time = 12
      String res = txs.serializeEvent(evt)

    expect:
      res == "<event type='jsonevent'><time>12</time><parameters></parameters><attributes></attributes></event>"
  }

  def "Test serializing an Event with just attributes"() {
    setup:
      ThingToXMLSerializer txs = new ThingToXMLSerializer()
      EventDescription ed = new EventDescription("jsonevent")
      ed.attr1 = "attribute1"
      ed.attr2 = "attribute2"
      Event evt = new Event(ed, 1)
      evt.time = 12
      String res = txs.serializeEvent(evt)

    expect:
      res == "<event type='jsonevent'><time>12</time><parameters></parameters><attributes><attribute><name>attr1</name><value>attribute1</value></attribute><attribute><name>attr2</name><value>attribute2</value></attribute></attributes></event>"
  }

  def "Test serializing an Event with just parameters"() {
    setup:
      ThingToXMLSerializer txs = new ThingToXMLSerializer()
      EventDescription ed = new EventDescription("jsonevent")
      ed.parameter "param1", "parameter 1"
      ed.parameter "param2", 2.0
      Event evt = new Event(ed, 1)
      evt.time = 12
      String res = txs.serializeEvent(evt)

    expect:
      res == "<event type='jsonevent'><time>12</time><parameters><parameter><name>param1</name><value>parameter 1</value></parameter><parameter><name>param2</name><value>2.0</value></parameter></parameters><attributes></attributes></event>"
  }

  def "Test serializing an Event with attributes and parameters"() {
    setup:
      ThingToXMLSerializer txs = new ThingToXMLSerializer()
      EventDescription ed = new EventDescription("jsonevent")
      ed.attr1 = "attribute1"
      ed.attr2 = "attribute2"

      ed.parameter "param1", "parameter 1"
      ed.parameter "param2", 2.0

      Event evt = new Event(ed, 1)
      evt.time = 12
      String res = txs.serializeEvent(evt)

    expect:
      res == "<event type='jsonevent'><time>12</time><parameters><parameter><name>param1</name><value>parameter 1</value></parameter><parameter><name>param2</name><value>2.0</value></parameter></parameters><attributes><attribute><name>attr1</name><value>attribute1</value></attribute><attribute><name>attr2</name><value>attribute2</value></attribute></attributes></event>"
  }

}
