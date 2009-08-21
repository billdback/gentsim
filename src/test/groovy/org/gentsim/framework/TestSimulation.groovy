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
class TestSimulation {

  def s = new Simulation()

  def "Test creating simulation"() {
    setup:

    expect:
      s.getEventDescription("time-update")
      s.getEventDescription("entity-created")
      s.getEventDescription("entity-destroyed")
      s.getEventDescription("entity-state-changed")
  }

  def "Test running the simulation"() {
    setup:
      def entd = new EntityDescription("system-msg-handler")
      entd.startupCalled = false
      entd.shutdownCalled = false
      entd.handleEvent ("system.startup") { startupCalled = true }
      entd.handleEvent ("system.shutdown") { shutdownCalled = true }
      s.addEntityDescription(entd)
      def ent = s.newEntity("system-msg-handler")

    when:
      s.run()
      sleep(10)
      s.stop()

    then:
      s.isStopped()
      ent.startupCalled
      ent.shutdownCalled
  }

  def "Test running the simulation for a fixed number of cycles"() {
    setup:
      def entd = new EntityDescription("system-msg-handler")
      entd.startupCalled = false
      entd.shutdownCalled = false
      entd.handleEvent ("system.startup") { startupCalled = true }
      entd.handleEvent ("system.shutdown") { shutdownCalled = true }
      s.addEntityDescription(entd)
      def ent = s.newEntity("system-msg-handler")

    when:
      s.run(10)
      while (!s.isStopped()) sleep(100) // wait for the simulation to stop.
    then:
      s.isStopped()
      ent.startupCalled
      ent.shutdownCalled
  }

  def "Test creating and destroying enities" () {
    setup:

      def ed1 = new EntityDescription("entity1")
      ed1.attribute "called", 0
      ed1.handleEntityCreated("entity2") { entity -> called += 1 }
      ed1.handleEntityDestroyed("entity2") { entity -> called -= 1 }
      s.addEntityDescription(ed1)
      def ed2 = new EntityDescription("entity2")
      s.addEntityDescription(ed2)

    when:
      def e1 = s.newEntity("entity1")
      def e2_1 = s.newEntity("entity2")
      def e2_2 = s.newEntity("entity2")
      s.cycle()
    then:
      e1.called == 2

    when:
      s.removeEntity(e2_1)
      s.cycle()
    then:
      e1.called == 1

    when:
      s.removeEntity(e2_2.id)
      s.cycle()
    then:
      e1.called == 0
  }

  def "Test updating enities and changing state" () {
    setup:
      def ed3 = new EntityDescription("entity3")
      ed3.attribute "called", 0
      ed3.handleEntityCreated("entity4") { entity -> called += 1 }
      ed3.handleEntityDestroyed("entity4") { entity -> called -= 1 }
      s.addEntityDescription(ed3)

      def ed4 = new EntityDescription("entity4")
      ed4.attribute "called", 0
      ed4.handleEntityStateChanged ("entity3", "called") { entity -> called += 1 }
      s.addEntityDescription(ed4)

    when:
      def e3 = s.newEntity("entity3")
      def e4_1 = s.newEntity("entity4")
      def e4_2 = s.newEntity("entity4")
      s.cycle()
      s.cycle() // needed to send all events
    then:
      e3.called == 2
      e4_1.called == 2

    when:
      s.removeEntity(e4_1)
      s.cycle()
      s.cycle() // needed to send all events
    then:
      e3.called == 1
      e4_2.called == 3
  }

  def "Test processing events" () {
    setup:
      def ed1 = new EntityDescription("entity1")
      ed1.attribute "called", 0
      ed1.handleEvent("event1") { event -> called += 1 }
      s.addEntityDescription(ed1)
      s.addEventDescription(new EventDescription("event1"))
      s.addEventDescription(new EventDescription("event2"))

    when:
      def e1 = s.newEntity("entity1")
    then:
      e1.called == 0

    when:
      e1.sendEvent(s.newEvent("event1"))
      s.cycle()
      s.cycle()
    then:
      e1.called == 1

    when:
      e1.sendEvent(s.newEvent("event1"))
      e1.sendEvent(s.newEvent("event2"))
      s.cycle()
    then:
      e1.called == 2

  }

  def "Test statistics" () {
    setup:
      Simulation s = new Simulation()
      def ed = new EntityDescription("some-entity")
      ed.handleEvent ("new-event") {}
      s.addEntityDescription(ed)

    when:
      s.newEntity ("some-entity")
      s.sendEvent (new Event(new EventDescription("new-event"), 1))
      s.run(1000)
      while (!s.isStopped()) sleep(100) // wait for the simulation to stop.
    then:
      s.statistics.start_time != 0 && s.statistics.start_time < new Date().getTime()
      s.statistics.end_time   != 0 && s.statistics.end_time   < new Date().getTime()
      s.statistics.elapsed_time > 0
      s.statistics.number_cycles == 1000
      s.statistics.number_events_sent == 1
  }

}

