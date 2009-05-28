package org.gentsim.framework

/**
 * Commands are special cases of events that are targeted to specific entities.
 */
class Command extends Event {

  /** Target of the command. */
  private target

  /**
   * Creates an instance of an command.
   * @param cd The description for the command.
   * @param id The unique id of the command.
   * @param tgt The target of the command (id or list of ids).
   * @param attrs Map of attributes and values.  This allows overwriting of the defaults.
   */
  Command (EventDescription cd, id, tgt, Map attrs = null) {
    super(cd, id, attrs)
    this.target = tgt
  }

  /**
   * Creates an instance of an command.
   * @param cd The description for the command.
   * @param id The unique id of the command.
   * @param tgt The target of the command (id or list of ids).
   * @param time The time the command is to be executed.
   * @param attrs Map of attributes and values.  This allows overwriting of the defaults.
   */
  Command (EventDescription cd, id, tgt, int time, Map attrs = null) {
    super(cd, id, time, attrs)
    this.target = tgt
  }
}

