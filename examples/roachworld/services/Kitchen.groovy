import org.gentsim.framework.ServiceDescription

kitchen = new ServiceDescription("kitchen")

kitchen.parameter "length", 5  // y-coordinate 
kitchen.parameter "width",  10 // x-coordinate 
kitchen.parameter "dropped_food", []  // list of all the dropped food - may want to create maps for faster lookup.

kitchen.handleEntityCreated ("dropped-food") { food ->
  dropped_food << food
}

kitchen.handleEntityDestroyed ("dropped-food") { food ->
  dropped_food = dropped_food - food
}

// Returns a list of coordinates to the nearest food or null if there is no food.
// Note that the food may be gone by the time the roach gets there.
kitchen.method ("routeToNearestFood") { roach_location ->
  // run through all of the dropped food and find the closest.
  // since the kitchen is an x-y grid, the sum of the distances is the closest.
  def current_closest_distance
  def current_closest_food
  for (def f in dropped_food) {
    def distance = Math.abs(roach_location[0] - f.location[0]) + Math.abs(roach_location[1] - f.location[1])
    if (current_closest_distance == null || distance < current_closest_distance) {
      current_closest_distance = distance
      current_closest_food = f
    }
  }

  if (current_closest_food) {
    route = []
    // Go straight up [1]...
    def first = true // the first entry is the current location, so skip.  This is easier than
                     // determining the first position, which could be up, down, or the same.
    (roach_location[1]..current_closest_food.location[1]).each {
      if (first) first = false
      else route << [roach_location[0], it]
    }
    // ...then over [0]
    first = true
    ((roach_location[0])..current_closest_food.location[0]).each {
      if (first) first = false
      else route << [it, current_closest_food.location[1]]
    }
  }

  route
}

// comparator for comparing food amounts. 
private mc = [compare:{f1, f2 -> f1.amount.equals(f2.amount) ? 0: f1.amount < f2.amount ? -1: 1}] as Comparator

// returns either the biggest food at the given location or null if there is none.
kitchen.method ("foodAt") { location ->
  def foods = dropped_food.findAll { it.location == location }
  def food = foods.max(mc)
  (food?.amount > 0) ? food : null
}
