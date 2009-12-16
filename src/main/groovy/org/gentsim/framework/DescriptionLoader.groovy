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
 * Defines the methods common to description loaders, which are classes that load simulation descriptions for
 * entities, events, and services.
 * @author Bill Back
 */
abstract class DescriptionLoader {

  /**
   * Loads descriptions from the given location and puts them into the simulation container.
   * @param location Some location that has meaning to the description loader, such as a directory or URI.
   * @param sc The simulation container to add the entities to.
   */  
  abstract loadDescriptionsFromLocation(String location, SimulationContainer sc)

  /**
   * Loads descriptions from a set of locations and puts them into the simulation container.
   * @param scripts List of locations to load from.
   * @param sc The simulation container to add the descriptions to.
   */
  def loadDescriptionsFromLocations (List scripts, SimulationContainer sc) {
    scripts.each { loadDescriptionsFromLocation(it, sc) }
  }

}