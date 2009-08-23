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
/**
 * The gentsim generator will generate files and structures of appropriate types
 * to ease gentsim simulation development.
 */
package org.gentsim.tools

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class GentsimGenerator {

  private static Log log = LogFactory.getLog("org.gentsim.log")

  /**
   * Returns a groovy file name by making sure the given name has .groovy at the end.
   * @param name The name to be used for the file.
   * @return A groovy file name by making sure the given name has .groovy at the end.
   */
  def groovyFileName (String name) {
    // Since this might have a path, need to get just the root file part.
    File f = new File(name)
    String gname = f.name
    gname = this.replaceIllegalNameChars(gname)
    gname = gname.endsWith("groovy") ? gname : gname + ".groovy"
    //
    // make sure the file has a first letter as a capital.  The parameter will
    // start with a lower case letter.  This ensures that the script and variable
    // don't have the same name, which would cause an error.'
    gname = (String)Character.toUpperCase(gname.charAt(0)) + gname.substring(1) // gotta be a better way
    (f.getParent() == null) ? gname : f.getParent() + File.separator + gname
  }

  /**
   * Returns a name that can be used for the file's variable.  This must differ
   * from the file name, since a filename becomes a class name for groovy scripts.
   * @param name The name to use.
   * @return A name to use for the variable.
   */
  String groovyVarName (String name) {
    String vname = this.replaceIllegalNameChars(name)
    vname = (String)Character.toLowerCase(vname.charAt(0)) + vname.substring(1)
  }

  /**
   * Replaces some common illegal chars with valid ones.  This is to open
   * up the options for names of entities in the simulation.
   * @param name The name to replace the chars of.
   * @return A new name with all legal characters.
   */
  String replaceIllegalNameChars (String name) {
    String nname = name.replace('.', '_')  // . is not valid
    nname = nname.replace('-', '_')  // - is not valid
    nname
  }

  def createDirIfNeeded (String dirName) {
    File dir = new File(dirName)
    if (!dir.exists()) dir.mkdir()
    else if (!dir.isDirectory()) thrown new IOException ("${dirName} exists, but is not a directory.")
  }

  /**
   * Copies a resource from the default location with the given name.
   * @param resourceName
   */
  def copyResource(String resourceName) {
    def ris = getClass().getResourceAsStream(resourceName)
    if (ris != null) {
      File res = new File("./" + resourceName) // use the same name, but current location.
      Writer writer = res.newWriter()
      int c = -1
      while ((c = ris.read()) != -1) {
        writer.write(c)
      }
      writer.flush()
      writer.close()
    }
    else {
      throw new FileNotFoundException ("Unable to find resource ${resourceName}")
    }

  }
  /**
   * Writes the content to a groovy file of the given name.
   * @param name The name of the thing being written.  This is used for the filename.
   * @param content The content of the file.
   */
  def writeFile (String name, def content) {
    File simfile = new File (this.groovyFileName (name))
    if (simfile.exists()) {
      throw new IOException ("${simfile.name} already exists.  Rename or delete the other file.")
    }

    Writer writer = simfile.newWriter()
    writer.write (content)
    writer.flush()
    writer.close()
  }

  /**
   * Generates a new simulation structure and class.
   * @param name The name of the simulation.
   */
  def generateSimulation (String name) {
    log.info "generating simulation ${name} ..."

    def vname = this.groovyVarName(name)

    this.writeFile(name,
"""import org.gentsim.framework.*

// create the simulation and specify location of entities, etc.
def ${vname} = new Simulation(["entities", "events", "services"])
// NOTE:  Use the followng for a time stepped simulation.
// def ${vname} = new TimeSteppedSimulation(["entities", "events", "services"])

// Create entities and services, setting attribute values.

// Start the simulation.
${vname}.run()

""")

    // generate the default directories as needed.
    this.createDirIfNeeded("entities")
    this.createDirIfNeeded("events")
    this.createDirIfNeeded("services")

    this.copyResource("/log4j.xml")
  }

  /**
   * Generates an entity with the given name.
   */
  def generateEntity (String name) {
    log.info "generating entity ${name}"

    this.createDirIfNeeded("entities")

    def vname = this.groovyVarName(name)

    this.writeFile("entities" + File.separator + name,
"""import org.gentsim.framework.EntityDescription

// create the entity.
//${vname} = new EntityDescription("${name}")

// create attributes
//${vname}.attrName = 0 // default value

// create parameters for all entities of this type
//${vname}.parameter "paramName", "value"

// create a method
//${vname}.method("methodName") { /* contents */ }

// handle time updates
//${vname}.handleTimeUpdate { time -> /* contents */ }

// register interest in other entities
//${vname}.handleEntityCreated("entityName") { ent -> /* contents */ }
//${vname}.handleEntityStateChanged ("entityName", "attributeName") { ent -> /* contents */ }
//${vname}.handleEntityDestroyed ("entityName") { ent -> /* contents */ }

// use a service
//${vname}.usesService("serviceName")

""")

  }

  /**
   * Generates an event with the given name.
   */
  def generateEvent (String name) {
    log.info "generating event ${name}"

    this.createDirIfNeeded("events")

    def vname = this.groovyVarName(name)

    this.writeFile("events" + File.separator + name,
"""import org.gentsim.framework.EventDescription

${vname} = new EventDescription("${name}")

// Event attributes.
// ${vname}.attribute "attribute-name", "default-value"
""")
  }

  /**
   * Generates a service with the given name.
   */
  def generateService (String name) {
    log.info "generating service ${name}"

    this.createDirIfNeeded("services")

    def vname = this.groovyVarName(name)

    this.writeFile("services" + File.separator + name,
"""import org.gentsim.framework.ServiceDescription

${vname} = new ServiceDescription("${name}")
// create attributes
//${vname}.attrName = 0 // default value

// create parameters for all services of this type
//${vname}.parameter "paramName", "value"

// create a method
//${vname}.method("methodName") { /* contents */ }

// handle time updates
//${vname}.handleTimeUpdate { time -> /* contents */ }

// register interest in entities
//${vname}.handleEntityCreated("entityName") { ent -> /* contents */ }
//${vname}.handleEntityStateChanged ("entityName", "attributeName") { ent -> /* contents */ }
//${vname}.handleEntityDestroyed ("entityName") { ent -> /* contents */ }

""")
  }

}
