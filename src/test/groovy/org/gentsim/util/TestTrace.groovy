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
 * Tests the Trace class.
 * @author Bill Back
 */
class TestTrace extends Specification {

  // This is needed because Trace is all static and doesn't get cleared out between tests.
  // There's probably a better answer, but this works for now.
  def setup() {
    Trace.traces.clear()
    Trace.traceWriters.clear()
  }
  
  def "Test adding a trace"() {
    setup:
      Trace.addTraceWriter ({tr, msg -> true } as TraceWriter)
    expect:
      Trace.traces.isEmpty()
      Trace.traceWriters.size() == 1
  }

  def "Test writing to a single trace" () {
    setup:
      Trace.on("test")
      Trace.trace("test", "test message") // just make sure it doesn't blow up
      def trace = ""
      def message = ""
      Trace.addTraceWriter ({tr, msg -> trace = tr; message = msg } as TraceWriter)

    when:
      Trace.trace("test", "test message 2")
    then:
      trace == "test"
      message == "test message 2"

    when:
      Trace.off("test")
      Trace.trace("test", "test message 3")
    then:
      trace == "test"
      message == "test message 2" // didn't change

    when:
      Trace.on("test")
      Trace.trace("test", "test message 4")
    then:
      trace == "test"
      message == "test message 4"
  }

  def "Test writing to multiple trace writers" () {
    setup:
      Trace.on("test")
      Trace.trace("test", "test message") // just make sure it doesn't blow up
      def trace1 = ""
      def message1 = ""
      def trace2 = ""
      def message2 = ""
      Trace.addTraceWriter ({tr, msg -> trace1 = tr; message1 = msg } as TraceWriter)
      Trace.addTraceWriter ({tr, msg -> trace2 = tr; message2 = msg } as TraceWriter)

    when:
      Trace.trace("test", "test message")
    then:
      trace1 == "test"
      message1 == "test message"
      trace2 == "test"
      message2 == "test message"
  }

}

