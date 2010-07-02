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

import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.TopicConnection
import javax.jms.TopicSession
import javax.jms.Topic
import javax.jms.TopicSubscriber

/**
 * The JMS Subscriber provides an easy mechanism for subscribing to JMS topics.
 * @author Bill Back
 */
abstract class JMSSubscriber extends ActiveMQTopicClient implements javax.jms.MessageListener {

  ActiveMQConnectionFactory connectionFactory
  TopicConnection           connection
  TopicSession              session
  Topic                     topic
  TopicSubscriber           subscriber

  /**
   * Creates a new subscriber and starts the connection to JMS.
   * @param connectionURL The URL for the running JMS instance.
   * @param topic The topic to connect to.
   */
  JMSSubscriber(String connectionURL, String topic) {
    super(connectionURL, topic)
    this.connectionFactory = new ActiveMQConnectionFactory(connectionURL)
    this.connection = connectionFactory.createTopicConnection()
    this.session    = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)
    this.topic = session.createTopic(topic)
    this.subscriber = session.createSubscriber(this.topic)
    this.subscriber.setMessageListener(this)

    this.connection.start()
  }

  /**
   * Handles the message from the JMS connection.
   * @param message The message received from the JMS connection.
   */
  abstract void onMessage(javax.jms.Message message)
  
}
