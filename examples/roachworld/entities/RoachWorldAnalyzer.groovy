import org.gentsim.framework.EntityDescription

rwa = new EntityDescription("roach-world-analyzer")

rwa.roaches_created = 0
rwa.roaches_died    = 0

rwa.handleEntityCreated ("roach") {
  roaches_created += 1
}

rwa.handleEntityDestroyed ("roach") {
  roaches_died += 1
}

rwa.handleEvent ("system.shutdown") {
  attributes.each { att -> println "${att.key}: ${att.value}" }
}
