import org.gentsim.framework.*

// this must be run from the current directory to work.
def rwsim = new TimeSteppedSimulation(["events", "entities", "services"]) 

// Create the individual services and things.
def kitchen = rwsim.newService("kitchen")
rwsim.newEntity("messy-person")
rwsim.newEntity("ui")
rwsim.newEntity("roach-world-analyzer")

// Create some random roaches to start with.
def random = new Random()
(1..200).each {
  def x = Math.abs(random.nextInt() % kitchen.width)
  def y = Math.abs(random.nextInt() % kitchen.length)
  rwsim.newEntity("roach", ["location" : [x, y]])
}

// start the simulation
rwsim.run(10)
