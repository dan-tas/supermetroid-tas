package com.github.dan_tas.snes.supermetroid.rng.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LcgRngServiceImplTest {
  private LcgRngServiceImpl lcgRngService = new LcgRngServiceImpl();

  @Test void simpleRngCalculationNoCarry() {
    int currRng = 0x1111;
    int nextRng = lcgRngService.advanceRng(currRng);

    assertFalse(lcgRngService.hasCarry(currRng));
    assertEquals(0x5555 + 0x0111 + 0, nextRng);
  }

  @Test void simpleRngCalculationWithCarryValueTruncated() {
    int currRng = 0x3333;
    int nextRng = lcgRngService.advanceRng(currRng);

    assertTrue(lcgRngService.hasCarry(currRng));
    assertNotEquals(0x10111, nextRng);
    assertEquals(0x0111, nextRng);
  }

  @Test void specificCarryRng() {
    int currRng = 0x3300;
    int nextRng = lcgRngService.advanceRng(currRng);

    assertTrue(lcgRngService.hasCarry(currRng));
    assertNotEquals(0x10012, nextRng);
    assertEquals(0x0012, nextRng);
  }

  @Test void cannotGetSpecificRngOutput() {
    assertCannotGetRngValueOut((0x0030 + (0x3333 * 0) + 0), 0x0201);
    assertCannotGetRngValueOut((0x0030 + (0x3333 * 0) + 1), 0x0206);

    assertCannotGetRngValueOut((0x0030 + (0x3333 * 1) + 0), 0x0201);
    assertCannotGetRngValueOut((0x0030 + (0x3333 * 1) + 1), 0x0206);

    assertCannotGetRngValueOut((0x0030 + (0x3333 * 2) + 0), 0x0200);
    assertCannotGetRngValueOut((0x0030 + (0x3333 * 2) + 1), 0x0205);

    assertCannotGetRngValueOut((0x0030 + (0x3333 * 3) + 0), 0x01FF);
    assertCannotGetRngValueOut((0x0030 + (0x3333 * 3) + 1), 0x0204);

    assertCannotGetRngValueOut((0x0030 + (0x3333 * 4) + 0), 0x01FE);
    assertCannotGetRngValueOut((0x0030 + (0x3333 * 4) + 1), 0x0203);

    assertCannotGetRngValueOut((0x0030 + (0x3333 * 5) + 0), 0x01FC);
    assertCannotGetRngValueOut((0x0030 + (0x3333 * 5) + 1), 0x0201);
  }
  private void assertCannotGetRngValueOut(int currRng, int expectedRngOutput) {
    int nextRng = lcgRngService.advanceRng(currRng);

    assertNotEquals(0x0202, nextRng);
    assertEquals(expectedRngOutput, nextRng);
  }
}
