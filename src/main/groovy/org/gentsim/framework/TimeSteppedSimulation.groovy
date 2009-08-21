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

/**
 * Defines a simulation that uses time stepping.  A time update event is automatically sent, incrementing by one.
 */
class TimeSteppedSimulation extends Simulation {

  /** Use the same time update event and just change the time attribute. */
  def timeUpdateEvent

  /**
   * Creates a new time stepped simulation, loading descriptions from the given location.
   */
  TimeSteppedSimulation () {
    super()
    timeUpdateEvent = newEvent("time-update")
  }

  /**
   * Creates a new time stepped simulation, loading descriptions from the given location.
   * @param location The location of the scripts to load.
   */
  TimeSteppedSimulation (String location) {
    super(location)
    timeUpdateEvent = newEvent("time-update")
  }

  /**
   * Creates a new time stepped simulation, loading desriptions from multiple locations.
   * @param locations A list of locations to load from.
   */
  TimeSteppedSimulation (List locations) {
    super(locations)
    timeUpdateEvent = newEvent("time-update")
  }

  /**
   * Performs a single tick of the simulation.  The time stepped simulation sends a time update
   * event at the beginning of each cycle.
   */
  def cycle () {
    timeUpdateEvent.time = super.eventQueue.currentTime + 1 // time just about to update.
    sendEventToEntities(timeUpdateEvent)
    super.cycle()
  }

}
