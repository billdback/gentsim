package org.gentsim.framework

/**
 * This class is used to describe simulation services.  Services are a special case of entities.
 */
class ServiceDescription extends EntityDescription {

  /**
   * Creates a new service that can be instantiated in the simulation.
   * @param type The unique type of the service.
   * @param attrs A map of attribute values.
   * @throws IllegalArgumentException Thrown if the type is invalid.
   */
  ServiceDescription (String type, Map attrs = null) throws IllegalArgumentException {
    super(type, attrs)
  }


}

