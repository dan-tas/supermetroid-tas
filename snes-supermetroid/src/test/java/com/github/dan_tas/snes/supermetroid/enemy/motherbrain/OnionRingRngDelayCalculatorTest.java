package com.github.dan_tas.snes.supermetroid.enemy.motherbrain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.dan_tas.snes.supermetroid.rng.RngService;
import com.github.dan_tas.snes.supermetroid.rng.impl.LcgRngServiceImpl;

class OnionRingRngDelayCalculatorTest {
  private OnionRingRngDelayCalculator onionRingRngDelayCalculator;

  @BeforeEach void onSetupTest() {
    RngService rngService = new LcgRngServiceImpl();
    onionRingRngDelayCalculator = new OnionRingRngDelayCalculator(rngService);
  }

  @Test void testSniq100MB2Delay() {
/*
  217271 003B 3F48 BB01 0001 C147 FFC5 FF44 0001 0000 0001 0000 0054 0062 007E 1402
  217272 003C 3F48 A816 0000 C147 FFC5 FF44 0004 0000 0001 0000 0053 0064 0079 1402 # The RNG check C15C first runs on this frame with RNG value 497F
  217273 0000 3BC4 497F 0000 C15C FFC5 FF44 0003 0000 0001 0000 0053 0064 0075 1402
  217274 0001 3BC4 708C 0000 C15C FFC5 FF44 0002 0000 0001 0000 0053 0064 0071 1402
  217275 0002 3BC4 33CD 0002 C15C FFC5 FF44 0008 0000 0001 0000 0053 0064 006C 1402
  217276 0003 3BC4 0413 0002 C15C FFC5 FF44 0007 0000 0001 0000 0053 0064 0068 1402
  217277 0004 3BC4 1570 0002 C15C FFC5 FF44 0006 0000 0001 0000 0053 0064 0064 1402
  217278 0005 3BC4 6C41 0002 C15C FFC5 FF44 0005 0000 0001 0000 0053 0065 0060 1402
  217279 0006 3BC4 1E56 0002 C15C FFC5 FF44 0004 0000 0001 0000 0053 0065 005C 1402 # The RNG check C15C first succeeds with RNG 98BF, 7 frames later than it could have
  217280 0007 3BC4 98BF 0002 C182 0040 FF44 0003 0000 0001 0000 0053 0065 0059 1402
 */
    int rngDelay = onionRingRngDelayCalculator.calculateDelay(0xA816);

    assertEquals(7+0+3+0+0+1+0+1+0+0, rngDelay);
  }

/*
  @Test void testFastForwardRng() {
    int rngNext = 0xD86D; // RNG when 0FA8 = B91A the first time
    // Is this always the case? E.g. What if a meatball still exists when rainbow beam attack happens?
    for (int i = 0; i < 4331; i++) {
      rngNext = RngUtil.lcgRng(rngNext);
    }

    assertEquals(0xA816, rngNext);
  }

  @Test void checkOnionRingRngDelay() {
    int initialRng = 0x0C56; // RNG when 0FA8 = B91A the first time
    int rngNext = initialRng;
    for (int i = 0; i < 4331; i++) {
      rngNext = RngUtil.lcgRng(rngNext);
    }

    int rngDelay = onionRingRngDelayCalculator.calculateDelay(rngNext);
    System.out.println(String.format("%04X %02d", initialRng, rngDelay));
  }

  @Test void checkAllRngDelays() {
    for (int initialRng = 0x0000; initialRng < 0x10000; initialRng++) {
      int rngNext = initialRng;
      for (int i = 0; i < 4331; i++) {
        rngNext = RngUtil.lcgRng(rngNext);
      }
      int rngDelay = onionRingRngDelayCalculator.calculateDelay(rngNext);
      System.out.println(String.format("%04X %02d", initialRng, rngDelay));
    }
  }
*/
}
