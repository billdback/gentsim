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
*/package org.gentsim.framework

/**
 * Manages a list of event queues.  Each entry has a pattern for the types of events it handles and an event queue.
 * @author Bill Back
 */
class EventQueueList {

  /** List of patterns for the event queues that lines up with the queues. */
  def eventQueuePatterns = []

  /** Event queues to store events. */
  def eventQueues = []

  /**
   * Returns the number of queues in the list.
   */
  int size() {
    eventQueues.size()
  }

  /**
   * Returns true if the queue list is empty.
   */
  boolean empty() {
    eventQueues.empty
  }

  /**
   * Adds an event queue to the list of event queues at the end of the list.
   * @param pattern The pattern for event types this queue will handle.
   * @param eventQueue The event queue that will store this type of events.
   */
  def addEventQueue (pattern, eventQueue) {
    this.eventQueuePatterns << pattern
    this.eventQueues << eventQueue
  }

  /**
   * Inserts an event queue into the list at the given (zero index) location in the list.  If the list doesn't
   * have enough entries for this to be stored, it is added to the end.  Note that the desired location is not remembered
   * so if the list subsequently grows, the original location is not preserved.
   * @param pattern The pattern for event types this queue will handle.
   * @param eventQueue The event queue that will store this type of events.
   * @param location The location that the queue wants to be inserted at.
   */
  def insertEventQueueAtLocation (pattern, eventQueue, location) {
    if (this.eventQueues.size() < location) {
      addEventQueue (pattern, eventQueue) // don't leave blank spots in the list.
    }
    else {
      this.eventQueuePatterns.add(location, pattern)
      this.eventQueues.add(location, eventQueue)
    }
  }


  /**
   * Will make an attempt to add an event to a queue.  This walks through the queues in order and if the type matches
   * the pattern, then the event is added and checking stops.
   * @event The event to add to a queue.
   * @return True if the event was added, false if it was not.
   */
  boolean addEvent (Event event) {
    for (int idx = 0; idx < this.eventQueuePatterns.size(); idx++) {
      if (event.type ==~ this.eventQueuePatterns[idx]) {
        this.eventQueues[idx] << event
        return true
      }
    }
    false
  }

  /**
   * Runs the closure against each queue.
   * @param c The closure to run against each queue.
   */
  def eachQueue (Closure c) { this.eventQueues.each { c(it) } }

  /**
   * Returns the current time for the queue list.  This is the minimum time of all the queues.
   * @return The current time for the queue list.
   */
  def getCurrentTime () {
    int ct = 0
    this.eventQueues.each { if (it.currentTime < ct) ct == it.currentTime }
    ct
  }

  /**
   * Returns the time of the next event in the queue, or -1 if there are no more events.
   */
  def getNextTime () {
    def nt = Integer.MAX_VALUE
    this.eventQueues.each { if (it.nextTime < nt) nt = it.nextTime }
    println "next time is ${nt}"
    (nt == Integer.MAX_VALUE) ? -1 : nt
  }

  /**
   * Returns all of the events, in order by queue, for the given time.
   */
  def getEventsForTime (int t) {
    def events = []
    this.eventQueues.each { q ->
      def evts = q.getEventsForTime(t)
      if (evts != null) events << evts
    }
    events.flatten()
  }
}
