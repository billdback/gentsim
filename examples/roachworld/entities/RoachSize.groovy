/** Valid sizes for the roach. */
enum RoachSize { 
  tiny(10, 1), small(20, 2), medium(30, 3), big(40, 4), huge(50, 5) 

  def RoachSize (int maxEnergy, int biteSize) {
    this.maxEnergy = maxEnergy
    this.biteSize = biteSize
  }

  def maxEnergy
  def biteSize
}
