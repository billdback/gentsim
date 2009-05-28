import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

import org.gentsim.framework.*
import org.gentsim.test.*

@Speck
@RunWith(Sputnik)
class TestRoach {

  def sth = new SimulationTestHarness(["../services", "../entities"])

  def "Test creating new roach" () {
    setup:
      def roach = sth.newEntity("roach")
    
    expect:
      roach.state == RoachState.moving
      roach.size == RoachSize.tiny
      roach.energy == RoachSize.tiny.maxEnergy
      roach.location == [0, 0]
      roach.route == []
      roach.foodBeingEaten == null
  }

  def "Test max energy"() {
    setup:
      def roach = sth.newEntity("roach")

    when:
      roach.size = RoachSize.tiny
    then:
      roach.maxEnergy() == 10

    when:
      roach.size = RoachSize.big
    then:
      roach.maxEnergy() == 40
  }

  def "Test using energy" () {
    when:
      def roach = sth.newEntity("roach")
      def e = roach.energy
      roach.state = RoachState.moving
      roach.useEnergy()
    then:
      e - roach.state.baseEnergy == roach.energy
  }

  def "Test eating no food" () {
    setup:
      def roach = sth.newEntity("roach")
    
    when:
      roach.state == RoachState.eating
      roach.foodBeingEaten = null
      roach.eat()
    then:
      roach.state == RoachState.moving

    when:
      def food = sth.newEntity("dropped-food")
      food.amount = 0
      roach.foodBeingEaten = food
      roach.state = RoachState.eating
      roach.eat()
    then:
      roach.state == RoachState.moving   
  }

  def "Test eating food but remaining hungry" () {
    setup:
      def roach = sth.newEntity("roach")
      def food = sth.newEntity("dropped-food")
      food.amount = 100
    
    when:
      roach.state = RoachState.eating
      roach.size = RoachSize.big
      roach.energy = 5
      roach.foodBeingEaten = food
      roach.eat()
    then:
      roach.state == RoachState.eating
      roach.energy == 5 + roach.size.biteSize
  }

  def "Test eating and then growing" () {
    setup:
      def roach = sth.newEntity("roach")
      def food = sth.newEntity("dropped-food")
      food.amount = 100
    
    when:
      roach.state = RoachState.eating
      roach.size  = RoachSize.medium
      roach.energy = roach.size.maxEnergy - 1
      roach.foodBeingEaten = food
      roach.eat()
    then:
      roach.state == RoachState.growing
      roach.size.maxEnergy == roach.energy
  }

  def "Test eating and then reproducing" () {
    setup:
      def roach = sth.newEntity("roach")
      def food = sth.newEntity("dropped-food")
      food.amount = 100
    
    when:
      roach.state = RoachState.eating
      roach.size  = RoachSize.huge
      roach.energy = roach.size.maxEnergy - 1
      roach.foodBeingEaten = food
      roach.eat()
    then:
      roach.state == RoachState.reproducing
      roach.size.maxEnergy == roach.energy
  }

  def "Test growing" () {
    setup:
      def roach = sth.newEntity("roach")
      
    expect:
      // Roach assumes huge is the max size.
      RoachSize.huge.ordinal() == RoachSize.values().length - 1 
      roach.size == RoachSize.tiny 

    when:
      roach.grow()
    then:
      roach.size == RoachSize.small

    when:
      roach.size = RoachSize.huge
      roach.grow()
    then:
     roach.size == RoachSize.huge
  }

  def "Test moving without route" () {
    setup:
      def roach = sth.newEntity("roach")
      def mroute = [ [0, 0], [0,1], [1, 1]] 
      roach.kitchen = sth.newService("kitchen")
    expect:
      !roach.route

    when:
      roach.move()
    then:
      roach.route = mroute
  }

  def "Test moving with route" () {
    setup:
      def roach = sth.newEntity("roach")
      roach.route = [ [0, 0], [0,1], [1, 1]] 
      roach.kitchen = ["foodAt": { false }]

    // Follow the route to the end.
    when:
      roach.move()
    then:
      roach.location == [0, 0]

    when:
      roach.move()
    then:
      roach.location == [0, 1]

    when:
      roach.move()
    then:
      roach.location == [1, 1]
      roach.route == []
  }

  def "Test reproducing" () {
    expect:
      sth.getEntitiesOfType("roach") == []

    when:
      def roach = sth.newEntity("roach")
    then:
      sth.getEntitiesOfType("roach").size == 1
      roach.state == RoachState.moving
    
    when:
      roach.reproduce()
    then:
      sth.getEntitiesOfType("roach").size == 2
      roach.state == RoachState.moving
  }

}

