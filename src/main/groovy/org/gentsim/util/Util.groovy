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
package org.gentsim.util

/**
 * Basic utilities for use with the simulation.
 */
class Util {
  
  /**
   * This reads the contents of a file and returns a string.
   */
  static String FileToString (File f) {
    return FileToStringBuffer(f).toString()
  }
  
  /**
   * This reads the contents of a file into a FileBuffer.
   */
  static StringBuffer FileToStringBuffer (File f) {
    StringBuffer sb = new StringBuffer()
    FileInputStream fis = new FileInputStream(f)
    def c
    while ((c = fis.read()) != -1) sb.append((char)c)
    return sb
  }
  
}
