package org.gentsim.framework

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestEntity {
  
  def "Test creating base entities"() {
    setup:
      def ed = new EntityDescription("type")
      def e1 = new Entity(ed, 1)

    expect:
      e1.id == 1
      ed.type == "type"
  }

  def "Test entity handle all events of type"() {
    setup:
      def ed = new EntityDescription("type")
      ed.attribute "value", 1
      ed.handleEvent ("someevent") { event ->
        // NOTE:  you cannot use "this" because the following must refer to the delegate.
        value = event.value
      }

      def evtd = new EventDescription("someevent")
      evtd.attribute "value", 5

    when:
      def evt = new Event(evtd, 2)
      def e1 = new Entity(ed, 1)
      def e2 = new Entity(ed, 2)
      e1.handleEvent(evt)

    then:
      e1.value == 5
      e2.value == 1

    when:
      e1.handleEvent(new Event(new EventDescription("unknown"), 2))

    then:
      thrown(NoSuchMethodException)
  }
  
  def "Test service handle all events of type"() {
    setup:
      def sd = new ServiceDescription("service")
      sd.attribute "value", 1
      sd.handleEvent ("someevent") { event ->
        // NOTE:  you cannot use "this" because the following must refer to the delegate.
        value = event.value
      }

      def evtd = new EventDescription("someevent")
      evtd.attribute "value", 5

    when:
      def evt = new Event(evtd, 2)
      def s1 = new Service(sd)
      def s2 = new Entity(sd, 2)
      s1.handleEvent(evt)

    then:
      s1.value == 5
      s2.value == 1

    when:
      s1.handleEvent(new Event(new EventDescription("unknown"), 2))

    then:
      thrown(NoSuchMethodException)
  }

  def "Test entity handle events with pattern"() {
    setup:
      def ed = new EntityDescription("type")
      ed.attribute "called", 0
      ed.handleEvent (/.*event/) { event ->
        delegate.called += 1
      }
      def ent = new Entity(ed, 1)

    when:
      def evt = new Event(new EventDescription("someevent"), 1)
      ent.handleEvent(evt)
    then:
      ent.called == 1

    when:
      evt = new Event(new EventDescription("anotherevent"), 1)
      ent.handleEvent(evt)
    then:
      ent.called == 2

  }

  def "Test entity event handlers with value conditions"() {
    // TODO
  }

  def "Test entity handlers for other entity creation"() {
    setup:
      def ed = new EntityDescription("type")
      ed.attribute "value", 1
      ed.handleEntityCreated ("type") { entity ->
        // NOTE:  you cannot use "this" because the following must refer to the delegate.
        value = entity.value
      }

      def e1 = new Entity(ed, 1)
      e1.value = 1
      def e2 = new Entity(ed, 2)
      e2.value = 2

      
      def evtd = new EventDescription("entity-created")
      evtd.attribute "entity_type"
      evtd.attribute "entity"

    expect:
      e1.value == 1
      e2.value == 2

    when:
      e1.handleEvent(new Event(evtd, 3, ["entity_type": "type", "entity": e2]))

    then:
      e1.value == 2
      e2.value == 2

    when:
      e1.handleEvent(new Event(evtd, 3, ["entity_type": "unknown", "entity": e2]))

    then:
      thrown(NoSuchMethodException)
  }

  def "Test entity handlers for other entity destruction"() {
    setup:
      def ed = new EntityDescription("type")
      ed.attribute "value", 1
      ed.handleEntityDestroyed ("type") { entity ->
        // NOTE:  you cannot use "this" because the following must refer to the delegate.
        value = entity.value
      }

      def e1 = new Entity(ed, 1)
      e1.value = 1
      def e2 = new Entity(ed, 2)
      e2.value = 2

      def evtd = new EventDescription("entity-destroyed")
      evtd.attribute "entity_type"
      evtd.attribute "entity"

    expect:
      e1.value == 1
      e2.value == 2

    when:
      e1.handleEvent(new Event(evtd, 3, ["entity_type": "type", "entity": e2]))

    then:
      e1.value == 2
      e2.value == 2

    when:
      e1.handleEvent(new Event(evtd, 3, ["entity_type": "unknown", "entity": e2]))

    then:
      thrown(NoSuchMethodException)
  }

  def "Test entity handlers for other entity state changes"() {
    setup:
      def ed = new EntityDescription("type")
      ed.attribute "value", 1
      ed.handleEntityStateChanged ("type", "someattribute") { entity ->
        // NOTE:  you cannot use "this" because the following must refer to the delegate.
        value = entity.value
      }

      def e1 = new Entity(ed, 1)
      e1.value = 1
      def e2 = new Entity(ed, 2)
      e2.value = 2

      def evtd = new EventDescription("entity-state-changed")
      evtd.attribute "entity_type"
      evtd.attribute "entity"
      evtd.attribute "changed_attributes"

    expect:
      e1.value == 1
      e2.value == 2

    when:
      e1.handleEvent(new Event(evtd, 3, ["entity_type": "type", 
                                         "entity": e2, 
                                         "changed_attributes": ["someattribute", "anotherattribute"]
                                        ]
                              )
                    )

    then:
      e1.value == 2
      e2.value == 2

    when:
      e1.handleEvent(new Event(evtd, 3, ["entity_type": "unknown", "entity": e2]))

    then:
      thrown(NoSuchMethodException)

  }
  
  def "Test calling methods on the entity"() {
    setup:
      def ed = new EntityDescription("type")
      ed.method ("callme") { return false }
      ed.method ("returnme") { x -> return x }
    
    when:
      def e = new Entity(ed, 1)

    then:
      !e.callme() 
      e.returnme(3) == 3

    when:
      e.unknown()

    then:
      thrown(MissingMethodException)

    when:
      // now try calling another method from a called method.  
      // Note that this is defined *after* the entity creation.
      ed.method ("callandcallagain") { x ->
        return returnme(x)
      }
    
    then:
      e.callandcallagain(23) == 23
  }

  def "Test a handler calling a method" () {
    setup:
      def entd = new EntityDescription("entity")
      entd.attribute "called", false
      entd.handleEvent ("event") { evt -> callMethod() }
      entd.method ("callMethod") { called = true }
      def ent = new Entity(entd, 1)

    when:
      ent.handleEvent (new Event(new EventDescription("event"), 2))
    then:
      ent.called == true
  }

  def "Test using a service"() {
    setup:
      def entd = new EntityDescription("entity")
      def svc = ["type": "svc_type", "name" : "service1"]
      def ent = new Entity(entd, 1)

    when:
      ent.setService(svc)
    then:
      ent.svc_type.name == "service1"
  }


}

