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
/* 
 * This script provides commandline utility for creating gentsim 
 * projects and things.
 * Usage: gentsim { document | <thing-type> <names> [<thing-type> <names> ...] }
 *   thing-type is one of [simulation | entity | event | service ]
 *   names is a list of names of the given type to create.
 * NOTE:  gentsim.jar should be in the classpath.
 */
import org.gentsim.jms.JMSSubscriber
import org.gentsim.jms.JMSConstants

import javax.jms.Message
import javax.jms.TextMessage
import javax.jms.JMSException

class JMSTraceConsole extends JMSSubscriber {

  /**
   * Creates a new console for logging trace messages.
   */
  JMSTraceConsole() {
    super("tcp://localhost:61616", JMSConstants.JMSSystemTraceTopic)
  }

  /**
   * Prints the contents of the message to standard out.
   * @param message The JMS message received.
   */
  void onMessage(Message message) {
    try {
      TextMessage msg = (TextMessage)message
      String text = msg.getText()
      println text
      if (text == "exit") {
        println "bye..."
        connection.close()
      }
    }
    catch (JMSException jmse) {
      jmse.printStackTrace()
    }
  }

  /**
   * Shows the usage for this tool.
   */
  static showUsage() {
    println "Usage: jmstrace"
    println "\tin the future you will be able to specify JMS parameters"
  }

  static main (String [] args) {

    println "creating the JMSTrace reader"
    JMSTraceConsole jmstc = new JMSTraceConsole()
  }

}
