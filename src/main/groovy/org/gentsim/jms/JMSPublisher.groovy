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
package org.gentsim.jms

import org.gentsim.jms.JMSConstants

import javax.jms.Session
import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.TopicConnection
import javax.jms.TopicSession
import javax.jms.Topic
import javax.jms.TopicPublisher
import javax.jms.TextMessage
import javax.jms.JMSException

/**
 * The JMS Publisher provides an easy way to publish to JMS topics.
 */
class JMSPublisher {
  ActiveMQConnectionFactory connectionFactory
  TopicConnection           connection
  TopicSession              session
  Topic                     traceTopic
  TopicPublisher            publisher

  /**
   * Creates a new publisher and connects to the JMS topic.
   * @param connectionURL The URL for the running JMS instance.
   * @param topic The topic to connect to.
   */
  JMSPublisher(String connectionURL, String topic) {
    connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")
    connection = connectionFactory.createTopicConnection()
    session    = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)
    traceTopic = session.createTopic(JMSConstants.JMSSystemTraceTopic)
    publisher  = session.createPublisher(traceTopic)

    println "Starting JMS publisher on topic ${JMSConstants.JMSSystemTraceTopic}"
    connection.start()
  }

  /**
   * Performs cleanup and closes connections to JMS.
   */
  protected close() {
    this.connection.close()
  }

  /**
   * Sends a text message to the JMS topic.
   * @param msg The message to send.
   */
  def sendTextMessage (String msg) {
    try {
      TextMessage message = session.createTextMessage()
      message.setText(msg)
      publisher.publish(message)
    }
    catch (JMSException jmse) {
      jmse.printStackTrace()
    }
  }

  /**
   * Sends a text message to the JMS topic.
   * @param msg The message to send.
   */
  def sendTextMessage (GString msg) {
    this.sendTextMessage(msg.toString())
  }
}
