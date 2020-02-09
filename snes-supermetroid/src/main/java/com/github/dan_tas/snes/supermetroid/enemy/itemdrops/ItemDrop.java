package com.github.dan_tas.snes.supermetroid.enemy.itemdrops;

import lombok.Getter;

// 1st RNG value determines the relative position of item drop
// 2nd RNG value determines the type of item drop
public enum ItemDrop {
  SMALL_ENERGY, LARGE_ENERGY, MISSILE, NOTHING, SUPER_MISSILE, POWER_BOMB;

  public RelativePositionItemDrop withRelativePosition(int rng) {
    return new RelativePositionItemDrop(this, rng);
  }

  @Getter
  private abstract class PositionItemDrop {
    private ItemDrop itemDrop;
    private int x;
    private int y;

    PositionItemDrop(ItemDrop itemDrop, int rng) {
      this.itemDrop = itemDrop;
      this.x = (rng & 0x00FF);
      this.y = ((rng & 0xFF00) >> 8);
    }

    PositionItemDrop(ItemDrop itemDrop, int x, int y) {
      this.itemDrop = itemDrop;
      this.x = x;
      this.y = y;
    }
  }

  public class RelativePositionItemDrop extends PositionItemDrop {
    public RelativePositionItemDrop(ItemDrop itemDrop, int rng) {
      super(itemDrop, rng);
    }

    public RelativePositionItemDrop(ItemDrop itemDrop, int relativeX, int relativeY) {
      super(itemDrop, relativeX, relativeY);
    }

    public AbsolutePositionItemDrop withAbsolutePosition(int xMask, int xShift, int yMask, int yShift) {
      int absoluteX = (this.getX() & xMask) + xShift;
      int absoluteY = (this.getY() & yMask) + yShift;

      return new AbsolutePositionItemDrop(this.getItemDrop(), absoluteX, absoluteY);
    }
  }

  public class AbsolutePositionItemDrop extends PositionItemDrop {
    public AbsolutePositionItemDrop(ItemDrop itemDrop, int absoluteX, int absoluteY) {
      super(itemDrop, absoluteX, absoluteY);
    }
  }
}
