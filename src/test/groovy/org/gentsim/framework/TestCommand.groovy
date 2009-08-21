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
class TestCommand {
  
  def "Test creating commands"() {
    setup:
      def cd = new EventDescription("type")
      def c1 = new Command(cd, 1, 3)

    expect:
      c1.id == 1
      c1.type == "type"
      c1.target == 3
      c1.time == 0
  }

  def "Test creating commands at time"() {
    setup:
      def cd = new EventDescription("type")
      def c1 = new Command(cd, 1, 3, 5)

    expect:
      c1.id == 1
      c1.type == "type"
      c1.target == 3
      c1.time == 5
  }
}

