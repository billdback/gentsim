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
package org.gentsim.util

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class will print trace messages to log4j.debug.  Tracing can be turned
 * on an off for various elements as desired by the user.
 */
class Trace {

  /** Default logger for gentsim. */
  private static Log log = LogFactory.getLog("org.gentsim.log")

  /** List of things to trace. */
  private static Set traces = new HashSet()

  /** Adds something else to trace. */
  static on (String t) {
    traces.add(t)
  }

  /** Removes a trace type. */
  static off (String t) {
    traces.remove(t)
  }
  /**
   * Adds a trace message to the log.
   * @param t The trace (that may or may not be set).
   * @param msg The message to trace if tracing for this type is on.
   */
  static trace (String t, String msg) {
    if (traces.contains(t)) { log.info("[${t}] ${msg}")}
  }

}
