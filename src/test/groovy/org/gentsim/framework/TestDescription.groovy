package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestDescription {
  
  def "Test blank entity" () {
    setup:   
      def e = new Description("Blank")
    expect:
      e.type == "Blank"
      e.attributes == [:]
      e.parameters == [:]
  }

  def "Test no entity type" () {
    when:
      new Description(null)
    then:
      thrown(IllegalArgumentException)

    when:
      new Description("")
    then:
      thrown(IllegalArgumentException)
  }

  def "Test setting attributes" () {
    setup:
      def e = new Description("Attribute")

    expect:
      e.attributes == [:]

    when:
      e.attribute "attribute1"
      e.attribute "attribute2", 2

    then:
      e.attributes.size() == 2
      !e.attributes["attribute1"]
      e.attributes["attribute2"] == 2
  }

  def "Test setting multiple attributes" () {
    setup:
      def e = new Description("Attributes", ["length" : 9, "width": 16, "height":  25])
    expect:
      e.attributes.size() == 3
      e.attributes["length"] == 9
      e.attributes["width"] == 16
      e.attributes["height"] == 25
  }

  def "Test setting attributes with properties"() {
    setup:
      def ed = new Description("Properties")
    when:
      ed.length = 10
      ed.width  = 20
      ed.height = 30
    then:
      ed.attributes["length"] == 10
      ed.length == 10
      ed.attributes["width"] == 20
      ed.width == 20
      ed.attributes["height"] == 30
      ed.height == 30
  }

  def "Test setting parameters" () {
    setup:
      def e = new Description ("Parameter ")

    expect:
      e.parameters.size() == 0

    when:
      e.parameter "parameter1", "p1"
    then:
      e.parameters.size() == 1

    when:
      e.parameter "param2",     2
    then:
      e.parameters.size() == 2
      e.parameters["parameter1"] == "p1"
      e.parameters["param2"] == 2
  }

}

