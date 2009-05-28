package org.gentsim.tools

import org.gentsim.framework.*

/**
 * Creates documentation for entities and events.
 */
class Documentor extends SimulationContainer {

  def prepLocation (loc) {
    // if the docs folder doesn't exist, then create it.
    File f = new File(loc + "/docs")
    if (!f.exists()) f.mkdir()

    // TODO find this in the correct .jar location.
    new AntBuilder().copy(file: "../resources/css/gentsim.css", tofile: "${f.getAbsolutePath()}/gentsim.css")
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
        h1("Descriptions for ${loc}")

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

  static main (args) {
    if (!args) {
      println "usage:  Documentor <scripts-location>"
    }
    else {
      Documentor doc = new Documentor()
      println "Creating documentation from ${args[0]}"
      doc.loadDescriptionsFrom(args[0])
      doc.generateHTML(args[0])
    }
  }
}
