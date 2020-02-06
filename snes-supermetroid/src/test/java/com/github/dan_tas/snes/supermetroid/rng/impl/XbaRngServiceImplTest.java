package com.github.dan_tas.snes.supermetroid.rng.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class XbaRngServiceImplTest {
  private XbaRngServiceImpl xbaRngService = new XbaRngServiceImpl();

  @Test void xbaDifferentOutputThanInput() {
  int rng = xbaRngService.advanceRng(0x0201);

  assertEquals(0x0102, rng);
  }

  @Test void xbaSameOutputAsInput() {
  int rng = xbaRngService.advanceRng(0x0202);

  assertEquals(0x0202, rng);
  }
}
