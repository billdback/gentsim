package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestEvent {
  
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

