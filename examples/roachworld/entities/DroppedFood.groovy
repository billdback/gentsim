import org.gentsim.framework.EntityDescription

droppedFood = new EntityDescription("dropped-food")

// Remaining amount of food in energy points.
droppedFood.amount   = 0
droppedFood.location = [0, 0]

droppedFood.method ("takeBite") { biteSize ->
  def actualBite = Math.min(biteSize, amount)
  amount = amount - actualBite
  if (amount <= 0) destroy()
  actualBite
}

