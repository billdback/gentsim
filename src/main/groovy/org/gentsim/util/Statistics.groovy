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
package org.gentsim.util

/**
 * Class to provide common statistics.
 * @author Bill Back
 */
@Singleton
class Statistics {

  /** Map of statistics about the simulation execution. */
  def statistics = [:]

  /** Prints the statistics for the simulation. */
  def printStatistics() { statistics.each { Trace.trace("statistics", "${it.key}: ${it.value}") } }

  /**
   * Sets the value of the statistic.
   * @param name The name of the statistic.  If this is null, nothing will be set.
   * @param value The value of the statistic.
   */
  def setStatistic (String name, value) {
    if (name != null) statistics[name] = value
  }

  /**
   * Returns the value of the statistic for the given name or null if it doesn't exist.
   * @param name The name of the statistic.
   */
  def getStatistic (String name) {
    statistics[name]
  }

  /**
   * Provide a cleaner use of statistics to reference a statistic by name as a property.
   * @param name The name of the statistic.
   * @param value The value of the statistic.  If null, then this is a query for the value, which is returned.
   */
  def propertyMissing (String name, value = null) {
    (value == null) ? this.getStatistic(name) : (this.setStatistic(name, value))
  }

}
