/*
Copyright � 2009 William D. Back
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
package org.gentsim.util

import spock.lang.*

/**
 * Tests the Util class.
 * @author Bill Back
 */
class TestUtil extends Specification {

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

