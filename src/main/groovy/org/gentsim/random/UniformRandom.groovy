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
 * Creates random numbers with a uniform distribution.
 * @author Bill Back
 */
class UniformRandom extends Random {

  /**
   * Specifies the lower bound (inclusive) of random values to return.
   */
  private lower

  /**
   * Specifies the upper bound (inclusive) of random values to return.
   */
  private upper

  /**
   * Difference in the upper and lower.  Used to adjust the generic random numbers into the range of values.
   */
  private rangeDiff

  /**
   * Creates a uniform random number generator with the given range.
   * @param range The range (inclusive) in which to generate numbers.
   */
  UniformRandom (Range range) {
    this(range.from, range.to)
  }

  /**
   * Returns a random number generator that returns numbers in the given range in a uniform distribution.
   * @param lower The lower bound (inclusive) of numbers.
   * @param upper The upper bound (inclusive) of numbers.
   * @return A uniform random number generator.
   */
  UniformRandom (double lower, double upper) {
    this.lower = lower
    this.upper = upper
    this.rangeDiff = upper - lower
  }

  /**
   * Returns the next random number.
   * @return The next random number.
   */
  double nextDouble() {
    // next double give a number from 0.0 to 1.0
    super.rand.nextDouble() * rangeDiff + lower
  }

  /**
   * Returns the next random number as a long integer.
   * @return The next random number.
   */
  long nextLong() {
    Math.abs(super.rand.nextLong()) % (long)rangeDiff + (long)lower
  }
}
