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

import javax.jms.Session
import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.Connection

/**
 * Provide basic JMS client capabilities.
 * @author Bill Back
 */
class ActiveMQClient {

  /**
   * Factory for creating connections.
   * TODO:  Make the connection configurable.
   */
  ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")

  /** Session to use for creating queues and writing data. */
  Session session

  /**
   * Creates a new ActiveMQClient with a connection and session.
   * TODO:  Does this need to be done for all JMS clients, or can there be one client that makes the connection?
   */
  ActiveMQClient() {
    Connection cnx = connectionFactory.createActiveMQConnection()
    cnx.start()
    session = cnx.createSession(false, Session.AUTO_ACKNOWLEDGE)
  }
  
}
