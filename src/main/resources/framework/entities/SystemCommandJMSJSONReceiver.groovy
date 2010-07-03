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
import org.gentsim.framework.EntityDescription
import org.gentsim.jms.JMSConstants
import org.gentsim.jms.JMSReceiver
import javax.jms.Message
import org.gentsim.serialize.JSONToThingDeserializer
import org.gentsim.framework.Event
import javax.jms.TextMessage
import org.gentsim.framework.Simulation
import org.gentsim.util.Trace
import org.gentsim.framework.ContainedEntity

ed = new EntityDescription("system.jms.json.commandreceiver")

// Note: This must be properly set to a JMSReceiver before JMS messages can be sent.
ed.parameter "jms_connection", null

ed.handleEvent("system.status.shutdown") { evt ->
  jms_connection.close()
}