package com.github.dan_tas.snes.supermetroid.enemy.motherbrain;

import com.github.dan_tas.snes.supermetroid.rng.RngService;

import lombok.extern.slf4j.Slf4j;

/**
 * This calculates how many frames of delay happen based on the RNG during the cut scene
 * between the MB2 and MB3 fights where the baby gets hit with 10 onion ring attacks
 */
@Slf4j
public class OnionRingRngDelayCalculator {
  private RngService rngService;

  public OnionRingRngDelayCalculator(RngService rngService) {
  this.rngService = rngService;
  }
/**
 * Calculates the number of frames of delay from RNG during the baby metroid cut scene
 *
 * @param initialRng the value of 7E:05E5 the last time 7E:0FA8 is C147 (onion rings attack starting)
 * @return The number of frames lost to RNG because of an artificial delay inserted by RNG
 */
  public int calculateDelay(int initialRng) {
    int rngDelay = 0;

    int nextRng = initialRng;
    for (int loop = 0; loop < 10; loop++) {
      do {
        nextRng = rngService.advanceRng(nextRng);
        if (nextRng < 0x8000) {
          rngDelay++;
          log.debug(String.format("RNG Delay! RNG: %04X, loop: %1d, total: %03d", nextRng, loop, rngDelay));
        }
      } while (nextRng < 0x8000);

      for (int betweenLoops = 0; betweenLoops < 65; betweenLoops++) {
        nextRng = rngService.advanceRng(nextRng);
        log.debug(String.format("  Advance: %04X" , nextRng));
      }
    }

    return rngDelay;
  }
}
