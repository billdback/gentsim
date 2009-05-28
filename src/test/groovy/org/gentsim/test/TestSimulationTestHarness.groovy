package org.gentsim.test

import org.gentsim.framework.*

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

@Speck
@RunWith(Sputnik)
class TestSimulationTestHarness {

  // assumes this is run from the gentsim home.
  @Shared sth = new SimulationTestHarness(["/entities",
                                           "/events",
                                           "/services"
                                          ])

  def "Test create new entity"() {
    when:
      def cat = sth.newEntity("cat")
    then:
      cat.type == "cat"
//      cat.size == AnimalSize.small
  }

  def "Test create service"() {
    when:
      def dateSvc = sth.newService("date")
    then:
      dateSvc.type == "date"
      dateSvc.getDate().class.name == "java.util.Date"
  }

  def "Test sending an event to an entity"() {
    setup:
      def entd = new EntityDescription("entity")
      entd.attribute "called", false
      entd.attribute "value", 0
      entd.handleEvent ("test-event") { evt -> called = true; value = evt.value }
      sth.addEntityDescription(entd)

      def ent = sth.newEntity("entity")
      
    when:
      sth.sendEventTo (ent, "test-event", ["value" : 12])
    then:
      ent.called
      ent.value == 12
  }

  def "Test sending a new entity to an entity"() {
    setup:
      def entd = new EntityDescription("entity")
      entd.attribute "called", false
      entd.attribute "value", 0
      entd.handleEntityCreated ("entity") { entity -> called = true; value = entity.value }
      sth.addEntityDescription(entd)

      def ent = sth.newEntity("entity")
      def ent2 = sth.newEntity("entity")
      ent2.value = 12
      
    when:
      sth.sendCreatedEntityTo (ent, ent2)
    then:
      ent.called
      ent.value == 12
  }

  def "Test sending a destroyed entity to an entity"() {
    setup:
      def entd = new EntityDescription("entity")
      entd.attribute "called", false
      entd.attribute "value", 0
      entd.handleEntityDestroyed ("entity") { entity -> called = true; value = entity.value }
      sth.addEntityDescription(entd)

      def ent = sth.newEntity("entity")
      def ent2 = sth.newEntity("entity")
      ent2.value = 12
      
    when:
      sth.sendDestroyedEntityTo (ent, ent2)
    then:
      ent.called
      ent.value == 12
  }

  def "Test sending an entity state changed to an entity"() {
    setup:
      def entd = new EntityDescription("entity")
      entd.attribute "called", false
      entd.attribute "value", 0
      entd.handleEntityStateChanged ("entity", "value") { entity -> called = true; value = entity.value }
      sth.addEntityDescription(entd)

      def ent = sth.newEntity("entity")
      def ent2 = sth.newEntity("entity")
      ent2.value = 12
      
    when:
      sth.sendChangedEntityTo (ent, ent2, ["value"])
    then:
      ent.called
      ent.value == 12
  }

  def "Test sending a time update to an entity"() {
    setup:
      def entd = new EntityDescription("entity")
      entd.attribute "called", false
      entd.attribute "time", 0
      entd.handleTimeUpdate { time -> delegate.called = true; delegate.time = time }
      sth.addEntityDescription(entd)

      def ent = sth.newEntity("entity")
      
    when:
      sth.sendTimeUpdateTo (ent, 12)
    then:
      ent.called
      ent.time == 12
  }

  def "Test invoking a method on an entity"() {
    setup:
      def entd = new EntityDescription("entity")
      entd.attribute "called", false
      entd.attribute "param", 0
      entd.method ("mthd1") { delegate.called = true }
      entd.method ("mthd2") { delegate.param = it }
      sth.addEntityDescription(entd)

      def ent = sth.newEntity("entity")
      
    when:
      ent.mthd1()
    then:
      ent.called == true

    when:
      ent.mthd2(10)
    then:
      ent.param == 10
  }
}

