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
class NormalRandom extends Random {

  /**
   * Specifies the lower bound (inclusive) of random values to return.
   */
  private double mean

  /**
   * Specifies the upper bound (inclusive) of random values to return.
   */
  private double standardDeviation = 0.0

  /**
   * Creates a random number that returns normal random numbers.
   * @param mean The mean around which the numbers are distributed.
   * @param stddev The standard deviation for the distribution.
   */
  NormalRandom (double mean, double stddev) {
    this.mean = mean
    this.standardDeviation = stddev
  }

  /**
   * Returns the next random number.
   * @return The next random number.
   */
  double nextDouble() {
    // next double give a number from 0.0 to 1.0
    super.rand.nextGaussian() * this.standardDeviation + this.mean
  }

}
