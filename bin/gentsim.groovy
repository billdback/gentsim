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
import org.gentsim.tools.*

/**
 * This script provides commandline utility for creating gentsim
 * projects and things.
 * Usage: gentsim { document | <thing-type> <names> [<thing-type> <names> ...] }
 *   thing-type is one of [simulation | entity | event | service ]
 *   names is a list of names of the given type to create.
 * NOTE:  gentsim.jar should be in the classpath.
 * @author Bill Back
 */
class gentsim {

  static showUsage() {
    println "Usage: gentsim { document | <thing-type> <names> [<thing-type> <names> ...] }"
    println "\tthing-type is one of [simulation | entity | event | service ]"
    println "\tnames is a list of names of the given type to create."
  }

  static main (String [] args) {

    def docu = new Documentor()
    def gg = new GentsimGenerator()

    String type = null
    final types = ["simulation", "entity", "event", "service"]

    for (arg in args) {
      if (arg == "document") {
        docu.loadDescriptionsFrom(".")
        docu.generateHTML(".")
      }
      else if (arg in types) type = arg
      else try {
        switch (type) {
          case "simulation": gg.generateSimulation(arg); break
          case "entity"    : gg.generateEntity(arg);     break
          case "event"     : gg.generateEvent(arg);      break
          case "service"   : gg.generateService(arg);    break
          default:  showUsage(); break;
        }
      }
      catch (IOException ioe) { // thrown for attempts to overwrite an existing file.
        ioe.printStackTrace();
        System.err.println ("ERROR:  attempting to overwrite file '${arg}'.  Rename or delete the old file.")
      }
    }
  }

}
