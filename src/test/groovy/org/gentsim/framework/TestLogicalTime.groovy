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

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestLogicalTime {
  def "Create logical time" () {
    setup:
      def lt = new LogicalTime()
    expect:
      lt.logicalCycleLength == 1.0
      lt.logicalStartTime == 0.0

    when:
      lt = new LogicalTime (3.0)
    then:
      lt.logicalCycleLength == 3.0
      lt.logicalStartTime == 0.0

    when:
      lt = new LogicalTime (3.0, 2.5)
    then:
      lt.logicalCycleLength == 3.0
      lt.logicalStartTime == 2.5
  }

  def "Test time of day"() {
    setup:
      def lt = new LogicalTime()
    expect:
      lt.timeOfDay(0) == 0.0
      lt.timeOfDay(2*3600) == 2.0
      lt.timeOfDay(25*3600) == 1.0
      lt.timeOfDay(3*3600 + 30*60) == 3.5

    when:
      lt = new LogicalTime(60) // 60 second cycles
    then:
      lt.timeOfDay(0) == 0.0
      lt.timeOfDay(2*60) == 2.0
      lt.timeOfDay(25*60)  == 1.0
      lt.timeOfDay(3*60 + 30) == 3.5

    when:
      lt = new LogicalTime(60, 3.0) // 60 second cycles, start at 3am
    then:
      lt.timeOfDay(0) == 3.0
      lt.timeOfDay(2*60) == 5.0
      lt.timeOfDay(25*60)  == 4.0
      lt.timeOfDay(3*60 + 30) == 6.5
  }

  def "formatted time"() {
    setup:
      def lt = new LogicalTime()
    expect:
      lt.formattedTimeOfDay(0) == "00:00:00.00"
      lt.formattedTimeOfDay(2*3600) == "02:00:00.00"
      lt.formattedTimeOfDay(25*3600) == "01:00:00.00"
      lt.formattedTimeOfDay(3*3600 + 30*60 + 10) == "03:30:10.00"

    when:
      lt = new LogicalTime(60)
    then:
      lt.formattedTimeOfDay(0) == "00:00:00.00"
      lt.formattedTimeOfDay(2*60) == "02:00:00.00"
      lt.formattedTimeOfDay(25*60) == "01:00:00.00"
      lt.formattedTimeOfDay(3*60 + 30) == "03:30:00.00"

    when:
      lt = new LogicalTime(60, 3.0)
    then:
      lt.formattedTimeOfDay(0) == "03:00:00.00"
      lt.formattedTimeOfDay(2*60) == "05:00:00.00"
      lt.formattedTimeOfDay(25*60) == "04:00:00.00"
      lt.formattedTimeOfDay(3*60 + 30) == "06:30:00.00"

  }
}
