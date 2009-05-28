package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestScriptEngineConnector {

  def "Test finding a resource"() {
    setup:
      def sec = new ScriptEngineConnector()
    
    when:
      def cnx = sec.getResourceConnection ("/framework/events")
    then:
      cnx != null
  }

  def "Test not finding a resource"() {
    setup:
      def sec = new ScriptEngineConnector()
    
    when:
      def cnx = sec.getResourceConnection ("resources/framework/events")
    then:
      thrown (ResourceException)
  }
}
