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
 * Tests the Statistics class.
 * @author Bill Back
 */
class TestStatistics extends Specification {

  def "Test setting a statistic with a null name" () {
    setup:
      Statistics.instance.statistics = [:] // empty out - may have data from previous tests.
      Statistics.instance.setStatistic(null, 10)
    expect:
      Statistics.instance.statistics.size() == 0 

  }

  def "Test creating statistics and getting the value" () {
    setup:
      Statistics.instance.statistics = [:] // empty out - may have data from previous tests.
      Statistics.instance.stat1 = "one"
    expect:
      Statistics.instance.stat1 == "one"
      Statistics.instance.stat2 == null
  }
}
