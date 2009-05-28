import org.gentsim.framework.EntityDescription

// A messy person drops food at random. 
person = new EntityDescription("messy-person")

person.usesService "kitchen"
person.avg_drop_frequency = 0.8 // how often, on average as percent, the person drops some food.
person.avg_drop_size      = 50  // how big, on average, the dropped food is.

person.parameter "random", { new Random() }

person.handleTimeUpdate { time ->
  // See if this is one of those times that food is dropped.
  // TODO Add a random library to the simulation environment.
  if (Math.abs(random.nextFloat()) < avg_drop_frequency) {
    // TODO Add random location and amount.
    def x = Math.abs(random.nextInt() % kitchen.width)
    def y = Math.abs(random.nextInt() % kitchen.length)
    def amt = Math.round(Math.abs(random.nextGaussian() * avg_drop_size))
    newEntity("dropped-food", ["location": [x, y], "amount": amt])
  }

}

