package com.github.dan_tas.snes.supermetroid.rng.impl;

import com.github.dan_tas.snes.supermetroid.rng.RngFrameChangeService;
import com.github.dan_tas.snes.supermetroid.rng.RngService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultRngFrameChangeServiceImpl implements RngFrameChangeService {
  private static final int MAX_RNG_CHANGES_PER_FRAME = 48;

  private static final String LCG = "LCG";
  private static final String XBA = "XBA";
  private static final String NO_CHANGE = "NOP"; // I was going to use "LAG" as in lag frame, but it is too similar to "LCG"
  private static final String SPACE = " ";

  private RngService lcgRngService;
  private RngService xbaRngService;

  public DefaultRngFrameChangeServiceImpl(RngService lcgRngService, RngService xbaRngService) {
    this.lcgRngService = lcgRngService;
    this.xbaRngService = xbaRngService;
  }

  @Override public String describeRngChanges(final int rngFrameStart, final int rngFrameEnd) {
    if (rngFrameStart == rngFrameEnd) {
      return NO_CHANGE;
    }

    String lcgAdvancesWithoutBeginningXba = testOneOrMoreLcgAdvances(rngFrameStart, rngFrameEnd);
    if (lcgAdvancesWithoutBeginningXba != null) {
      return lcgAdvancesWithoutBeginningXba;
    }

    int xbaRng = xbaRngService.advanceRng(rngFrameStart);
    if (rngFrameStart != xbaRng) {
      if (xbaRng == rngFrameEnd) {
        return XBA;
      }

      String lcgAdvancesWithBeginningXba = testOneOrMoreLcgAdvances(xbaRng, rngFrameEnd);

      if (lcgAdvancesWithBeginningXba != null) {
        return XBA + SPACE + lcgAdvancesWithBeginningXba;
      }
    }

    return handleSpecialCases(rngFrameStart, rngFrameEnd);
  }

  private String testOneOrMoreLcgAdvances(final int rngFrameStart, final int rngFrameEnd) {
    int workingRng = rngFrameStart;
    int xbaRng = rngFrameStart;
    int lcgAdvanceCount = 0;

    while ((workingRng != rngFrameEnd) && (xbaRng != rngFrameEnd) && (lcgAdvanceCount < MAX_RNG_CHANGES_PER_FRAME)) {
      workingRng = lcgRngService.advanceRng(workingRng);
      xbaRng = xbaRngService.advanceRng(workingRng);
      lcgAdvanceCount++;
    }

    if (((workingRng == rngFrameEnd) || (xbaRng == rngFrameEnd)) && (lcgAdvanceCount > 0)) {
      String description = "";
      for (int lcgAdvance = 0; lcgAdvance < lcgAdvanceCount; lcgAdvance++) {
        description += SPACE + LCG;
      }

      if ((xbaRng == rngFrameEnd) && (workingRng != rngFrameEnd)) {
        description += SPACE + XBA;
      }

      return description.substring(SPACE.length());
    }

    return null;
  }

  private String handleSpecialCases(final int rngFrameStart, final int rngFrameEnd) {
    switch (rngFrameEnd) {
      case 0x0011: return "Entered a room with polyps";
      case 0x0017: return "Entered a room with beetoms";
      case 0x0025: return "Entered a room with sidehoppers"; // I think the LCG might always happen afterwards, e.g. this case is not necessary. I did not confirm for certain.
      case 0x01CA: return "Entered a room with sidehoppers, then LCG";
      case 0x0061:
        if (rngFrameStart == 0x0000) {
          return "RNG set to initial value: 0061";
        }
      break;
      case 0x0000:
        if (rngFrameStart == 0x5555) {
          return "RNG went from 5555 to 0000, usually happens on frame 000004";
        }
      break;
    }

    /*
     * There is one frame during the cpadolf 100% TAS, the second time leaving Norfair, where the cycle is lost.
     * The frame boundary happens when only one of the two bytes of RNG are updated, but the other is not.
     */
    String errorMessage = String.format("Lost the RNG cycle going from RNG %04X to RNG %04X", rngFrameStart, rngFrameEnd);
    log.info(errorMessage);

    return errorMessage;
  }
}
