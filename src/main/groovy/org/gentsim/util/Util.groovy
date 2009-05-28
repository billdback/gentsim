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
