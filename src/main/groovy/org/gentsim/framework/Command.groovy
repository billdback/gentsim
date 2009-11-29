/*
Copyright © 2009 William D. Back
This file is part of gentsim.

    gentsim is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    gentsim is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with gentsim.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gentsim.framework

/**
 * Commands are special cases of events that are targeted to specific entities.
 */
class Command extends Event implements Serializable {

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

