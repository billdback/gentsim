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
import org.gentsim.jms.JMSPublisher
import org.gentsim.jms.JMSConstants

/**
 * This script provides commandline utility for writing test messages to a JMS topic.
 * Usage: jmstestwriter <topic>
 *   topic is the JMS topic to connect to.
 * NOTE:  gentsim.jar should be in the classpath.
 * @author Bill Back
 */
class JMSTopicTestWriter extends JMSPublisher {

  JMSTopicTestWriter(topic) {
    super("tcp://localhost:61616", topic)
  }

  /**
   * Shows the usage for this tool.
   */
  static showUsage() {
    println "Usage: jmstestwriter <topic>"
    println "\ttopic: The JMS topic to write to."
  }

  static main (String [] args) {

    if (args.length < 1) {
      showUsage()
    }
    else {
      println "creating the JMSTopicTestWriter"
      JMSTopicTestWriter jmst = new JMSTopicTestWriter(args[0])
      (1..5).each { cnt ->
        def msg = "test message ${cnt}"
        println msg
        jmst.sendTextMessage(msg)
        Thread.sleep(1000)
      }
      println "exiting"
      jmst.sendTextMessage("exit")
      jmst.close()
    }
  }

}
