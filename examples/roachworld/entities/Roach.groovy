import org.gentsim.framework.EntityDescription

roach = new EntityDescription("roach")

roach.state    = RoachState.moving
roach.size     = RoachSize.tiny
roach.energy   = roach.size.maxEnergy
roach.location = [0, 0]
roach.route    = []
roach.foodBeingEaten = null

roach.usesService "kitchen"

roach.method ("maxEnergy") {
  return size.maxEnergy
}

roach.handleTimeUpdate { time ->
  useEnergy()
  if (energy <= 0) destroy() // dead roach.
  else {

    switch (state) {
      case RoachState.eating:
        eat(); break
      case RoachState.growing:
        grow(); break
      case RoachState.moving:
        move(); break
      case RoachState.reproducing:
        reproduce(); break;
      default:
        println("whoops!  I didn't see that coming.  Invalid state ${state}"); // TODO throw an exception
    }
  }
}

/**
 * Use energy based on state.
 */
roach.method ("useEnergy") {
  energy -= state.baseEnergy
}


/** 
 * A roach will eat until it is full or the food runs out.
 * If the roach is full and it is huge, then it will reproduce.  If it is not huge, then it will grow.
 * If the food runs out, the roach will look for more food.
 */
roach.method ("eat") {
  // make sure there is more food
  if (foodBeingEaten == null || foodBeingEaten.amount == 0) {
    state = RoachState.moving
  }
  else {

    // if so, take a bite and increase energy
    energy += foodBeingEaten.takeBite(Math.min(size.biteSize, size.maxEnergy - energy))
  
    // if full, then either grow or reproduce.
    if (energy == size.maxEnergy) {
      if (size == RoachSize.huge) {
        state = RoachState.reproducing
      }
      else {
        state = RoachState.growing
      }
    }
  }
}

/**
 * The roach grows to the next size.
 */
roach.method ("grow") {
  if (size != RoachSize.huge) size = RoachSize.values()[size.ordinal() + 1];
}

/**
 * The roach will move until it finds food.
 */
roach.method ("move") {
  // if there is no current route, get one from the kitchen.
  if (route.size == 0) route = kitchen.routeToNearestFood(location)
  // now, move to the next spot in the route if not already there or there is nowhere to go.
  if (route.size > 0) location = route.remove(0)
  foodBeingEaten = kitchen.foodAt(location) // see if there is food
  if (foodBeingEaten != null) state = RoachState.eating
}

/**
 * Huge roaches will reproduce when full, then resume eating.
 */
roach.method ("reproduce") {
  newEntity("roach")
  state = RoachState.moving
}

