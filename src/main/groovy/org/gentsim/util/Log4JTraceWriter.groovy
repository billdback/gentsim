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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory

/**
 * Traces messages to Log4J.
 * @author Bill Back
 */
class Log4JTraceWriter implements TraceWriter {

  /** Default logger for gentsim. */
  private static Log log = LogFactory.getLog("org.gentsim.log")

  /**
   * Send a trace message.
   * @param t The trace to send to.
   * @param msg The message to send to.
   */
  def trace (String t, String msg) {
    log.info("[${t}] ${msg}")
  }
}
