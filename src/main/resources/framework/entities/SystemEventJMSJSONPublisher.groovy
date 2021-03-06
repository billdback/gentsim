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

ed = new EntityDescription("system.jms.json.publisher")

// Note: This must be properly set to a JMSPublisher before JMS messages can be sent.
ed.parameter "jms_connection", null

ed.handleEvent("system.*") { evt ->
  jms_connection.publishEvent(evt)
  if (evt.type == "system.status.shutdown") {
    try {
      jms_connection.close()
    }
    catch (javax.jms.IllegalStateException ise) {} // ignore if already closed.
  }
}