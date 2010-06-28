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
 * Tests the TimeEventQueue
 * @author Bill Back.
 */
class TestTimeEventQueue extends Specification {

  def "Test adding entries and getting back in order"() {
    setup:
      def teq = new TimeEventQueue()
      def ed = new EventDescription("event1")
      ed.attribute "name"
    when:
      (1..5).each { teq << new Event(ed, it, 12 - it * 2) }
    then:
      teq.next().id == 5
      teq.next().id == 4
      teq.next().id == 3
      teq.next().id == 2
      teq.next().id == 1
      teq.next()    == null
  }

  def "Test getting the size of the event queue"() {
    setup:
      def teq = new TimeEventQueue()
      def ed = new EventDescription("event1")
      ed.attribute "name"

    expect:
      teq.size() == 0

    when:
      teq << new Event(ed, 1, 2) // adding event in the future.
    then:
      teq.size() == 1

    when:
      teq << new Event(ed, 2, 3)
    then:
      teq.size() == 2

    when:
      teq << new Event(ed, 3, 3)
    then:
      teq.size() == 3
  }

  def "Test adding events"() {
    setup:
      def teq = new TimeEventQueue()
      def ed = new EventDescription("event1")
      ed.attribute "name"
    when:
      teq << new Event(ed, 1, 2) // adding event in the future.
    then:
      teq.next().id == 1

    when:
      teq << new Event(ed, 2) // adding event with no time
      def e = teq.next()
    then:
      e.id == 2
      e.time == 3
      teq.currentTime == 3

    when:
      teq << new Event(ed, 3, 2) // adding event in the past.
    then:
      thrown (IllegalArgumentException)

    when:
      teq << new Event(ed, 4, 3) // adding event at the current time.
    then:
      thrown (IllegalArgumentException)

  }

  def "Test checking time"() {
    setup:
      def teq = new TimeEventQueue()
      def ed = new EventDescription("event1")
      ed.attribute "name"
    when:
      teq << new Event(ed, 1, 2)
      teq << new Event(ed, 1, 4)
      teq << new Event(ed, 1, 4)
    then:
      teq.currentTime == 0 
      teq.nextTime == 2

    when:
      teq.next()
    then:
      teq.currentTime == 2
      teq.nextTime == 4

    when:
      teq.next()
    then:
      teq.currentTime == 4
      teq.nextTime == 4

    when:
      teq.next()
    then:
      teq.currentTime == 4
      teq.nextTime == -1
  }

  def "Test getting a list of events"() {
    setup:
      def teq = new TimeEventQueue()
      def ed = new EventDescription("event1")
      ed.attribute "name"

    expect:
      teq.getNextTimeEvents() == null

    when:
      teq << new Event(ed, 1, 2)
      teq << new Event(ed, 2, 2)
      teq << new Event(ed, 3, 2)
      teq << new Event(ed, 4, 3)
      teq << new Event(ed, 5, 3)
      teq << new Event(ed, 6, 4)
    then:
      teq.currentTime == 0
      teq.nextTime == 2

    when:
      def list = teq.getNextTimeEvents()
    then:
      list.size == 3
      teq.currentTime == 2
      teq.nextTime == 3

    when:
      list = teq.getNextTimeEvents()
    then:
      list.size == 2
      teq.currentTime == 3
      teq.nextTime == 4
    
    when:
      list = teq.getNextTimeEvents()
    then:
      list.size == 1
      teq.currentTime == 4
      teq.nextTime == -1
  }

  def "Test getting events for a particular time"() {
    setup:
      def teq = new TimeEventQueue()
      def ed = new EventDescription("event1")
      ed.attribute "number"
      def ent1 = new Event(ed, 1, 2, [ "number" : 1])
      teq.add(ent1)
      def ent2 = new Event(ed, 2, 2, [ "number" : 1])
      teq.add(ent2)
      def ent3 = new Event(ed, 3, 3, [ "number" : 1])
      teq.add(ent3)
      def ent4 = new Event(ed, 4, 5, [ "number" : 1])
      teq.add(ent4)

    expect:
      teq.getEventsForTime(1) == []
      teq.currentTime == 1
      teq.getEventsForTime(2).size == 2
  }
}

