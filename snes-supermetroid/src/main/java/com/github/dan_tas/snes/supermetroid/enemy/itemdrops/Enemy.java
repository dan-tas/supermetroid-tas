package com.github.dan_tas.snes.supermetroid.enemy.itemdrops;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Enemy {
  ALCOON                     (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE),
  ATOMIC                     (0x00, 0xA5, 0x50, 0x00, 0x05, 0x05),
  BEETOM                     (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE),
  BOMB_TORIZO                (0x32, 0x32, 0x32, 0x00, 0x32, 0x37),
  BOMB_TORIZO_EGG            (0x74, 0x14, 0x41, 0x36, 0x00, 0x00),
  BOTWOON                    (0x00, 0x82, 0x3C, 0x05, 0x1E, 0x1E),
  BOULDER                    (0x00, 0x00, 0x00, 0xFF, 0x00, 0x00),
  BOYON                      (0x14, 0x0A, 0x55, 0x82, 0x05, 0x05),
  BULL                       (0x00, 0x05, 0x00, 0x00, 0x00, 0xFA),
  CACATAC                    (0x00, 0x01, 0x00, 0x00, 0xFE, 0x00),
  CHOOT                      (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A),
  COVERN                     (0x32, 0x5F, 0x46, 0x00, 0x14, 0x14),
  CROCOMIRE                  (0x00, 0x78, 0x64, 0x05, 0x14, 0x0A),
  CROCOMIRE_FIREBALL         (0x00, 0x0A, 0xDC, 0x05, 0x14, 0x00),
  DESSGEEGA_SMALL            (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE),
  DESSGEEGA_LARGE            (0x50, 0x14, 0x32, 0x5F, 0x05, 0x05),
  DRAGON                     (0x32, 0x32, 0x46, 0x4B, 0x05, 0x05),
  DRAYGON                    (0x32, 0x32, 0x32, 0x00, 0x32, 0x37),
  DRAYGON_GOO                (0x05, 0x1E, 0x6E, 0x64, 0x05, 0x05),
  EVIR                       (0x50, 0x50, 0x50, 0x05, 0x05, 0x05),
  FIREFLEA                   (0x00, 0x01, 0x00, 0x00, 0x00, 0xFE),
  FUNE                       (0x50, 0x50, 0x50, 0x05, 0x05, 0x05),
  GAMET                      (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A),
  GEEGA                      (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A),
  GEEMER_BLUE                (0x37, 0x19, 0xAA, 0x00, 0x05, 0x00),
  GEEMER_GREY                (0x82, 0x14, 0x00, 0x64, 0x00, 0x05),
  GEEMER_ORANGE              (0x82, 0x14, 0x00, 0x64, 0x00, 0x05),
  GERUTA                     (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A),
  GOLD_TORIZO                (0x32, 0x32, 0x32, 0x00, 0x32, 0x37),
  GOLD_TORIZO_EGG            (0x1E, 0x1E, 0x32, 0x3C, 0x55, 0x00),
  GRAY_SPACE_PIRATE_STANDING (0x32, 0x78, 0x50, 0x00, 0x05, 0x00),
  GRAY_SPACE_PIRATE_WALL     (0x0A, 0x2D, 0xC5, 0x01, 0x01, 0x01),
  GREEN_SPACE_PIRATE         (0x32, 0x1E, 0x64, 0x2D, 0x14, 0x0A),
  HOLTZ                      (0x00, 0x78, 0x32, 0x00, 0x32, 0x23),
  KAGO_BUG                   (0x32, 0x5A, 0x46, 0x05, 0x14, 0x14),
  KIHUNTER_GREEN             (0x32, 0x1E, 0x50, 0x4B, 0x0A, 0x0A),
  KIHUNTER_RED               (0x23, 0x78, 0x0A, 0x14, 0x3C, 0x0A),
  KIHUNTER_YELLOW            (0x37, 0x50, 0x0A, 0x28, 0x3C, 0x0A),
  KRAID                      (0x32, 0x32, 0x32, 0x00, 0x32, 0x37),
  KRAID_NAIL                 (0x0A, 0x23, 0xC8, 0x00, 0x0A, 0x00),
  MAGDOLLITE_PROJECTILE      (0x50, 0x50, 0x50, 0x05, 0x05, 0x05),
  MELLA                      (0x46, 0x1E, 0x50, 0x46, 0x05, 0x00),
  MELLOW                     (0x1E, 0x46, 0x50, 0x46, 0x05, 0x00),
  MENU                       (0x46, 0x14, 0x50, 0x50, 0x05, 0x00),
  METROID                    (0x19, 0x32, 0x5A, 0x0A, 0x32, 0x1E),
  MINI_KRAID                 (0x00, 0x01, 0x00, 0x00, 0xFE, 0x00),
  MOCHTROID                  (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A),
  MULTIVIOLA                 (0x46, 0x1E, 0x4B, 0x46, 0x05, 0x05),
  PHANTOON                   (0x32, 0x32, 0x32, 0x00, 0x32, 0x37),
  PHANTOON_FLAME             (0x14, 0x14, 0x64, 0x69, 0x0A, 0x00),
  POWAMP                     (0x01, 0x00, 0x00, 0x00, 0xFE, 0x00),
  PUYO                       (0x3C, 0x3C, 0x3C, 0x05, 0x0A, 0x3C),
  REO                        (0x1E, 0x50, 0x55, 0x28, 0x0A, 0x0A),
  RIDLEY                     (0x32, 0x32, 0x32, 0x00, 0x32, 0x37),
  RIPPER                     (0x50, 0x14, 0x50, 0x41, 0x05, 0x05),
  RIPPER_2                   (0x00, 0x01, 0x00, 0x00, 0xFE, 0x00),
  SCISER                     (0x01, 0x64, 0x00, 0x00, 0x00, 0x9A),
  SHAKTOOL                   (0x50, 0x50, 0x50, 0x05, 0x05, 0x05),
  SIDEHOPPER_LARGE           (0x14, 0x14, 0x37, 0x64, 0x37, 0x05),
  SIDEHOPPER_SMALL           (0x14, 0x28, 0x55, 0x64, 0x05, 0x05),
  SKREE                      (0x14, 0x03, 0x55, 0x89, 0x05, 0x05),
  SKULTERA                   (0x50, 0x1E, 0x46, 0x46, 0x05, 0x00),
  SOVA                       (0x50, 0x46, 0x1E, 0x19, 0x32, 0x00),
  SPORE_SPAWN                (0x32, 0x32, 0x32, 0x00, 0x32, 0x37),
  SPORE_SPAWN_SPORE          (0x0A, 0x14, 0xC8, 0x19, 0x00, 0x00),
  SQUEEPT                    (0x32, 0x32, 0x32, 0x05, 0x32, 0x32),
  VIOLA                      (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE),
  WAVER                      (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A),
  YAPPING_MAW                (0x50, 0x50, 0x50, 0x05, 0x05, 0x05),
  YARD                       (0x55, 0x50, 0x00, 0x50, 0x00, 0x0A),
  ZEB                        (0x3C, 0x3C, 0x3C, 0x05, 0x3C, 0x0A),
  ZEBBO                      (0x00, 0x8C, 0x0A, 0x00, 0x64, 0x05),
  ZEELA                      (0x37, 0x19, 0x7D, 0x00, 0x32, 0x00),
  ZERO                       (0x01, 0x00, 0x00, 0x00, 0x00, 0xFE);

  private int smallEnergyRelative;
  private int largeEnergyRelative;
  private int missilesRelative;
  private int nothingRelative;
  private int superMissilesRelative;
  private int powerBombsRelative;

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
        adjustedNothingRelative, adjustedSuperMissilesRelative, adjustedPowerBombsRelative, this.name());
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
    private String name;

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
