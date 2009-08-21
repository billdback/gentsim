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

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestCondition {

  def "Test matching a thing with a simple condition" () {
    setup:
      def t = ["attributes" : ["name" : "Joe"]]
      def c = new Condition ("name", "Joe")
    
    expect:
      c.matches(t)
  }

  def "Test matching against a closure" () {
    setup:
      def c = new Condition().match({ it.attributes.name == "Joe" })

    when:
      def t = ["attributes" : ["name" : "Joe"]]
    then:
      c.matches(t)

    when:
      t = ["attributes" : ["name" : "Sam"]]
    then:
      !c.matches(t)
  }

  def "Test to see if any of a set of values matches" () {
    setup:
      def c = new Condition().matchAny("name", ["Joe", "Bob", "Don"])

    when:
      def t = ["attributes" : ["name" : "Joe"]]
    then:
      c.matches(t)

    when:
      t = ["attributes" : ["name" : "Sam"]]
    then:
      !c.matches(t)
     
  }

  def "Test that any part of a set of conditions matches" () {
    setup:
      def c = new Condition().matchAny (["name": "Joe", "age": 35, "state": "TN"])

    when:
      def t = ["attributes" : ["name" : "Joe"]]
    then:
      c.matches(t)
    
    when:
      t = ["attributes" : ["age" : 35]]
    then:
      c.matches(t)

    when:
      t = ["attributes" : ["state": "TN"]]
    then:
      c.matches(t)
  }

  def "Test to see if all of a set of values matches" () {
    setup:
      def c = new Condition().matchAll(["name": "Joe", "age": 35, "state": "TN"])

    when:
      def t = ["attributes" : ["name" : "Joe"]] // too few attributes
    then:
      !c.matches(t)
    
    when:
      t = ["attributes" : ["name" : "Joe", "age": 35, "state": "TN"]]
    then:
      c.matches(t)
    
    when:
      t = ["attributes" : ["name" : "Bob", "age": 35, "state": "TN"]]
    then:
      !c.matches(t)
    
  }

  def "Test to see if a value is in a range" () {
    setup:
      def c = new Condition().matchRange("age", 30..40)

    when:
      def t = ["attributes" : ["age" : 35]]
    then:
      c.matches(t)

    when:
      t = ["attributes" : ["age" : 28]] // too young
    then:
      !c.matches(t)
  }

  def "Test to see if a value matches a closure" () {
    setup:
      def t = ["attributes" : ["name" : "Joe"]]
      def c = new Condition ("name", { name -> name == "Joe" })

    expect:
      c.matches(t)

    when:
      c = new Condition ("name", { name -> name == "Bob" })
    then:
      !c.matches(t)
  }

  def "Test to see if lists match" () {
    setup:
      def t = ["attributes" : ["name" : ["Joe", "BoB", "Sam"]]]
      def c = new Condition().matchIntersects("name", ["Joe"]) // simple match

    expect:
      c.matches(t)

    when:
      c = new Condition().matchIntersects("name", ["Joe", "Sam", "Fred"]) // matches against a list.
    then:
      c.matches(t)

    when:
      c = new Condition().matchIntersects("name", ["Rita", "Sally", "Sue"]) // fails to match
    then:
      !c.matches(t)
  }
}

