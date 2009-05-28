package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestCommand {
  
  def "Test creating commands"() {
    setup:
      def cd = new EventDescription("type")
      def c1 = new Command(cd, 1, 3)

    expect:
      c1.id == 1
      c1.type == "type"
      c1.target == 3
      c1.time == 0
  }

  def "Test creating commands at time"() {
    setup:
      def cd = new EventDescription("type")
      def c1 = new Command(cd, 1, 3, 5)

    expect:
      c1.id == 1
      c1.type == "type"
      c1.target == 3
      c1.time == 5
  }
}

