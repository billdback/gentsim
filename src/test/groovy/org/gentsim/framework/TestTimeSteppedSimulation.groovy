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
class TestTimeSteppedSimulation {

  @Shared s = new Simulation()

  def "Test creating simulation"() {
    expect:
      s.getEventDescription("time-update")
      s.getEventDescription("entity-created")
      s.getEventDescription("entity-destroyed")
      s.getEventDescription("entity-state-changed")
  }

  def "Test the event cycle with time update"() {
    setup:
      def entd = new EntityDescription ("entity")
      entd.attribute "time_update_called"
      entd.attribute "time", 0
      entd.handleTimeUpdate { t -> time = t; time_update_called = new Date() }
      entd.attribute "event_called"
      entd.handleEvent ("event") { evt -> event_called = new Date() }
      s.addEntityDescription(entd)
      def entity = s.newEntity("entity")

      s.addEventDescription (new EventDescription("event"))

    when:
      (1..10).each {s.sendEvent(s.newEvent("event"))}
      s.cycle()
    then:
      entity.time == 0
      entity.time_update_called < entity.event_called
  }

}

