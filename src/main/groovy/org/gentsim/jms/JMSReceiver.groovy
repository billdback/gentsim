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

import javax.jms.QueueReceiver
import javax.jms.Message
import javax.jms.MessageListener

/**
 * The JMSSender provides support for sending messages to a JMS Queue.
 * @author Bill Back
 */
abstract class JMSReceiver extends ActiveMQQueueClient implements MessageListener {

  /** Receives messages from the queue. */
  QueueReceiver receiver

  /**
   * Creates a sender to send messages to a JMS queue.
   * @param connectionURL The URL for the running JMS instance.
   * @param queue The queue to connect to.
   */
  JMSReceiver(String connectionURL, String q) {
    super(connectionURL, q)
    this.receiver = super.session.createReceiver(super.queue)
    this.receiver.setMessageListener(this)
  }

  /**
   * Asynchronously handles messages received from the queue.
   * @param message The message received from the queue.
   */
  abstract void onMessage(Message message)
}
