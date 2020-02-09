package com.github.dan_tas.snes.supermetroid.enemy.itemdrops;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.dan_tas.snes.supermetroid.enemy.itemdrops.Enemy.AdjustedEnemy;
import com.github.dan_tas.snes.supermetroid.enemy.itemdrops.ItemDrop.AbsolutePositionItemDrop;
import com.github.dan_tas.snes.supermetroid.enemy.itemdrops.ItemDrop.RelativePositionItemDrop;
import com.github.dan_tas.snes.supermetroid.rng.RngService;
import com.github.dan_tas.snes.supermetroid.rng.impl.LcgRngServiceImpl;
import com.github.dan_tas.snes.supermetroid.rng.impl.XbaRngServiceImpl;

class EnemyItemDropsTest {
  private RngService lcgRngService = new LcgRngServiceImpl();
  private RngService xbaRngService = new XbaRngServiceImpl();

  @Test void testUnadjustedDrop() {
    int rng = 0x3342;
    Enemy enemy = Enemy.WAVER;

    ItemDrop itemDrop = enemy.withAdjustedDrops(0).getItemDrop(rng);

    assertEquals(ItemDrop.LARGE_ENERGY, itemDrop);
  }

  @Test void testAdjustedDrop() {
    int rng = 0x15FE;
    Enemy enemy = Enemy.BEETOM;

    ItemDrop itemRegular = enemy.withAdjustedDrops(0).getItemDrop(rng);
    ItemDrop itemAdjusted = enemy.withAdjustedDrops(SamusDropModifier.POWER_BOMBS_FULL.getFlag()).getItemDrop(rng);

    assertEquals(ItemDrop.POWER_BOMB, itemRegular);
    assertEquals(ItemDrop.SMALL_ENERGY, itemAdjusted);
  }

  @Test void healthBomb() {
    Enemy enemy = Enemy.GREEN_SPACE_PIRATE;
    AdjustedEnemy adjustedEnemy = enemy.withAdjustedDrops(SamusDropModifier.HEALTH_BOMB.getFlag());

    assertTrue(enemy.getLargeEnergyRelative() < enemy.getNothingRelative());
    assertTrue(enemy.getNothingRelative() < enemy.getSmallEnergyRelative());

    assertEquals(0, adjustedEnemy.getNothingRelative());
    assertEquals(0xFE, adjustedEnemy.getSmallEnergyRelative() + adjustedEnemy.getLargeEnergyRelative());

    assertTrue(adjustedEnemy.getNothingRelative() < adjustedEnemy.getLargeEnergyRelative());
    assertTrue(adjustedEnemy.getLargeEnergyRelative() < adjustedEnemy.getSmallEnergyRelative());

    int smallHealthDrops = 0;
    int largeHealthDrops = 0;
    int missileDrops = 0;
    int nothingDrops = 0;
    int superMissileDrops = 0;
    int powerBombDrops = 0;

    for (int rng = 0x0000; rng <= 0xFFFF; rng++) {
      if ((rng & 0x00FF) != 0x0000) {
        ItemDrop itemDrop = adjustedEnemy.getItemDrop(rng);

        switch(itemDrop) {
          case SMALL_ENERGY: smallHealthDrops++;
          break;
          case LARGE_ENERGY: largeHealthDrops++;
          break;
          case MISSILE: missileDrops++;
          break;
          case NOTHING: nothingDrops++;
          break;
          case SUPER_MISSILE: superMissileDrops++;
          break;
          case POWER_BOMB: powerBombDrops++;
          break;
        }
      }
    }

    assertNotEquals(0, smallHealthDrops);
    assertNotEquals(0, largeHealthDrops);
    assertEquals(0, missileDrops);
    assertNotEquals(0, nothingDrops);
    assertEquals(0, superMissileDrops);
    assertEquals(0, powerBombDrops);

    assertEquals(0x100, nothingDrops);
    assertTrue(nothingDrops < largeHealthDrops);
    assertTrue(largeHealthDrops < smallHealthDrops);
  }

  @Test void ridleyDropsFullMissiles() {
    Enemy enemy = Enemy.RIDLEY;
    AdjustedEnemy adjustedEnemy = enemy.withAdjustedDrops(SamusDropModifier.MISSILES_FULL.getFlag());

    List<String> expectedItemDropDetails = Arrays.asList(
      "(006C,016D) LARGE_ENERGY",
      "(0072,017C) SMALL_ENERGY",
      "(0088,0167) LARGE_ENERGY",
      "(00AE,015C) SMALL_ENERGY",
      "(0064,014D) SUPER_MISSILE",
      "(00AA,014E) SUPER_MISSILE",
      "(0080,017B) LARGE_ENERGY",
      "(0066,014F) LARGE_ENERGY",
      "(005C,014D) SMALL_ENERGY",
      "(0062,015A) SMALL_ENERGY",
      "(0078,0160) SMALL_ENERGY",
      "(009E,016B) LARGE_ENERGY",
      "(0054,014F) LARGE_ENERGY",
      "(009A,017F) POWER_BOMB",
      "(0070,0176) SMALL_ENERGY",
      "(0056,0151) LARGE_ENERGY"
    );

    // On new frame, XBA then LCG
    int startingRng = 0x3632;
    int rng = xbaRngService.advanceRng(startingRng);
    rng = lcgRngService.advanceRng(rng);

    // Drops
    for (int dropIndex = 0; dropIndex < 0x10; dropIndex++) {
      rng = lcgRngService.advanceRng(rng);
      int dropPositionRng = rng;

      rng = lcgRngService.advanceRng(rng);
      if ((rng & 0x00FF) == 0x0000) {
        rng = lcgRngService.advanceRng(rng);
      }

      int dropItemRng = rng;

      ItemDrop itemDrop = adjustedEnemy.getItemDrop(dropItemRng);
      RelativePositionItemDrop relativePositionItemDrop = itemDrop.withRelativePosition(dropPositionRng);
      AbsolutePositionItemDrop absolutePositionItemDrop = relativePositionItemDrop.withAbsolutePosition(0x007F, 0x0040, 0x003F, 0x0140);

      String itemDropDetails = String.format("(%04X,%04X) %s", absolutePositionItemDrop.getX(), absolutePositionItemDrop.getY(), absolutePositionItemDrop.getItemDrop().name());
      assertEquals(expectedItemDropDetails.get(dropIndex), itemDropDetails);
    }

    assertEquals(0xD67F, rng);
  }
}
