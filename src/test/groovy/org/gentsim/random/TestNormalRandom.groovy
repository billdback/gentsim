/*
Copyright © 2010 William D. Back
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
package org.gentsim.random

import spock.lang.Specification

/**
 * Tests Normally distributed random numbers.
 * @author Bill Back
 */
class TestNormalRandom extends Specification {

  def "Test random long values"() {
    setup:
      NormalRandom nr = new NormalRandom(35.0, 5.0)
      def vals = (1..1000).collect { nr.nextDouble()}
      def mean = vals.sum() / vals.size()

    expect:
      // Not a great test, but this should usually work. 
      mean > 33.0 && mean < 37.0
  }

  def "Test random double values"() {
    setup:
      NormalRandom nr = new NormalRandom(35, 5)
      def vals = (1..1000).collect { nr.nextLong()}
      def mean = vals.sum() / vals.size()

    expect:
      // Not a great test, but this should usually work.
      mean > 33 && mean < 37
  }

}
