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

import org.gentsim.util.Trace

/**
 * Loads descriptions from the file system and adds them to a simulation container.
 * @author Bill Back
 */
class FileSystemDescriptionLoader extends DescriptionLoader {

  /**
   * Loads descriptions from a script.  The script can be a directory, in which case an attempt is
   * made to load from children directories.  It can also be any valid name of a resource, in which case that is
   * used.
   * @param scriptName A script name to load from.
   * @param sc The simulation container to add the descriptions to.
   * @throws FileNotFoundException if the file doesn't exist.
   */
  def loadDescriptionsFromLocation (String scriptName, SimulationContainer sc) {
    Trace.trace("system", "Attempting to load descriptions from ${scriptName}")

    File file = new File(scriptName)
    def ris = getClass().getResourceAsStream(scriptName)

    if (file.exists() && !file.isDirectory() && file.name.endsWith("groovy")) { // single file, load it.
      loadDescriptionUsingGroovyScriptEngine(new GroovyScriptEngine(
                                                   file.absolutePath, new GroovyClassLoader()),
                                             file.name, sc)
    }
    else if (file.exists()) { // directory of files - load each (note, can recurse if child is dir.)
      for (File f in file.list()) {
        loadDescriptionsFromLocation(scriptName + File.separator + f, sc)
      }
    }
    else if (new File("./${scriptName}").exists()) {  // child of current directory, so load it.
      loadDescriptionsFromLocation ("./${scriptName}", sc)
    }
    else if (ris != null) { // valid script name, either file or directory.
      if (scriptName.endsWith("groovy")) { // assume this is a single file
        loadDescriptionUsingGroovyScriptEngine(new GroovyScriptEngine(new ScriptEngineConnector(),
                                               new GroovyClassLoader()), scriptName, sc)
      }
      // NOTE:  the following does not work with jars and throws an exception.
      else { // treat like a directory.  This will only load immediate resources with .groovy extensions.  No recurse.
        def br = new BufferedReader(new InputStreamReader(ris))
        for (def line = br.readLine(); line != null; line = br.readLine()) {
          if (line.endsWith("groovy")) loadDescriptionsFromLocation (scriptName + File.separator + line, sc)
        }
      }
    }
    else { // exhausted all possibilities.
      throw new FileNotFoundException ("No file or directory named ${scriptName}")
    }
  }

  /**
   * Determines if any of the standard description locations exist.
   * Standard locations are entities, services, and events.
   * @param path The location to search.
   * @param sc The simulation container to add the descriptions to.
   * @return true if any of the standard locations exist.
   */
  def standardLocationsExist (String path, SimulationContainer sc) {
    new File(path + File.separator + "entities").exists() ||
    new File(path + File.separator + "events").exists() ||
    new File(path + File.separator + "services").exists()
  }

  /**
   * Load from the standard locations.  Standard locations are entities, services, and events.
   * @param path The location to search.
   * @param sc The simulation container to add the descriptions to.
   */
  def loadFromStandardLocations (String path, SimulationContainer sc) {
    if (new File("{path}${File.separator}entities").exists()) loadDescriptionsFromLocation "${path}{File.separator}entities", sc
    if (new File("{path}${File.separator}events").exists())   loadDescriptionsFromLocation "${path}{File.separator}events", sc
    if (new File("{path}${File.separator}services").exists()) loadDescriptionsFromLocation "${path}{File.separator}services", sc
  }

  /**
   * Loads a descriptions from a resource.  For this to work, all of the entity descriptions in an
   * individual file must be assigned to individual variables, e.g. myEd = new <type>Description("mytype").
   * This is due to the way the GroovyScriptEngine passes back variables.
   * Note that by using the GroovyScriptEngine, it is theoretically possible to change the entity
   * description at runtime and see the change, however, this has not been tried.
   * @param scriptName A resource name to load from.
   * @param sc The simulation container to add the descriptions to.
   * @throws FileNotFoundException if the file doesn't exist.
   */
  private loadDescriptionUsingGroovyScriptEngine(GroovyScriptEngine gse, String scriptName, SimulationContainer sc) {
    try {
      Trace.trace("system", "Running script ${scriptName}")
      Binding b = new Binding()
      gse.run(scriptName, b)
      // search the binding for any entity descriptions that got created.
      for (var in b.variables) {
        def desc = var.getValue()
        if (desc instanceof ServiceDescription) {
          sc.addServiceDescription(desc)
          Trace.trace("system", "Adding service description ${desc.type}")
          Trace.trace("services", "Adding service description ${desc.type}")
        }
        else if (desc instanceof EntityDescription) {
          Trace.trace("system", "Adding entity description ${desc.type}")
          Trace.trace("entities", "Adding entity description ${desc.type}")
          sc.addEntityDescription(desc)
        }
        else if (desc instanceof EventDescription) {
          Trace.trace("system", "Adding event description ${desc.type}")
          Trace.trace("events", "Adding event description ${desc.type}")
          sc.addEventDescription(desc)
        }
      }
    }
    catch (InstantiationException ie) { /* ignore */ }
    catch (Exception e) {
      //e.printStackTrace()
      Trace.trace "system", "Error loading script from ${scriptName}"
    }
  }


}
