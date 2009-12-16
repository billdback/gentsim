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

import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*
import org.gentsim.util.Statistics

@Speck
@RunWith(Sputnik)
class TestSimulationContainer {

  def "Test getting entity descriptions" () {
    setup:
      SimulationContainer sc = new SimulationContainer()
      EntityDescription e = new EntityDescription("entity1")
      sc.addEntityDescription (e)

    when:
      def e2 = sc.getEntityDescription("entity1")
    then:
      e == e2
      e.type == "entity1"
  }

  def "Test getting all entity descriptions" () {
    setup:
      SimulationContainer sc = new SimulationContainer()
      sc.addEntityDescription (new EntityDescription("entity1"))
      sc.addEntityDescription (new EntityDescription("entity2"))

    expect:
      sc.entityDescriptions.size() == 2
  }
  
  def "Test for containing a certain entity description" () {
    setup:
      SimulationContainer sc = new SimulationContainer()
      EntityDescription e = new EntityDescription("entity2")

    when:
      sc.addEntityDescription (e)
    then:
      !sc.hasEntityDescription("not-there")
      sc.hasEntityDescription("entity2")
  }

  def "Test creating entities"() {
    setup:
      SimulationContainer sc = new SimulationContainer()
      def ed = new EntityDescription("createme")
      sc.addEntityDescription ed

    when:
      def e = sc.newEntity(ed.type)
    then:
      e.id == 1
      e.type == "createme"

    when:
      e = sc.getEntityWithId(1)
    then:
      e
      e.id == 1

    when:
      e = sc.getEntityWithId(2)
    then:
      !e
  }

  def "Test getting entities of a certain type" () {
    setup:
      SimulationContainer sc = new SimulationContainer()
      sc.addEntityDescription(new EntityDescription("type1"))
      sc.addEntityDescription(new EntityDescription("type2"))

      sc.newEntity("type1")
      sc.newEntity("type1")
      sc.newEntity("type1")
      sc.newEntity("type2")
      sc.newEntity("type2")

    expect:
      sc.getEntitiesOfType("type1").size == 3
      sc.getEntitiesOfType("type2").size == 2
      sc.getEntitiesOfType("type3").size == 0
  }

  def "Test creating entity attributes"() {
    setup:
      SimulationContainer sc = new SimulationContainer()
      def ed = new EntityDescription("fancyattributes")
      ed.attribute "set_attribute", 3.14
      ed.attribute ("calc_attribute", { 3+4 })
      sc.addEntityDescription ed
    
    when:
      def e = sc.newEntity("fancyattributes")
    then:
      e.set_attribute == 3.14
      e.calc_attribute == 7
  }

  def "Test removing entities"() {
    setup:
      SimulationContainer sc = new SimulationContainer()
      def ed = new EntityDescription("deleteme")
      sc.addEntityDescription ed

      def e = sc.newEntity(ed.type)
      def eid = e.id

    when:
      e = sc.getEntityWithId(eid)
    then:
      e

    when:
      e = sc.removeEntity(eid)
    then:
      e

    when:
      e = sc.getEntityWithId(eid)
    then:
      !e

    when:
      sc.removeEntity(eid)
    then:
      thrown(IllegalArgumentException)
  }

  def "Test creating a new service" () {
    setup:
      SimulationContainer sc = new SimulationContainer()
      def sd = new ServiceDescription("new service")

    expect:
      sd.parameter "param", 3

    when:
      sc.addServiceDescription(sd)
      def sdr = sc.getServiceDescription("new service")
    then:
      sd == sdr
      sd.parameters.param == 3

    when:
      // services are automatically created, so this should get the new one.
      def svc = sc.newService("new service")
    then:
      svc.type == "new service"
      svc.param == 3

    when:
      def svc2 = sc.getService("new service")
    then:
      svc == svc2

  }

  def "Test connecting services after creating entities"() {
    setup:
      SimulationContainer sc = new SimulationContainer()
      def ed1 = new EntityDescription("entity-1")
      ed1.usesService("service1")
      sc.addEntityDescription(ed1)
      def ed2 = new EntityDescription("entity-2")
      ed2.usesService("service2")
      sc.addEntityDescription(ed2)

    when:
      // Multiple instances of each type to make sure multiple are set.
      def ent1_1 = sc.newEntity("entity-1")
      def ent1_2 = sc.newEntity("entity-1")
      def ent2_1 = sc.newEntity("entity-2")
      def ent2_2 = sc.newEntity("entity-2")

    then:
      !ent1_1.services["service1"]
      !ent1_2.services["service1"]
      !ent2_1.services["service2"]
      !ent2_2.services["service2"]

    when:
      sc.addServiceDescription(new ServiceDescription("service1"))
      def svc1 = sc.newService("service1")

    then:
      ent1_1.services["service1"] == svc1
      ent1_2.services["service1"] == svc1
      !ent2_1.services["service2"]
      !ent2_2.services["service2"]

    when:
      sc.addServiceDescription(new ServiceDescription("service2"))
      def svc2 = sc.getService("service2")

    then:
      ent1_1.services["service1"] == svc1
      ent1_2.services["service1"] == svc1
      ent2_1.services["service2"] == svc2
      ent2_2.services["service2"] == svc2
  }

