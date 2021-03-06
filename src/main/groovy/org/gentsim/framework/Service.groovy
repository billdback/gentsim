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
package org.gentsim.framework

/**
 * Defines all of the base capabilities of a service.  Services are special entities to
 * the container.
 * @author Bill Back
 */
class Service extends Entity implements Serializable {
  
  /**
   * Creates a new service.
   * @param sd The service description.
   * @param attrs Map of attributes and values.  This allows overwriting of the defaults.
   */
  Service (ServiceDescription sd, Map attrs = null) {
    super (sd, -1, attrs)
  }

}

