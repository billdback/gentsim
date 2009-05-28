package org.gentsim.framework

import java.net.*

/**
 * Provides a resource connector for the GroovyScriptEngine to use to load resources.
 * @returns A ULRConnection to the resource.
 * @throws ResourceConnection Thrown if there is a problem finding or connecting to the resource.
 */
class ScriptEngineConnector implements ResourceConnector {
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
