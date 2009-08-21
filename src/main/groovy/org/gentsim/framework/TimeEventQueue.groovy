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
 * Defines an event queue based on time.
 */
class TimeEventQueue {

  /** Queue to hold events. */
  // TODO make this an ordered list so that the keys (time) come back in order.
  SortedMap eventQueue = Collections.synchronizedSortedMap(new TreeMap());

  /**
   * The current time of the queue.  The current time is the time of the most recent event.  This is 
   * important because the event queue will not allow events to be added at the current time.  This
   * prevents having old events.  Furthermore, events can be added without a time and they will automatically
   * be set to the next time.
   */
  int currentTime = 0;

  /**
   * Adds an event to the queue.  
   * @param event The event to add to the queue.
   */
  def add (Event event) {
    if (! event.time) event.time = currentTime + 1 // this could be expensive.
//    println "adding event ${event.type} for time ${event.time}"
    if (event.time <= currentTime) throw new IllegalArgumentException ("Events must be scheduled in the future.")

    def list = this.eventQueue.get(event.time)
    if (! list) {
      list = []
      this.eventQueue.put(event.time, list)
    }

    list << event
    event
  }

  /**
   * Adds an event to the queue.
   * @param event The event to add to the queue.
   */
  def leftShift (Event event) {
    add(event)
  }

  /**
   * Returns the next event or a null if there are no more events.
   * @return The next event or a null if there are no more events.
   */
  def next() {
    static currentList = [] // NOTE:  hard to distribute.

    if (currentList.isEmpty()) {
      try { // queue could be totally empty.
        this.currentTime = this.eventQueue.firstKey()
      } catch (NoSuchElementException nsee) { return null }
      currentList = this.eventQueue.get(this.currentTime)
    }

    def evt = (currentList && !currentList.isEmpty()) ? currentList.remove(0) : null

    if (currentList == []) { // remove list if this was the last one.
      this.eventQueue.remove(this.currentTime) // remove the old, empty list.
    }

    evt
  }

  /**
   * Returns a list of all events for the next time or null if the queue is empty.
   * @return A list of all events for the next time or null if the queue is empty.
   */
  def getNextTimeEvents () {
    try {
      this.currentTime = this.eventQueue.firstKey()
      def evt = this.eventQueue.remove(this.currentTime)
      evt
    }
    catch (NoSuchElementException nsee) { // empty queue
      null
    }
  }

  /**
   * Returns the current time for the queue.
   * @return The current time for the queue.
   */
  def getCurrentTime () {
    return this.currentTime
  }

  /**
   * Returns the time of the next event in the queue, or -1 if there are no more events.
   */
  def getNextTime () {
    try {
      this.eventQueue.firstKey()
    } 
    catch (NoSuchElementException nsee) {
      return -1
    }
  }

}

