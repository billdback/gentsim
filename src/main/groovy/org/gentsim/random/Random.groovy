/*
Copyright © 2010 William D. Back
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
package org.gentsim.random

/**
 * Base class.  These classes wrap the standard Java Random class to make it more natural to use.
 * TODO Find a clean way to specify the types of return values and just call next()
 * @author Bill Back
 */
abstract class Random {

  /**
   * Random number generator to be used by distribution classes.
   */
  protected java.util.Random rand
  
  /**
   * Creates a new random number generator.
   */
  Random() {
    this.rand = new java.util.Random()
  }

  /**
   * Creates a new random number generator with the given seed value.
   * @param seed The seed to use for random number generation.
   */
  Random (long seed) {
    this.rand = new java.util.Random(seed)
  }

  /**
   * Returns the next random number as a long integer.
   * @return The next random number.
   */
  long nextLong() {
    Math.round(nextDouble())
  }

  /**
   * Returns the next random number as a double.
   * @return The next random number.
   */
  abstract double nextDouble()

  /**
   * Sets the see for random number generation.
   * @param seed The seed to use for random number generation.
   * @return The random number.  This is returned to support chaining.
   */
  Random setSeed (long seed) {
    this.rand.setSeed(seed)
  }

}
