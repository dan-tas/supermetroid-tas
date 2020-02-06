package com.github.dan_tas.snes.supermetroid.rng.impl;

import com.github.dan_tas.snes.supermetroid.rng.RngService;

/**
 * Calculates the next RNG by switching the most significant byte
 * with the least signficant byte, like the XBA assembly command.
 */
public class XbaRngServiceImpl implements RngService {
  @Override public int advanceRng(int rng) {
    return (((rng % 0x100) << 8) + (rng >> 8)) & 0xFFFF;
  }
}
