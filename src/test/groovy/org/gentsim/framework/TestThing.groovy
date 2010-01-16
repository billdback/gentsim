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

class TestThing extends Specification {
  
  def "Test creating base things"() {
    setup:
      def bd = new Description("type")
      def t1 = new Thing(bd, 1)

    expect:
      t1.id == 1
      bd.type == "type"
  }

  def "Test creating thing with no description"() {
    when:
      new Thing(null, 1)
    then:
      thrown(IllegalArgumentException)
  }

  def "Test creating thing attributes"() {
    setup:
      def bd = new Description("type")
      bd.attribute "foo"
      bd.attribute "bar", 1
      bd.attribute "foobar", { 3 + 2 }

    when:
      def t1 = new Thing(bd, 1)
      def t2 = new Thing(bd, 2)

    then: 
      t1.foo == null
      t1.getAttribute("bar") == 1
      t1.bar == 1
      t1.foobar == 5

      t2.foo == null
      t2.getAttribute("bar") == 1
      t2.bar == 1

    when:
      t1.foo = "hi"
    then:
      t1.foo == "hi"
      t2.foo == null
  }

  def "Test creating thing with attribute override"() {
    setup:
      def bd = new Description("type")
      bd.attribute "foo", 1
      bd.attribute "bar", "hi"

    when:
      def t1 = new Thing(bd, 1)
    then:
      t1.foo == 1
      t1.bar == "hi"

    when:
      t1 = new Thing(bd, 1, [foo: 2, bar: "hello"])
    then:
      t1.foo == 2
      t1.bar == "hello"
  }

  def "Test creating thing parameters"() {
    setup:
      def bd = new Description("type")
      bd.parameter "foo", "bar"
      bd.parameter "bar", 1
      bd.parameter "foobar", { 3 + 2 }

    when:
      def t1 = new Thing(bd, 1)

    then:
      t1.getParameter("foo") == "bar"
      t1.foo == "bar"
      t1.getParameter("bar") == 1
      t1.bar == 1
      t1.getParameter("foobar") == 5
      t1.foobar == 5

    when:
      def t2 = new Thing(bd, 2)
      t1.setParameter "foo", "nofoo"

    then:
      t1.foo == "nofoo"
      t2.foo == "nofoo"
  }

  def "Test attributes and parameters with the same type"() {
    setup:
      def bd = new Description("type")
      bd.attribute "foo", "three"
      bd.parameter "foo", 3.0

    when:
      // in all cases, attributes have priority over parameters (local vs. global)
      def e = new Thing(bd, 1)
    
    then:
      e.getAttribute("foo") == "three"
      e.getParameter("foo") == 3.0

    when:
      e.foo = "five"

    then:
      e.getAttribute("foo") == "five"
      e.getParameter("foo") == 3.0
    
    when:
      e.setParameter "foo", 5.0

    then:
      e.getAttribute("foo") == "five"
      e.getParameter("foo") == 5.0
    
      e.foo == "five"
  }

}