  def "Test connecting services when creating entities"() {
    setup:
      SimulationContainer sc = new SimulationContainer()
      def ed1 = new EntityDescription("entity-1")
      ed1.usesService("service1")
      sc.addEntityDescription(ed1)

      def ed2 = new EntityDescription("entity-2")
      ed2.usesService("service2")
      sc.addEntityDescription(ed2)

      sc.addServiceDescription(new ServiceDescription("service1"))
      def svc1 = sc.getService("service1")

      sc.addServiceDescription(new ServiceDescription("service2"))
      def svc2 = sc.getService("service2")

    when:
      // Multiple instances of each type to make sure multiple are set.
      def ent1_1 = sc.newEntity("entity-1")
      def ent1_2 = sc.newEntity("entity-1")
      def ent2_1 = sc.newEntity("entity-2")
      def ent2_2 = sc.newEntity("entity-2")

    then:
      ent1_1.services["service1"] == svc1
      ent1_2.services["service1"] == svc1
      ent2_1.services["service2"] == svc2
      ent2_2.services["service2"] == svc2
  }

  def "Test getting all service descriptions" () {
    setup:
      SimulationContainer sc = new SimulationContainer()
      sc.addServiceDescription (new ServiceDescription("svc1"))
      sc.addServiceDescription (new ServiceDescription("svc2"))

    expect:
      sc.serviceDescriptions.size() == 2
  }

  def "Test creation of events"() {
    setup:
      SimulationContainer sc = new SimulationContainer()
      def ed = new EventDescription("event")
      ed.attribute "attr", 2

    when:
      sc.addEventDescription(ed)
      def evt1 = sc.newEvent("event")
      def evt2 = sc.newEvent("event", ["attr" : 3])

    then:
      evt1.type == "event"
      evt1.attr == 2
      evt2.type == "event"
      evt2.attr == 3
  }

  def "Test creation of commands"() {
    setup:
      SimulationContainer sc = new SimulationContainer()
      def ed = new EventDescription("command")
      ed.attribute "attr", 2

    when:
      sc.addEventDescription(ed)
      def cmd2 = sc.newCommand("command", 2, ["attr" : 3])

    then:
      cmd2.type == "command"
      cmd2.attr == 3
      cmd2.target == 2
  }

  def "Test getting entities and services who handle events" () {
    setup:
      SimulationContainer sc = new SimulationContainer()

      def ed = new EntityDescription("IHandleEvents")
      ed.handleEvent("some-event") {}
      sc.addEntityDescription(ed)

      def sd = new ServiceDescription("IAlsoHandleEvents")
      sd.handleEvent("some-event") {}
      sc.addServiceDescription(sd)

      ed = new EntityDescription("IDontHandleEvents")
      sc.addEntityDescription(ed)

      sc.newEntity ("IHandleEvents")
      sc.newEntity ("IHandleEvents")
      sc.newEntity ("IHandleEvents")
      sc.newService ("IAlsoHandleEvents")
      sc.newEntity ("IDontHandleEvents")
      sc.newEntity ("IDontHandleEvents")
      
      def evt = new Event (new EventDescription("some-event"), 123)

      when:
        def evts = sc.getEntitiesWhoHandleEvent (evt)
      then:
        evts.size == 4
        evts[0].type =~ /I(Also)?HandleEvents/
        evts[1].type =~ /I(Also)?HandleEvents/
        evts[2].type =~ /I(Also)?HandleEvents/
        evts[3].type =~ /I(Also)?HandleEvents/
  }

  def "Test statistics"() {
    setup:
      SimulationContainer sc = new SimulationContainer()
      sc.addEntityDescription(new EntityDescription("some-entity"))
      sc.addServiceDescription(new ServiceDescription("some-service"))
      sc.addEventDescription(new EventDescription("some-event"))

   when:
     def ent = sc.newEntity("some-entity")
     sc.newEntity("some-entity")
     sc.newEntity("some-entity")
     sc.newService("some-service")
     sc.newService("some-service")
     sc.newService("some-service")
     sc.newEvent("some-event")
     sc.newEvent("some-event")
     sc.newEvent("some-event")
     sc.newCommand("some-event", 1)
     sc.newCommand("some-event", 2)
     sc.newCommand("some-event", 3)
     sc.removeEntity(ent)
   then:
     Statistics.instance.number_entity_descriptions == 1
     Statistics.instance.number_service_descriptions == 1
     Statistics.instance.number_event_descriptions == 1
     Statistics.instance.number_entities_created == 3
     Statistics.instance.number_entities_removed == 1
     Statistics.instance.number_services_created == 3
     Statistics.instance.number_events_created == 3
     Statistics.instance.number_commands_created == 3
  }

}

