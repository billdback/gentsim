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

import javax.jms.QueueSender
import javax.jms.TextMessage

/**
 * The JMSSender provides support for sending messages to a JMS Queue.
 * @author Bill Back
 */
class JMSSender extends ActiveMQQueueClient {

  /** Sends messages to the queue. */
  QueueSender sender

  /**
   * Creates a sender to send messages to a JMS queue.
   * @param connectionURL The URL for the running JMS instance.
   * @param queue The queue to connect to.
   */
  JMSSender(String connectionURL, String q) {
    super(connectionURL, q)
    this.sender = super.session.createSender(super.queue)
  }

  /**
   * Sends the message to the queue.
   * @param msg The message to send.
   */
  void sendTextMessage(String msg) {
    TextMessage tmsg = super.session.createTextMessage(msg)
    sender.send(tmsg)
  }

}
