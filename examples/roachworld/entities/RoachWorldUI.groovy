import org.gentsim.framework.EntityDescription

rwui  = new EntityDescription("ui")

rwui.handleTimeUpdate { time ->
  println "time: ${time}"
}

rwui.handleEntityCreated("roach") { roach ->
  println "roach ${roach.id} created at ${roach.location}"
}

rwui.handleEntityStateChanged ("roach", "location") { roach ->
  println "roach ${roach.id} moved to ${roach.location}"
}

rwui.handleEntityStateChanged ("roach", "state") { roach ->
  println "roach ${roach.id} changed state to ${roach.state}"
}

rwui.handleEntityDestroyed ("roach") { roach ->
  println "roach ${roach.id} is dead"
}

rwui.handleEntityCreated("dropped-food") { food ->
  println "${food.amount} food was dropped at ${food.location}"
}

rwui.handleEntityDestroyed("dropped-food") { food ->
  println "${food.id} food is gone"
}

