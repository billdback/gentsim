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
package org.gentsim.framework

import org.gentsim.util.Trace

/**
 * Special queues that returns only time update events and doesn't allow users to add events.
 * @author Bill Back
 */
class TimeUpdateEventQueue extends TimeEventQueue {

  /** Use the same time update event and just change the time attribute. */
  def timeUpdateEvent

  /**
   * Creates a new time update queue using the passed in time update event to return time.
   */
  TimeUpdateEventQueue (Event timeUpdateEvent) {
    this.timeUpdateEvent = timeUpdateEvent
  }

  /**
   * Adds an event to the queue.
   * @param event The event to add to the queue.
   */
  def add (Event event) {
    throw new Exception ("Events may not be added directly to TimeUpdateEventQueues")
  }

  /**
   * Returns the next time update event.  Time starts at 1.
   * @return The next event or a null if there are no more events.
   */
  def next() {
    ++currentTime  // first time is 1.
    this.timeUpdateEvent.time = currentTime

    this.timeUpdateEvent
  }

  /**
   * Returns a list of all events for the next time or null if the queue is empty.
   * @return A list of all events for the next time or null if the queue is empty.
   */
  def getNextTimeEvents () {
    [next()]
  }

  /**
   * Returns the total number of events in the queue.  This is always 1 for the TimeUpdateEventQueue.
   */
  int size() {
    1
  }

  /**
   * Returns the time of the next event in the queue, or -1 if there are no more events.
   */
  def getNextTime () {
    return currentTime + 1
  }

  /**
   * Returns the events for the given time, removing them from the queue.
   * @return A list of events for the given time, or null.
   */
  def getEventsForTime (int t) {
    this.currentTime = t
    this.timeUpdateEvent.time = currentTime

    this.timeUpdateEvent
    
  }


}
