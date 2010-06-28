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
package org.gentsim.jms

import org.gentsim.serialize.Serializer
import org.gentsim.framework.Event

/**
 * Writes events to JMS as using the provided serializer.
 * @author Bill Back
 */
class JMSEventPublisher extends JMSPublisher {

  /**
   * Instance to use to serialize events.
   */  
  private serializer

  /**
   * Creates a new event publisher for sending events in XML format to JMS.
   * @param connectionURL The URL to use to connect to JMS.
   * @param topic The topic to publish to.
   * @param serializer The serializer to use for converting from an event to the proper format.
   */
  JMSEventPublisher (String connectionURL, String topic, Serializer serializer) {
    super(connectionURL, topic)
    println "Created a publish topic ${topic}"
    this.serializer = serializer
  }

  /**
   * Publishes an event to the JMS topic.
   * @param evt The event to send.
   */
  void publishEvent (Event evt) {
    sendTextMessage(serializer.serializeEvent(evt))
  }

}
