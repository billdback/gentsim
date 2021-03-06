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

import org.gentsim.jms.JMSConstants

import javax.jms.Session
import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.QueueSession
import javax.jms.QueueConnection

/**
 * Provides base functionality for JMS publishers and subscribers using ActiveMQ.
 * @author Bill Back
 */
abstract class ActiveMQQueueClient {

  private   ActiveMQConnectionFactory connectionFactory
  private   QueueConnection           connection
  protected QueueSession              session
  protected javax.jms.Queue           queue

  /**
   * Creates a connection and session on a topic.
   * @param connectionURL The URL for the running JMS instance.
   * @param queue The queue to connect to.
   */
  ActiveMQQueueClient(String connectionURL, String q) {
    this.connectionFactory = new ActiveMQConnectionFactory(connectionURL)
    this.connection = connectionFactory.createQueueConnection()
    this.connection.start()
    this.session    = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
    this.queue      = session.createQueue(q)
  }

  /**
   * Performs cleanup and closes connections to JMS.
   */
  protected close() {
    this.connection.close()
  }


}
