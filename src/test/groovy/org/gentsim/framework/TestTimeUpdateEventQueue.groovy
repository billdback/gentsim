/*
Copyright � 2009 William D. Back
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
 * Tests the TimeUpdateEventQueue.
 * @author Bill Back.
 */
class TestTimeUpdateEventQueue extends Specification {

  def "Test adding events exception"() {
    setup:
      def tueq = new TimeUpdateEventQueue(new Event(new EventDescription("time-update"), 1))
      def ed = new EventDescription("event")
      ed.attribute "name"

    expect:
      tueq.currentTime == 0

    when:
      tueq << new Event(ed, 1, 2) // adding event in the future.
    then:
      thrown (Exception)

    when:
      tueq << new Event(ed, 2) // adding event with no time
    then:
      thrown (Exception)
  }

  def "Test getting time events"() {
    setup:
      def tueq = new TimeUpdateEventQueue(new Event(new EventDescription("time-update"), 1))

    when:
      def event = tueq.next()
    then:
      tueq.currentTime == 1
      event.time == 1

    when:
      event = tueq.next()
    then:
      tueq.currentTime == 2
      event.time == 2
  }

  def "Test getting a list of time events"() {
    setup:
      def tueq = new TimeUpdateEventQueue(new Event(new EventDescription("time-update"), 1))
    when:
      def list = tueq.getNextTimeEvents()
    then:
      list.size == 1
      tueq.currentTime == 1
      list[0].time == 1
  }

  def "Test getting next time"() {
    setup:
      def tueq = new TimeUpdateEventQueue(new Event(new EventDescription("time-update"), 1))
    expect:
      tueq.nextTime == 1
    when:
      tueq.next()
    then:
      tueq.nextTime == 2
  }
}

