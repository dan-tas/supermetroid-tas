package com.github.dan_tas.snes.supermetroid.enemy.itemdrops;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Enemy {
  ALCOON                     (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE, "ALCOON"),
  BEETOM                     (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE, "BEETOM"),
  BULL                       (0x00, 0x05, 0x00, 0x00, 0x00, 0xFA, "BULL"),
  CACATAC                    (0x00, 0x01, 0x00, 0x00, 0xFE, 0x00, "CACATAC"),
  CHOOT                      (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A, "CHOOT"),
  CROCOMIRE                  (0x00, 0x78, 0x64, 0x05, 0x14, 0x0A, "CROC"),
  DESSGEEGA_SMALL            (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE, "DESSG_S"),
  FIREFLEA                   (0x00, 0x01, 0x00, 0x00, 0x00, 0xFE, "FIRFLEA"),
  GAMET                      (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A, "GAMET"),
  GEEGA                      (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A, "GEEGA"),
  GEEMER_BLUE                (0x37, 0x19, 0xAA, 0x00, 0x05, 0x00, "GEEMR_B"),
  GERUTA                     (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A, "GERUTA"),
  GRAY_SPACE_PIRATE_STANDING (0x32, 0x78, 0x50, 0x00, 0x05, 0x00, "PIRT_1S"),
  GRAY_SPACE_PIRATE_WALL     (0x0A, 0x2D, 0xC5, 0x01, 0x01, 0x01, "PIRT_1W"),
  GREEN_SPACE_PIRATE         (0x32, 0x1E, 0x64, 0x2D, 0x14, 0x0A, "PIRATE2"),
  KAGO                       (0x32, 0x5A, 0x46, 0x05, 0x14, 0x14, "KAGO"),
  KRAID                      (0x32, 0x32, 0x32, 0x00, 0x32, 0x37, "KRAID"),
  MELLOW                     (0x1E, 0x46, 0x50, 0x46, 0x05, 0x00, "MELLOW"),
  MINI_KRAID                 (0x00, 0x01, 0x00, 0x00, 0xFE, 0x00, "MNI_KRD"),
  MOCHTROID                  (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A, "MOCHTRD"),
  PHANTOON_FLAME             (0x14, 0x14, 0x64, 0x69, 0x0A, 0x00, "PHN_FLM"),
  PUYO                       (0x3C, 0x3C, 0x3C, 0x05, 0x0A, 0x3C, "PUYO"),
  REO                        (0x1E, 0x50, 0x55, 0x28, 0x0A, 0x0A, "REO"),
  RIDLEY                     (0x32, 0x32, 0x32, 0x00, 0x32, 0x37, "RIDLEY"),
  RIPPER_2                   (0x00, 0x01, 0x00, 0x00, 0xFE, 0x00, "RIPPER2"),
  SIDEHOPPER_LARGE           (0x14, 0x14, 0x37, 0x64, 0x37, 0x05, "SDHOP_L"),
  SIDEHOPPER_SMALL           (0x14, 0x28, 0x55, 0x64, 0x05, 0x05, "SDHOP_S"),
  SKREE                      (0x14, 0x03, 0x55, 0x89, 0x05, 0x05, "SKREE"),
  VIOLA                      (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE, "VIOLA"),
  WAVER                      (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A, "WAVER"),
  YAPPING_MAW                (0x50, 0x50, 0x50, 0x05, 0x05, 0x05, "YAP_MAW"),
  ZEB                        (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A, "ZEB"),
  ZEELA                      (0x37, 0x19, 0x7D, 0x00, 0x32, 0x00, "ZEELA"),
  ZERO                       (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE, "ZERO");

  private int smallEnergyRelative;
  private int largeEnergyRelative;
  private int missilesRelative;
  private int nothingRelative;
  private int superMissilesRelative;
  private int powerBombsRelative;
  private String displayText;

  public AdjustedEnemy withAdjustedDrops(int samusDropModifier) {
    boolean healthBomb = (samusDropModifier & SamusDropModifier.HEALTH_BOMB.getFlag()) != 0;
    boolean healthAndReservesFull = (samusDropModifier & SamusDropModifier.HEALTH_AND_RESERVES_FULL.getFlag()) != 0;
    boolean missilesFull = (samusDropModifier & SamusDropModifier.MISSILES_FULL.getFlag()) != 0;
    boolean supersFull = (samusDropModifier & SamusDropModifier.SUPER_MISSILES_FULL.getFlag()) != 0;
    boolean powerBombsFull = (samusDropModifier & SamusDropModifier.POWER_BOMBS_FULL.getFlag()) != 0;

    int divideTier1 = 0;
    int keepTier1 = 0;
    int divideTier2 = 0;

    if (healthAndReservesFull) {
      divideTier1 += this.smallEnergyRelative + this.largeEnergyRelative;
    } else {
      keepTier1 += this.smallEnergyRelative + this.largeEnergyRelative;
    }

    if (healthBomb || missilesFull) {
      divideTier1 += this.missilesRelative;
    } else {
      keepTier1 += this.missilesRelative;
    }

    if (healthBomb) {
      divideTier1 += this.nothingRelative;
    } else {
      keepTier1 += this.nothingRelative;
    }

    if (healthBomb || supersFull) {
      divideTier2 += superMissilesRelative;
    }

    if (healthBomb || powerBombsFull) {
      divideTier2 += powerBombsRelative;
    }
    int divideTiers = (divideTier1 + divideTier2);

    int adjustedSmallHealthRelative = adjustTier1Drop(healthAndReservesFull, this.getSmallEnergyRelative(), divideTiers,  keepTier1);
    int adjustedLargeHealthRelative = adjustTier1Drop(healthAndReservesFull, this.getLargeEnergyRelative(), divideTiers,  keepTier1);
    int adjustedMissilesRelative = adjustTier1Drop((healthBomb || missilesFull), this.getMissilesRelative(), divideTiers,  keepTier1);
    int adjustedNothingRelative = adjustTier1Drop(healthBomb, this.getNothingRelative(), divideTiers,  keepTier1);

    int adjustedSuperMissilesRelative = adjustTier2Drop((healthBomb || supersFull), getSuperMissilesRelative());
    int adjustedPowerBombsRelative = adjustTier2Drop((healthBomb || powerBombsFull), getPowerBombsRelative());

    return new AdjustedEnemy(adjustedSmallHealthRelative, adjustedLargeHealthRelative, adjustedMissilesRelative,
        adjustedNothingRelative, adjustedSuperMissilesRelative, adjustedPowerBombsRelative, this.displayText);
  }
  private int adjustTier1Drop(boolean adjust, int unadjustedValue, int divideTiers, int keepTier1) {
    if (adjust) {
      return 0;
    } else {
      return (unadjustedValue + (unadjustedValue * divideTiers / keepTier1));
    }
  }
  private int adjustTier2Drop(boolean adjust, int unadjustedValue) {
    if (adjust) {
      return 0;
    } else {
      return unadjustedValue;
    }
  }

  @Getter
  @AllArgsConstructor
  public class AdjustedEnemy {
    private int smallEnergyRelative;
    private int largeEnergyRelative;
    private int missilesRelative;
    private int nothingRelative;
    private int superMissilesRelative;
    private int powerBombsRelative;
    private String displayText;

    /**
     * Calculates the item drop from the enemy adjust by Samus' drop modifiers
     *
     * Pre-condition: If the LSB of the rng value is 0x00, the drops will never scale
     * because 0 * X = 0. In this case, RNG must advance once before calling this method.
     *
     * I do not set RNG to 0x0011 because the carry from RNG can cause RNG to become 0x0012.
     *
     * @param rng - The current RNG for the enemy drop. The LSB should never be 0x00, see pre-condition.
     * @return The item drop corresponding to this combination of adjusted enemy and RNG
     */
    public ItemDrop getItemDrop(int rng) {
      ItemDrop itemDrop = ItemDrop.NOTHING;

      rng &= 0x00FF;
      if (rng == 0x0000) {
        throw new IllegalArgumentException("Cannot use RNG value 0x..00, the drop will not adjust");
      }

      int dropBoundaryIndex = 0;
      List<Integer> dropBoundaries = Arrays.asList(this.getSmallEnergyRelative(), this.getLargeEnergyRelative(),
          this.getMissilesRelative(), this.getNothingRelative(), this.getSuperMissilesRelative(), this.getPowerBombsRelative());

      int cumulative = 0;
      for (ItemDrop itemDropEntry : ItemDrop.class.getEnumConstants()) {
        cumulative += dropBoundaries.get(dropBoundaryIndex);
        if (rng <= cumulative) {
          itemDrop = itemDropEntry;
          break;
        }

        dropBoundaryIndex++;
      }

      return itemDrop;
    }
  }
}
