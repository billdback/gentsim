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
import javax.jms.TopicSubscriber

/**
 * The JMS Subscriber provides an easy mechanism for subscribing to JMS topics.
 * @author Bill Back
 */
abstract class JMSSubscriber implements javax.jms.MessageListener {

  ActiveMQConnectionFactory connectionFactory
  TopicConnection           connection
  TopicSession              session
  Topic                     traceTopic
  TopicSubscriber           subscriber

  /**
   * Creates a new subscriber and starts the connection to JMS.
   * @param connectionURL The URL for the running JMS instance.
   * @param topic The topic to connect to.
   */
  JMSSubscriber(String connectionURL, String topic) {
    connectionFactory = new ActiveMQConnectionFactory(connectionURL)
    connection = connectionFactory.createTopicConnection()
    session    = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)
    traceTopic = session.createTopic(topic)
    subscriber = session.createSubscriber(traceTopic)
    subscriber.setMessageListener(this)

    println "Starting JMS listener on topic ${JMSConstants.JMSSystemTraceTopic}"
    connection.start()
  }

  /**
   * Handles the message from the JMS connection.
   * @param message The message received from the JMS connection.
   */
  abstract void onMessage(javax.jms.Message message)
  
}
