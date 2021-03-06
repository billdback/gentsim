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
package org.gentsim.tools

import org.gentsim.framework.*

/**
 * Creates documentation for entities and events.
 * @author Bill Back
 */
class Documentor extends SimulationContainer {

  def prepLocation (loc) {
    // if the docs folder doesn't exist, then create it.
    File f = new File(loc + "/docs")
    if (!f.exists()) f.mkdir()

    // TODO - I use this logic in a few spots, make it a util. 
    def ris = getClass().getResourceAsStream("/css/gentsim.css")
    if (ris != null) {
      File res = new File(loc + "/docs/gentsim.css") // use the same name, but current location.
      Writer writer = res.newWriter()
      int c = -1
      while ((c = ris.read()) != -1) {
        writer.write(c)
      }
      writer.flush()
      writer.close()
    }
    else {
      throw new FileNotFoundException ("Unable to find resource gentsim.css")
    }
  }

  /**
   * Creates HTML documentation of all classes and entities.
   */
  def generateHTML (loc) {
    prepLocation (loc)
    generateHTMLIndex(loc) 
    eventDescriptions.each { generateHTMLForEvent(loc, it.value) }
    entityDescriptions.each { generateHTMLForEntity(loc, it.value) }
    serviceDescriptions.each { generateHTMLForService(loc, it.value) }
  }

  protected generateHTMLIndex (loc) {
    FileWriter f = new FileWriter("${loc}/docs/index.html")
    def mb = new groovy.xml.MarkupBuilder(f)
    mb.html {
      head {
        title("Index for ${loc}")
        link(rel:"stylesheet", type:"text/css", href:"gentsim.css")
      }
      body {
        h1("Simulation Descriptions for ${new File(loc).getAbsolutePath()}")

        if (eventDescriptions.size() > 0) {
          h2("Events")
          eventDescriptions.each { a(href:"event_${it.value.type}.html", it.value.type); br() }
        }

        if (entityDescriptions.size() > 0) {
          h2("Entities")
          entityDescriptions.each { a(href:"entity_${it.value.type}.html", it.value.type); br() }
        }

        if (serviceDescriptions.size() > 0) {
          h2("Services")
          serviceDescriptions.each { a(href:"service_${it.value.type}.html", it.value.type); br() }
        }
      }
    }
  }

  protected generateHTMLHead (mb, page_title) {
    mb.head {
      title(page_title)
      link(rel:"stylesheet", type:"text/css", href:"gentsim.css")
    }
  }

  protected generateHTMLTableForMap (mb, table_title, Map m) {
    if (m.size() > 0) {
      mb.h2(table_title)
      mb.table {
        tr { th("Name"); th("Value") }
        m.each { attr ->
          tr {
            td(attr.key); td(attr.value) 
          }
        }
      }
    }
  }

  protected generateHTMLForMethods (mb, ed) {
    if (ed.methods.size() > 0) {
      mb.h2("Methods")
      mb.ul {
        ed.methods.each { mthd ->
          li (mthd.key)
        }
      }
    }
  }

  protected generateHTMLForEventHandlers (mb, ed) {
    if (ed.eventHandlers.size() > 0) {
      mb.h2("Event Handlers")
      mb.ul {
        ed.eventHandlers.each { mthd ->
          li (mthd.key)
        }
      }
    }
  }

  protected generateHTMLForServiceUse (mb, ed) {
    if (ed.services.size() > 0) {
      mb.h2("Services Used")
      mb.ul {
          ed.services.each { svc ->
          li (svc)
        }
      }
    }
  }

  protected generateHTMLForEvent (loc, ed) {
    def mb = new groovy.xml.MarkupBuilder(new FileWriter("${loc}/docs/event_${ed.type}.html"))
    mb.html {
      generateHTMLHead (mb, "Event $ed.type}")
      body {
        a(href: "index.html", "[index]")
        h1("Event ${ed.type}")
        generateHTMLTableForMap (mb, "Attributes", ed.attributes)
        generateHTMLTableForMap (mb, "Parameters", ed.parameters)
      }
    }
  }

  protected generateHTMLForEntity (loc, ed) {
    def mb = new groovy.xml.MarkupBuilder(new FileWriter("${loc}/docs/entity_${ed.type}.html"))
    mb.html {
      generateHTMLHead (mb, "Entity ${ed.type}")
      body {
        a(href: "index.html", "[index]")
        h1("Entity ${ed.type}")
        generateHTMLTableForMap (mb, "Attributes", ed.attributes)
        generateHTMLTableForMap (mb, "Parameters", ed.parameters)
        generateHTMLForMethods  (mb, ed)
        generateHTMLForEventHandlers  (mb, ed)
        generateHTMLForServiceUse (mb, ed)
      }
    }
  }

  protected generateHTMLForService (loc, sd) {
    def mb = new groovy.xml.MarkupBuilder(new FileWriter("${loc}/docs/service_${sd.type}.html"))
    mb.html {
      generateHTMLHead (mb, "Service ${sd.type}")
      body {
        a(href: "index.html", "[index]")
        h1("Service ${sd.type}")
        generateHTMLTableForMap (mb, "Attributes", sd.attributes)
        generateHTMLTableForMap (mb, "Parameters", sd.parameters)
        generateHTMLForMethods  (mb, sd)
        generateHTMLForEventHandlers  (mb, sd)
      }
    }
  }

  /**
   * Loads the descriptions from the given location.
   * @param loc The location to load from.
   */
  protected loadDescriptionsFrom (String loc) {
    new FileSystemDescriptionLoader().loadDescriptionsFromLocation(loc, this)
  }

}
