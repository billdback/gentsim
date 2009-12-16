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
package org.gentsim.jms

import javax.jms.Session
import org.gentsim.util.TraceWriter

/**
 * Writes trace messages to JMS.
 * @author Bill Back
 */
class JMSTraceWriter implements TraceWriter {

  /** Session for talking to ActiveMQ. */
  Session session

  /**
   * Creates a new JMS trace writer.
   * @param JMSConnection connection to the JMS broker to talk to.
   */
  JMSTraceWriter(JMSConnection) {
  }

  /**
   * Writes a trace message to JMS.
   * @param t The trace name.
   * @param msg The message to write. 
   */
  def trace (String t, String msg) {

  }
}
