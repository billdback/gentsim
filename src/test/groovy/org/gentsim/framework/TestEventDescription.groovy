package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestEventDescription {
  
  def "Test blank event" () {
    setup:   
      def e = new EventDescription("BlankEvent")
    expect:
      e.type == "BlankEvent"
      e.attributes == [:]
      e.parameters == [:]
  }

}

