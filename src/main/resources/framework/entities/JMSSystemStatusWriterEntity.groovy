/*
Copyright © 2009 William D. Back
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
import org.gentsim.util.Trace

// Network entities are a special system entity that will send events
// to things connected on the network.
networkEntity = new EntityDescription("system.network.entity")

networkEntity.socket = null
networkEntity.out = null

networkEntity.handleEvent ("system.shutdown") { evt ->
  if (out != null) { out.close() }
  if (socket != null) { socket.close() }
}

networkEntity.handleEvent (".*") { evt ->
  Trace.trace("system", "network entity handling event ${evt.description.type}")
  Trace.trace("system", "out == ${out}")
  if (out != null) {
    Trace.trace("system", "writing ${evt}")
    // write the event to the socket.
    out.writeObject(evt)
    out.flush()
  }
}


