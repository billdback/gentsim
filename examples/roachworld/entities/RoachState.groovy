/**
 * These are the states the roach can be in.  The value is the base energy
 * cost for that state each time step.
 */
enum RoachState { 
  eating(0), growing(2), moving(1), reproducing(2) 

  final baseEnergy

  RoachState (be) {
    this.baseEnergy = be
  }
}
