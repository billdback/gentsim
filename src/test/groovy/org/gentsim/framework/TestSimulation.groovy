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
import org.gentsim.util.Statistics
import org.gentsim.util.Trace

class TestSimulation extends Specification {

  def s = new Simulation()

  def "Test creating simulation"() {
    setup:

    expect:
      s.getEventDescription("time-update")
      s.getEventDescription("entity-created")
      s.getEventDescription("entity-destroyed")
      s.getEventDescription("entity-state-changed")
  }

  def "Test setting the cycle limit"() {
    setup:

    expect:
      s.cycleLength == 0

    when:
      s.cycleLength = 2
    then:
      s.cycleLength == 2
  }

  def "Test running the simulation"() {
    setup:
      def entd = new EntityDescription("system-msg-handler")
      entd.startupCalled = false
      entd.shutdownCalled = false
      entd.handleEvent ("system.status.startup") { startupCalled = true }
      entd.handleEvent ("system.status.shutdown") { shutdownCalled = true }
      s.addEntityDescription(entd)
      def ent = s.newEntity("system-msg-handler")

    when:
      s.run()
      sleep(2)
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
      entd.handleEvent ("system.status.startup") { startupCalled = true }
      entd.handleEvent ("system.status.shutdown") { shutdownCalled = true }
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

  def "Test creating entities with init"() {
    setup:

      def ed1 = new EntityDescription("entity1")
      ed1.initCalled = false
      ed1.method ("init") { initCalled = true }
      s.addEntityDescription(ed1)

    when:
      def e1 = s.newEntity("entity1")
      s.cycle()
    then:
      e1.initCalled == true
  }

  def "Test creating and destroying entities" () {
    setup:

      def ed1 = new EntityDescription("entity1")
      ed1.attribute "called", 0
      ed1.handleEntityCreated("entity2") { entity -> println "created"; called += 1 }
      ed1.handleEntityDestroyed("entity2") { entity -> println "destroyed"; called -= 1 }
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
      s.removeEntity(e2_1.id)
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
      s.removeEntity(e4_1.id)
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

  def "Test sending events in order"() {
    setup:
      def evtd1 = new EventDescription("user1")
      def evtd2 = new EventDescription("user2")
      s.addEventDescription(evtd1)
      s.addEventDescription(evtd2)

      def entd = new EntityDescription("allhandler")
      entd.events = []
      entd.handleEvent(".*") { event -> events << event.type; println event.type }
      s.addEntityDescription(entd)

      s.setEventOrder(["user1", "user2"])

      def expectedEventOrder = ["system.control.pause", "system.status.startup", "entity-created", "user1", "user2"]

      def ent = s.newEntity("allhandler")
      s.sendEvent(s.newEvent("user2"))
      s.sendEvent(s.newEvent("user1"))
      s.sendEvent(s.newEvent("system.control.pause"))
      s.sendEvent(s.newEvent("system.status.startup"))
      s.cycle()
    expect:
      ent.events == expectedEventOrder
  }

  def "Test setting time step"() {
    setup:
      Simulation sim = new Simulation()
    expect:
      !sim.timeStepped

    when:
      sim.timeStepped()
    then:
      sim.timeStepped
  }

  def "Test creating with time step on"() {
    setup:
      Simulation sim = new Simulation(true)
    expect:
      sim.timeStepped
  }

  def "Test using time step"() {
    setup:
      s.timeStepped()

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
      entity.time == 1
      entity.time_update_called < entity.event_called
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
      Statistics.instance.start_time != 0 && Statistics.instance.start_time < new Date().getTime()
      Statistics.instance.end_time   != 0 && Statistics.instance.end_time   < new Date().getTime()
      Statistics.instance.elapsed_time > 0
      Statistics.instance.number_cycles == 1000
      Statistics.instance.number_events_sent == 1
  }

}

