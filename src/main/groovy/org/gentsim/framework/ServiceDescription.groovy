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
 * This class is used to describe simulation services.  Services are a special case of entities.
 */
class ServiceDescription extends EntityDescription {

  /**
   * Creates a new service that can be instantiated in the simulation.
   * @param type The unique type of the service.
   * @param attrs A map of attribute values.
   * @throws IllegalArgumentException Thrown if the type is invalid.
   */
  ServiceDescription (String type, Map attrs = null) throws IllegalArgumentException {
    super(type, attrs)
  }


}

