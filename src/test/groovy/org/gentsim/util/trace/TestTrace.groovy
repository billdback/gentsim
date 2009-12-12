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
package org.gentsim.util.trace


import spock.lang.*
import org.junit.runner.RunWith

@Speck
@RunWith(Sputnik)
class TestTrace {

  // This is needed because Trace is all static and doesn't get cleared out between tests.
  // There's probably a better answer, but this works for now.
  def setup() {
    Trace.traces.clear()
    Trace.traceWriters.clear()
  }
  
  def "Test adding a trace"() {
    setup:
      Trace.addTraceWriter (new InternalTraceWriter())
    expect:
      Trace.traces.isEmpty()
      Trace.traceWriters.size() == 1
  }

  def "Test writing to single trace writer"() {
    setup:
      Trace.on("test")
      TraceWriter tw = new InternalTraceWriter()
      Trace.trace("test", "test message 1") // just make sure it doesn't blow up

    when:
      Trace.addTraceWriter (tw)
      Trace.trace("test", "test message 2")
    then:
      tw.trace == "test"
      tw.message == "test message 2"

    when:
      Trace.off("test")
      Trace.trace("test", "test message 3")
    then:
      tw.trace == "test"
      tw.message == "test message 2" // didn't change

    when:
      Trace.on("test")
      Trace.trace("test", "test message 4")
    then:
      tw.trace == "test"
      tw.message == "test message 4"
  }

  def "Test writing to multiple trace writers"() {
    setup:
      Trace t = new Trace()
      Trace.on("test")
      TraceWriter tw1 = new InternalTraceWriter()
      Trace.addTraceWriter(tw1)
      TraceWriter tw2 = new InternalTraceWriter()
      Trace.addTraceWriter(tw2)

    when:
      Trace.trace("test", "test message")
    then:
      tw1.trace == "test"
      tw1.message == "test message"
      tw2.trace == "test"
      tw2.message == "test message"
  }


}

