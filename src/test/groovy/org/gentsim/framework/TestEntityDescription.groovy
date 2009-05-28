package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestEntityDescription {
  
  def "Test blank entity" () {
    setup:   
      def e = new EntityDescription("BlankEntity")
    expect:
      e.type == "BlankEntity"
      e.attributes == [:]
      e.parameters == [:]
      e.eventHandlers == [:]
  }

  def "Test adding methods" () {
    setup:
      def e = new EntityDescription("MethodEntity")
      e.method ("testMethod") { a, b -> return a == b + 1 }
    expect:
      e.methods.testMethod(2, 1)
  }

  def "Test event handler with no conditions" () {
    setup:
      def e = new EntityDescription("EventHandler")
      e.handleEvent("event") { event -> event == "event OK" }

      def evtd = new EventDescription ("event")
      evtd.attribute "value"
      def evt = new Event(evtd, 12)

    when:
      evt.value = 1
    then:
      e.handlesEvent(evt)

    when:
      evt.value = 2
    then:
      e.handlesEvent(evt)
  }

  def "Test event handler with one simple condition" () {
    setup:
      def e = new EntityDescription("EventHandler")
      e.handleEvent("event1", ["value": 2]) { event -> event == "event OK" }

      def evtd = new EventDescription ("event1")
      evtd.attribute "value"
      def evt = new Event(evtd, 12)

    when:
      evt.value = 1
    then:
      !e.handlesEvent(evt)
      
    when:
      evt.value = 2
    then:
      e.handlesEvent(evt)
  }

  def "Test event handler with one closure condition" () {
    setup:
      def e = new EntityDescription("EventHandler")
      e.handleEvent("event2", ["value": { v -> v == 2}]) { event -> event == "event OK" }

      def evtd = new EventDescription ("event2")
      evtd.attribute "value"
      def evt = new Event(evtd, 12)

    when:
      evt.value = 1
    then:
      !e.handlesEvent(evt)
    when:
      evt.value = 2
    then:
      e.handlesEvent(evt)
  }

  def "Test event handler with multiple conditions" () {
    setup:
      def e = new EntityDescription("EventHandler")
      e.handleEvent("rect1", ["length": 2]) { event -> event == "event OK" }
      e.handleEvent("rect1", ["width": 3]) { event -> event == "event OK" }

      def evtd = new EventDescription ("rect1")
      evtd.attribute "length"
      evtd.attribute "width"
      def evt = new Event(evtd, 12)

    when:
      evt.length = 5
      evt.width  = 5
    then:
      !e.handlesEvent(evt)

    when:
      evt.length = 2
      evt.width  = 5
    then:
      e.handlesEvent(evt)

    when:
      evt.length = 5
      evt.width  = 3
    then:
      e.handlesEvent(evt)

    when:
      evt.length = 2
      evt.width  = 3
    then:
      e.handlesEvent(evt)
  }

  def "Test event handler with multiple closure conditions" () {
    setup:
      def e = new EntityDescription("EventHandler")
      e.handleEvent("rect1", ["length": {l -> l >= 1 && l <=2 } ]) { event -> event == "event OK" }
      e.handleEvent("rect1", ["width": {w -> w >= 3 && w <= 4} ]) { event -> event == "event OK" }

      def evtd = new EventDescription ("rect1")
      evtd.attribute "length"
      evtd.attribute "width"
      def evt = new Event(evtd, 12)

    when:
      evt.length = 5
      evt.width  = 5
    then:
      !e.handlesEvent(evt)

    when:
      evt.length = 2
      evt.width  = 5
    then:
      e.handlesEvent(evt)

    when:
      evt.length = 5
      evt.width  = 3
    then:
      e.handlesEvent(evt)

    when:
      evt.length = 2
      evt.width  = 3
    then:
      e.handlesEvent(evt)
  }

  def "Test event handler with pattern" () {
    setup:
      def e = new EntityDescription("EventHandler")
      e.handleEvent(/.*evt.*/) { event -> event == "event OK" }

    expect:
      e.handlesEvent(new Event(new EventDescription("evt"), 1))
      e.handlesEvent(new Event(new EventDescription("xxevt"), 2))
      e.handlesEvent(new Event(new EventDescription("evtxx"), 3))
      e.handlesEvent(new Event(new EventDescription("xxevtxx"), 4))
      !e.handlesEvent(new Event(new EventDescription("xxexlxvtxx"), 5))

  }

  def "Test entity created handlers" () {
    setup:
      def e = new EntityDescription("entity-created")
    expect:
      !e.eventHandlers["entity"]
    when:
      // matches the actual entity created event.
      def evtd = new EventDescription("entity-created")
      evtd.attribute "entity_type", "entity"
      e.handleEntityCreated ("entity") { evt -> "entity created" }
      def evt = new Event(evtd, 12)
    then:
      e.handlesEvent (evt)
    
    when:
      evt.entity_type = "unknown_entity"
    then:
      !e.handlesEvent (evt)
  }

  def "Test entity destroyed handlers" () {
    setup:
      def e = new EntityDescription("EntityDestroyedHandler")
    expect:
      !e.eventHandlers["entity"]
    when:
      // matches the actual entity destroyed event.
      def evtd = new EventDescription("entity-destroyed")
      evtd.attribute "entity_type", "entity"
      e.handleEntityDestroyed ("entity") { evt -> "entity destroyed" }
      def evt = new Event(evtd, 12)
    then:
      e.handlesEvent (evt)
    
    when:
      evt.entity_type = "unknown_entity"
    then:
      !e.handlesEvent (evt)
  }

  def "Test entity state change handlers for any attribute" () {
    setup:
      def ed = new EntityDescription("EntityStateChanged")
      ed.handleEntityStateChanged ("entity") { evt -> "attrs changed" }

      def evtd = new EventDescription("entity-state-changed")
      evtd.attribute "entity_type", "entity"
      def evt = new Event(evtd, 12)

    when:
      evt.changed_attributes = []
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr1"]
    then:
      ed.handlesEvent(evt)
  }

  def "Test entity state change handlers for list of attributes" () {
    setup:
      def ed = new EntityDescription("EntityStateChanged")
      ed.handleEntityStateChanged ("entity", ["attr1", "attr2", "attr3"]) { evt -> "entity changed" }

      def evtd = new EventDescription("entity-state-changed")
      evtd.attribute "entity_type", "entity"
      def evt = new Event(evtd, 12)

    when:
      evt.changed_attributes = []
    then:
      !ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr1"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr2"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr4"]
    then:
      !ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr1", "attr2", "attr3"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr3", "attr4"]
    then:
      ed.handlesEvent(evt)
  }

  def "Test entity state change handlers for single attributes" () {
    setup:
      def ed = new EntityDescription("EntityStateChanged")
      ed.handleEntityStateChanged ("entity", "attr1") { evt -> "attr1 changed" }
      ed.handleEntityStateChanged ("entity", "attr2") { evt -> "attr2 changed" }
      ed.handleEntityStateChanged ("entity", "attr3") { evt -> "attr3 changed" }

      def evtd = new EventDescription("entity-state-changed")
      evtd.attribute "entity_type", "entity"
      def evt = new Event(evtd, 12)

    when:
      evt.changed_attributes = []
    then:
      !ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr1"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr2"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr4"]
    then:
      !ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr1", "attr2", "attr3"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr3", "attr4"]
    then:
      ed.handlesEvent(evt)
  }

  def "Test entity state change handlers for pattern attributes" () {
    setup:
      def ed = new EntityDescription("EntityStateChanged")
      ed.handleEntityStateChanged ("ent.*", "attr.") { evt -> "attr changed" }

      def evtd = new EventDescription("entity-state-changed")
      evtd.attribute "entity_type", "entity"
      def evt = new Event(evtd, 12)

    when:
      evt.changed_attributes = []
    then:
      !ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr1"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr2"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["attr1", "attr2", "attr3"]
    then:
      ed.handlesEvent(evt)

    when:
      evt.changed_attributes = ["axttr3"]
    then:
      !ed.handlesEvent(evt)
  }

  def "Test entity using a service" () {
    setup:
      def e = new EntityDescription("EntityThatUsesAService")
    expect:
      !e.services["someservice"]
    when:
      e.usesService("someservice")
    then:
      e.services.contains("someservice")
  }

}

