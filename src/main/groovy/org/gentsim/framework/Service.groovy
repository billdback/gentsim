package org.gentsim.framework

/**
 * Defines all of the base capabilities of a service.  Services are special entities to
 * the container.
 */
class Service extends Entity {
  
  /**
   * Creates a new service.
   * @param sd The service description.
   * @param attrs Map of attributes and values.  This allows overwriting of the defaults.
   */
  Service (ServiceDescription sd, Map attrs = null) {
    super (sd, -1, attrs)
  }

}

