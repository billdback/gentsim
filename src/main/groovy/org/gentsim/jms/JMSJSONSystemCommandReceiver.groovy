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

import org.gentsim.serialize.JSONToThingDeserializer
import javax.jms.TextMessage
import org.gentsim.util.Trace
import javax.jms.Message
import org.gentsim.framework.ContainedEntity
import org.gentsim.framework.Simulation
import org.gentsim.framework.Event

/**
 * Creates a JMS receiver that receives system commands in JSON format.
 * @author Bill Back
 */
class JMSJSONSystemCommandReceiver extends JMSReceiver {

  /** Simulation that owns this receiver. */
  Simulation simulation

  /** Used to deserialize incoming messages. */
  JSONToThingDeserializer jtd

  /**
   * Creates a new system command receiver.
   * @param url The URL to JMS.
   * @param q The queue to connect to.
   * @param sim The simulation to send commands to.
   */
  JMSJSONSystemCommandReceiver(String url, String q, Simulation sim) {
    super(url, q)
    this.simulation = sim
    this.jtd = new JSONToThingDeserializer(sim)
  }

  /**
   * Asynchronously handles messages received from the queue.
   * @param message The message received from the queue.
   */
  void onMessage(Message message) {
    Trace.trace "jms", "Received JSM command: ${((TextMessage)message).getText()}"
    Event evt = jtd.deserializeEvent(((TextMessage)message).getText())
//    Trace.trace "jms", "New JMS command:  ${evt}"
    simulation.sendEvent(evt)
  }
}
