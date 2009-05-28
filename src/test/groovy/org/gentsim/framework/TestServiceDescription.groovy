package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestServiceDescription {
  
  def "Test a new service"() {
    when:
      def svc = new ServiceDescription("BlankService")
    then:
      svc.type == "BlankService"
  }

  def "Test with no entity name"() {
    when:
      new ServiceDescription("")
    then:
      thrown(IllegalArgumentException)
  }

}

