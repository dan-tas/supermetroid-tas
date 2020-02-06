package com.github.dan_tas.snes.supermetroid.rng.impl;

import com.github.dan_tas.snes.supermetroid.rng.RngService;

/**
 *  Linear Congruential Generator implementation with carry
 */
public class LcgRngServiceImpl implements RngService {
  @Override public int advanceRng(int rng) {
    int nextRng = rng * 5;
    nextRng += 0x0111;

    if(hasCarry(rng)) {
      nextRng++;
    }

    return nextRng & 0xFFFF;
  }

  // TODO: Actually do the byte math instead of hard-coding
  // the ranges of input generate a carry.
  /**
   * Determine whether the next RNG value will have a carry as part of the LCG with carry.
   * @param rng The RNG value
   * @return true if the RNG value will generate a carry when computing the next RNG value
   */
  public boolean hasCarry(int rng) {
    boolean result = false;

    if (rng == 0x3300) {
      result = true;
    } else {
      if (rng > 0x3000) {
        int quotient = rng / 0x3000;
        int lastNoOverflow = (0x3333 * quotient) - 0x0033;
        int firstNoOverflow = (0x3300 * quotient) + 0x0100;

        result = (lastNoOverflow < rng) && (rng < firstNoOverflow);
      }
    }

    return result;
  }
}
