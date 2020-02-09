package com.github.dan_tas.snes.supermetroid.enemy.itemdrops;

import lombok.Getter;

@Getter
public enum SamusDropModifier {
  HEALTH_BOMB(7), HEALTH_AND_RESERVES_FULL(0), MISSILES_FULL(1), SUPER_MISSILES_FULL(2), POWER_BOMBS_FULL(3);

  private int flag;

  private SamusDropModifier(int bit) {
    this.flag = 1 << bit;
  }
}
