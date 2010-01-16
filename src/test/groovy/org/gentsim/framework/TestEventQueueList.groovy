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

import spock.lang.*

/**
 * Test of the EventQueueList class.
 * @author Bill Back
 */
class TestEventQueueList extends Specification {

  def "Test creating a queue list" () {
    setup:
      EventQueueList eql = new EventQueueList()
    expect:
      eql.empty()
  }

  def "Test adding queues queue list" () {
    setup:
      EventQueueList eql = new EventQueueList()
    when:
      eql.addEventQueue("queue1", new TimeEventQueue())
    then:
      eql.size() == 1

    when:
      eql.addEventQueue("queue2", new TimeEventQueue())
    then:
      eql.size() == 2
  }

  def "Test inserting a queue" () {
    setup:
      EventQueueList eql = new EventQueueList()
      eql.addEventQueue("queue1", new TimeEventQueue())
      eql.addEventQueue("queue2", new TimeEventQueue())
    expect:
      eql.size() == 2

    when:
      eql.insertEventQueueAtLocation("queue1.5", new TimeEventQueue(), 1)
    then:
      eql.size() == 3
      eql.eventQueuePatterns[0] == "queue1"
      eql.eventQueuePatterns[1] == "queue1.5"
      eql.eventQueuePatterns[2] == "queue2"
  }

  def "Test inserting a queue past the end" () {
    setup:
      EventQueueList eql = new EventQueueList()
      eql.addEventQueue("queue1", new TimeEventQueue())
      eql.addEventQueue("queue2", new TimeEventQueue())
    expect:
      eql.size() == 2

    when:
      eql.insertEventQueueAtLocation("queue3", new TimeEventQueue(), 5)
    then:
      eql.size() == 3
      eql.eventQueuePatterns[0] == "queue1"
      eql.eventQueuePatterns[1] == "queue2"
      eql.eventQueuePatterns[2] == "queue3"
  }

  def "Test successfully sending events to queues in the list" () {
    setup:
      EventQueueList eql = new EventQueueList()
      TimeEventQueue teq1 = new TimeEventQueue()
      eql.addEventQueue("evt1", teq1)
      TimeEventQueue teq2 = new TimeEventQueue()
      eql.addEventQueue("evt2", teq2)

    when:
      eql.addEvent (new Event(new EventDescription("evt1"), 1))
    then:
      teq1.size() == 1
      teq2.size() == 0

    when:
      eql.addEvent (new Event(new EventDescription("evt2"), 2))
    then:
      teq1.size() == 1
      teq2.size() == 1
  }

  def "Test sending events to queues with some failures"() {
    setup:
      EventQueueList eql = new EventQueueList()
      TimeEventQueue teq1 = new TimeEventQueue()
      eql.addEventQueue("event.*", teq1)
      TimeEventQueue teq2 = new TimeEventQueue()
      eql.addEventQueue("non-event", teq2) // matches all events
    expect:
      !eql.addEvent(new Event(new EventDescription("nomatch"), 1))

      eql.addEvent(new Event(new EventDescription("eventone"), 2))
      eql.addEvent(new Event(new EventDescription("event"), 3))
      eql.addEvent(new Event(new EventDescription("event3"), 4))

      teq1.size() == 3
      teq2.size() == 0
  }

  def "Test getting events back in the proper order"() {
    setup:
      EventQueueList eql = new EventQueueList()
      eql.addEventQueue("ed1", new TimeEventQueue())
      eql.addEventQueue("ed3", new TimeEventQueue())
      eql.addEventQueue("ed6", new TimeEventQueue())
      eql.addEvent(new Event(new EventDescription("ed3"), 1, 3))
      eql.addEvent(new Event(new EventDescription("ed1"), 2, 1))
      eql.addEvent(new Event(new EventDescription("ed6"), 3, 6))
    expect:
      eql.getEventsForTime(0) == []
      eql.getEventsForTime(1)[0].type == "ed1"
      eql.getEventsForTime(2) == []
      eql.getEventsForTime(3)[0].type == "ed3"
      eql.getEventsForTime(4) == []
      eql.getEventsForTime(5) == []
      eql.getEventsForTime(6)[0].type == "ed6"
  }

  /**
   * Test getting the current and next time.
   */
  def "Test current and next time" () {
    setup:
      EventQueueList eql = new EventQueueList()
      eql.addEventQueue("ed1", new TimeEventQueue())
      eql.addEventQueue("ed2", new TimeEventQueue())
      eql.addEvent(new Event(new EventDescription("ed1"), 1, 2))
      eql.addEvent(new Event(new EventDescription("ed2"), 2, 3))
    expect:
      eql.currentTime == 0
      eql.nextTime == 2

  }


}
