package org.gentsim.framework

class ContainedEntity extends Entity {

  /** The container of the entity. */
  private container

  /** 
   * Creates a new contained entity.
   * @param ed The entity description of the entity.
   * @param id The unique id for the entity.
   * @param container The simulation container of the entity.
   * @param attrs Attribute values to override the defaults.
   */
  ContainedEntity (EntityDescription ed, id, container, Map attrs = null) {
    super(ed, id, attrs)
    this.container = container
  }

  /*********** wrapper methods to make life easier **********/

  /**
   * Creates a new entity of the given type.
   * @param type The type of the entity type to create.
   * @param attrs Attribute values to override the defaults.
   */
  def newEntity (type, Map attrs = null) {
    return container.newEntity(type, attrs)
  }

  /**
   * Destroys the entity, removing it from the container.
   */
  def destroy () {
    return container.removeEntity(this.id)
  }

  /**
   * Posts an event to the container.
   * @param event The event to send.
   * @return this to allow for method chaining
   */
  def sendEvent (Event event) {
    container.sendEvent(event)
    this
  }

}

