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

class TestEvent extends Specification {
  
  def "Test creating events"() {
    setup:
      def ed = new EventDescription("type")
      def e1 = new Event(ed, 1)

    expect:
      e1.id == 1
      e1.type == "type"
      e1.time == 0
  }

  def "Test creating events at time"() {
    setup:
      def ed = new EventDescription("type")
      def e1 = new Event(ed, 1, 5)

    expect:
      e1.id == 1
      e1.type == "type"
      e1.time == 5
  }
}

