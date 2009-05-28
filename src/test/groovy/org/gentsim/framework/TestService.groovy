package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestService {
  
  def "Create a base service"() {
    setup:
      def sd = new ServiceDescription("new service")
    when:
      def svc = new Service(sd)

    then:
      svc.id   == -1
      svc.type == "new service"
  }

}
