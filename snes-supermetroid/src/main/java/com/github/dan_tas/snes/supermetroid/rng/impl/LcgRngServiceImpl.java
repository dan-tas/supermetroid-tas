package com.github.dan_tas.snes.supermetroid.rng.impl;

import com.github.dan_tas.snes.supermetroid.rng.RngService;

/**
 *  Linear Congruential Generator implementation with carry
 */
public class LcgRngServiceImpl implements RngService {
  private static final int MULTIPLIER = 0x0005;
  private static final int ADDEND = 0x0111;

  private static final int LSB_MASK = 0x00FF;
  private static final int MSB_MASK = 0xFF00;
  private static final int RNG_MASK = 0xFFFF;

  @Override public int advanceRng(int rng) {
    int nextRng = rng;
    nextRng *= MULTIPLIER;
    nextRng += ADDEND;

    if(hasCarry(rng)) {
      nextRng++;
    }

    return nextRng & RNG_MASK;
  }

  /**
   * Determine whether the next RNG value will have a carry as part of the LCG with carry.
   * @param rng The RNG value
   * @return true if the RNG value will generate a carry when computing the next RNG value
   */
  public boolean hasCarry(int rng) {
    int addend1 = (((rng & MSB_MASK) * MULTIPLIER) & MSB_MASK);
    int addend2 = (((rng & LSB_MASK) * MULTIPLIER) & MSB_MASK);
    int addend3 = ADDEND & MSB_MASK;
    int sum = (addend1 >> 8) + (addend2 >> 8) + (addend3 >> 8);

    return ((sum & MSB_MASK) != 0x0000);
  }
}
