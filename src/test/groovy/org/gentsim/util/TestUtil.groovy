package org.gentsim.util

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestUtil {

  def "Test converting a file to a string"() {
    when:
      def s = Util.FileToString (new File("src/test/resources/entities/Animals.groovy"))
    then:
      s.contains("cat = new EntityDescription(\"cat\")")
  }

  def "Test converting a file to string buffer"() {
    when:
      def sb = Util.FileToStringBuffer (new File("src/test/resources/entities/Animals.groovy"))
    then:
      sb.toString().contains("cat = new EntityDescription(\"cat\")")
  }
}

