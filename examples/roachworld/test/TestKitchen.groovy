import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

import org.gentsim.framework.*
import org.gentsim.test.*

@Speck
@RunWith(Sputnik)
class TestKitchen {

  def sth = new SimulationTestHarness(["../services", "../entities"])

  def "Test create dropped food" () {
    setup:
      def kitchen = sth.newService("kitchen")
      
      (1..10).each { sth.sendCreatedEntityTo(kitchen, sth.newEntity("dropped-food")) }
    expect:
      kitchen.dropped_food.size == 10
  }

  def "Test get directions to the nearest food" () {
    setup:
      def kitchen = sth.newService("kitchen")
      def food = sth.newEntity("dropped-food", ["location": [2,4]])
      sth.sendCreatedEntityTo(kitchen, food)

      food = sth.newEntity("dropped-food", ["location": [3,2]])
      sth.sendCreatedEntityTo(kitchen, food)

      food = sth.newEntity("dropped-food", ["location": [2,3]])
      sth.sendCreatedEntityTo(kitchen, food)

      food = sth.newEntity("dropped-food", ["location": [2,1]])
      sth.sendCreatedEntityTo(kitchen, food)

    when:
      def roachLocation = [0, 0]
      def route = kitchen.routeToNearestFood(roachLocation)
    then:
      route.size == 3
      route[0] == [0, 1]
      route[1] == [1, 1]
      route[2] == [2, 1]

    when:
      roachLocation = [6, 6]
      route = kitchen.routeToNearestFood(roachLocation)

    then:
      route.size == 6
      route[0] == [6, 5]
      route[1] == [6, 4]
      route[2] == [5, 4]
      route[3] == [4, 4]
      route[4] == [3, 4]
      route[5] == [2, 4]
  }

  def "Test checking a position for food" () {
    setup:
      def kitchen = sth.newService("kitchen")
      //def food = sth.newEntity("dropped-food", ["location": [2,4], "amount": 100])
      def food = sth.newEntity("dropped-food")
      food.location = [2, 4]
      food.amount = 100
      sth.sendCreatedEntityTo(kitchen, food)

      food = sth.newEntity("dropped-food", ["location": [6,6]]) // no amount, so no food.
      sth.sendCreatedEntityTo(kitchen, food) 

    expect:
      kitchen.foodAt([2,4]) != null
      kitchen.foodAt([6,6]) == null
      kitchen.foodAt([1,3]) == null
      kitchen.foodAt([2,5]) == null
      kitchen.foodAt([4,4]) == null
  }

}

