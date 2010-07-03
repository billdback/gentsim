/*
Copyright © 2010 William D. Back
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
import org.gentsim.jms.JMSSubscriber
import org.gentsim.jms.JMSConstants

import javax.jms.Message
import javax.jms.TextMessage
import javax.jms.JMSException

/**
 * This script provides commandline utility for writing messages from a JMS topic.
 * Usage: jmsconsole <topic>
 *   topic is the JMS topic to connect to.
 * NOTE:  gentsim.jar should be in the classpath.
 * @author Bill Back
 */
class JMSTopicConsole extends JMSSubscriber {

  /**
   * Creates a new console for logging trace messages.
   */
  JMSTopicConsole(String topic) {
    super("tcp://localhost:61616", topic)
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
        super.close()
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
    println "Usage: jmsconsole <topic>"
    println "\ttopic: The JMS topic to read from."
  }

  static main (String [] args) {
    if (args.length < 1) {
      showUsage()
    }
    else {
      JMSTopicConsole jmsc = new JMSTopicConsole(args[0])
    }
  }

}
