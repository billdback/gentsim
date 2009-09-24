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

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class LogicalTime {

  private static Log log = LogFactory.getLog("org.gentsim.log")

  /** Logical length of a cycle in seconds. */
  float logicalCycleLength = 1.0

  /** Time of day to start from for logical time. */
  float logicalStartTime = 0.0 // midnight

  /** Starts at midnight with a one second logical time. */
  LogicalTime () {}

  /**
   * Creates a new LogicalTime with the given cycle length and start time.
   * @param cycleLength The length of a cycle in logical time in seconds.
   * @param startTime The time of day to start at in hours where 0.0 is midnight.
   */
  LogicalTime (BigDecimal cycleLength, BigDecimal startTime = 0.0) {
    this.logicalCycleLength = cycleLength
    this.logicalStartTime = startTime
  }

  /**
  * Returns the time of day as a fractional hour.  0.0 is midnight and 23.999+ is just before midnight.
  * @param cycle The simulation cycle.
  * @return the time of day as a float.  It will be 0.0..23.99
  */
  float timeOfDay (int cycle) {
    // Convert cycle to time of day in seconds.
log.info ("${this.logicalStartTime} * 3600.0 + (${cycle} * ${this.logicalCycleLength}) ")
    float tod = (this.logicalStartTime * 3600.0) + (cycle * this.logicalCycleLength)
log.info "tod == ${tod}"

    int hour = (int)(tod / 3600.0)
log.info "hour == ${hour}"
    float rem = (tod - (hour * 3600.0)) / 3600
log.info "rem == ${rem}"
    hour = hour % 24
log.info "time of day for ${cycle} is ${rem + hour}"
    return hour + rem
  }

  /**
   * Returns the time of day as a formatted time string, i.e. hour:minute:seconds
   * @param cycle The simulation cycle.
   * @return The time of day as a formatted time string, i.e. hour:minute:seconds
   */
  String formattedTimeOfDay (int cycle) {
    float tod = this.timeOfDay(cycle)
    int hours = (int)tod
    float seconds = tod - hours // seconds in fractions of an hour.
    int minutes = (int)(seconds*60)
    seconds = (seconds - minutes/60) * 3600 
log.info "hours == ${hours} minutes == ${minutes} seconds == ${seconds}"
log.info (String.format("%02d:%02d:%05.2f", hours, minutes, seconds))
    String.format("%02d:%02d:%05.2f", hours, minutes, seconds)
  }
}

