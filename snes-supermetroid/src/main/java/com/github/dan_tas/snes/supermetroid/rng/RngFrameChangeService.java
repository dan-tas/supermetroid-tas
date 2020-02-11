package com.github.dan_tas.snes.supermetroid.rng;

@FunctionalInterface
public interface RngFrameChangeService {
  String describeRngChanges(int rngFrameStart, int rngFrameEnd);
}
