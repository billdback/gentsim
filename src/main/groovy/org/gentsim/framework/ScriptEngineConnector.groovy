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

import java.net.*

/**
 * Creates a connector to the script engine.
 * @author Bill Back
 */
class ScriptEngineConnector implements ResourceConnector {

  /**
   * Provides a resource connector for the GroovyScriptEngine to use to load resources.
   * @returns A ULRConnection to the resource.
   * @throws ResourceException Thrown if there is a problem finding or connecting to the resource.
   */
  def URLConnection getResourceConnection (String name) throws ResourceException {
    try {
      def url = getClass().getResource(name)
      def cnx = url.openConnection()
      cnx
    }
    catch (NullPointerException npe) {
      throw new ResourceException ("Unable to locate resource ${name}")
    }
  }
}
