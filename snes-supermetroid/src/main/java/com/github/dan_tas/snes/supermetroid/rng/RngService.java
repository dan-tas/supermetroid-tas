package com.github.dan_tas.snes.supermetroid.rng;

@FunctionalInterface
/**
 * Calculate the next RNG value from the current RNG value
 */
public interface RngService {
  /**
   * Calculates the next RNG value based on the current RNG value
   * @param rng The current RNG to advance to the next RNG value
   * @return The next RNG value calculated from the given RNG
   */
  int advanceRng(int rng);
}
