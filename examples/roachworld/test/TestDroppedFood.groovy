import org.junit.runner.RunWith
import spock.lang.*
import static spock.lang.Predef.*

import org.gentsim.framework.*
import org.gentsim.test.*

@Speck
@RunWith(Sputnik)
class TestDroppedFood {

  @Shared sth = new SimulationTestHarness("../entities")

  def "Test create dropped food" () {
    when:
      def df = sth.newEntity("dropped-food")
    then:
      !df.amount
      df.amount == 0
      df.location == [0, 0]
  }

  def testSetLocation () {
    when:
      def df = sth.newEntity("dropped-food")
    then:
      df.location == [0, 0]

    when:
      df.location = [10, 5]
    then:
      df.location == [10, 5]
  }

  def testTakeBiteEnoughRemaining () {
    when:
      def df = sth.newEntity("dropped-food")
      df.amount = 10
    then:
      df.takeBite(4) == 4
      df.amount == 6
  }

  def testTakeBiteNotEnoughRemaining () {
    when:
      def df = sth.newEntity("dropped-food")
      df.amount = 2
    then:
      sth.hasEntityWithId(df.id)
      df.amount == 2
      df.takeBite(4) == 2
      df.amount == 0
      !sth.hasEntityWithId(df.id)
  }

}

