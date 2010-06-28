/*
Copyright � 2010 William D. Back
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

import org.gentsim.util.TraceWriter

/**
 * Writes trace messages to JMS.
 * @author Bill Back
 */
class JMSTraceWriter extends JMSPublisher implements TraceWriter {

  /**
   * Creates a new JMS trace writer.
   * @param url The URL to use to connect to the JMS server.
   */
  JMSTraceWriter(String url) {
    super(url, JMSConstants.JMSSystemTraceTopic)
  }

  /**
   * Writes a trace message to JMS.
   * @param t The trace name.
   * @param msg The message to write. 
   */
  def trace (String t, String msg) {
    if (msg == "exit") {
      super.sendTextMessage("exit")
      close()
    }
    else {
      super.sendTextMessage("[${t}] ${msg}")
    }
  }
}
