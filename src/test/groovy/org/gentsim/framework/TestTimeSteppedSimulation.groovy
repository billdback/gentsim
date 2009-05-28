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

