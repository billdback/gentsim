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

class TestDescription extends Specification {
  
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

