package com.github.dan_tas.snes.supermetroid.enemy.motherbrain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.github.dan_tas.snes.supermetroid.rng.RngService;

import com.github.dan_tas.tas.framework.solver.InputProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MB2FightInputProcessorImpl implements InputProcessor<MB2FightData> {
  private static final int CIWP_SHOT = 0x0384;

  private static final String STEP_FORWARDS = " # step forwards";
  private static final String STEP_BACKWARDS = " # step backwards";
  private static final String MB2_DAMAGE = " # hit by CIWP shot";

  private static final String ATTACK_NEXT_FRAME = " # choose an attack next frame";
  private static final String DO_NOTHING_1 = " # do nothing for 1 frame";
  private static final String DO_NOTHING_64 = " # do nothing for 64 frames";
  private static final String ONION_RINGS = " # onion rings";

  private static final String FRENCH_FRY1 = " # french fry part 1/2";
  private static final String FRENCH_FRY2 = " # french fry part 2/2";

  private static final String MEATBALL_STAND = " # meatball (without squat/unsquat)";
  private static final String MEATBALL_SQUAT1 = " # (squat) then meatball then unsquat";
  private static final String MEATBALL_SQUAT2 = " # squat then (meatball) then unsquat";
  private static final String MEATBALL_SQUAT3 = " # squat then meatball then (unsquat)";
  private static final String MEATBALL_ALREADY_ON_SCREEN = " # meatball already on screen";

  private static final String KETCHUP = " # ketchup";
  private static final String RAINBOW_BEAM = " # rainbow beam";

  private RngService rngService;

  public MB2FightInputProcessorImpl(RngService rngService) {
    this.rngService = rngService;
  }

  @Override public Set<MB2FightData> process(MB2FightData first) {
    MB2FightData input = first.toBuilder().build();

    int frame = input.getFrame();
    frame++;
    input.setFrame(frame);

    int newRng = rngService.advanceRng(input.getMemory05E5());
    input.setMemory05E5(newRng);

    int counter = input.getMemory0F94();
    counter--;
    input.setMemory0F94(counter);

    int meatballCounter = input.getMemory784A();
    if (meatballCounter > 0x0000) {
      meatballCounter -= 0x0100;

      if ((meatballCounter & 0xFF00) == 0x0000) {
        input.setMemory784A(0x0000);
      } else {
        input.setMemory784A(meatballCounter);
      }
    }

    Set<MB2FightData> outputs = new TreeSet<>();
    switch(input.getMemory0FA8()) {
      case 0x8EAA:
        // this is a hack to get this working
        // this is only correct for the last frame of 8EAA

      // first frame hit determined by input data
        input.setMemory0FA8(0x8EF5);
        input.setMemory0FB2(0x0017);
        outputs.add(input);
      break;
      case 0x8EF5:
        outputs.addAll(bankA9_8EF5(input));
      break;
      case 0x8F14:
        outputs.addAll(bankA9_8F14(input));
      break;
      case 0x8F33:
        outputs.addAll(bankA9_8F33(input));
      break;
      case 0xB605: // main AI, decide what to do
        outputs.addAll(bankA9_B605(input));
      break;
      case 0xB64B: // three phases, choose attack, wait 64 frames (includes onion rings), return to main AI
        outputs.addAll(bankA9_B64B(input));
      break;
      case 0xB781: // choose between meatball standing or squatting
        outputs.addAll(bankA9_B781(input));
      break;
      case 0xB7C6: // squat before meatball
        outputs.addAll(bankA9_B7C6(input));
      break;
      case 0xB7E8: // meatball attack, standing or squatting
        outputs.addAll(bankA9_B7E8(input));
      break;
      case 0xB7F8: // unsquat after meatball
        outputs.addAll(bankA9_B7F8(input));
      break;
      case 0xB80E: // setup for french fry
        outputs.addAll(bankA9_B80E(input));
      break;
      case 0xB839: // french fry part 1/2
        outputs.addAll(bankA9_B839(input));
      break;
      case 0xB863:  // french fry part 2/2
        outputs.addAll(bankA9_B863(input));
      break;
      case 0xB87D:
        log.info("Ketchup, failure :(");
        input.setComments(KETCHUP);
        outputs.add(input); // filter out when copying outputs to inputs
      break;
      case 0xB8EB:
      case 0xB891:
        log.info("Rainbow beam, success!");
        input.setComments(RAINBOW_BEAM);
        outputs.add(input); // filter out when copying outputs to inputs
      break;
    }

    // manage charge shot counter and the delay before MB2 takes damage
    SortedSet<MB2FightData> updatedOutputs = new TreeSet<>();
    for (MB2FightData output : outputs) {
      int chargeCounter = output.getMemory0CD0() & 0x00FF;
      if (chargeCounter == 0x003C) { // changed from 0x003D since the increment does not happen early
        output.setMemory0CD0(0x2000); // charge shot can hit in any of the next 32 frames
      } else {
        chargeCounter = output.getMemory0CD0();
        chargeCounter++;
        output.setMemory0CD0(chargeCounter);
      }

      int damageTimer = (output.getMemory0CD0() & 0xFF00);
      if (damageTimer >= 0x0100l) {
        damageTimer = output.getMemory0CD0();
        damageTimer -= 0x0100;
        output.setMemory0CD0(damageTimer);

        if((damageTimer & 0xFF00) > 0x0000) {
          // On the last frame a charge shot can hit, 0x0000, always hit, do not give a delay path
          MB2FightData mb2NoDamage = output.toBuilder().build();
        updatedOutputs.add(mb2NoDamage);
        }



        MB2FightData mb2TakesDamage = output.toBuilder().build();
        int mb2Health = mb2TakesDamage.getMemory0FCC();
        mb2Health -= CIWP_SHOT; // currently assumes only charge+ice+wave+plasma
        // If I remember correctly, there is some logic related to damage source
        // that I have hardcoded away under the CIWP assumption
        if (mb2Health < 0x0000) {
          mb2Health = 0x0000;
        }
        mb2TakesDamage.setMemory0FCC(mb2Health);
        mb2TakesDamage.setComments(mb2TakesDamage.getComments() + MB2_DAMAGE);

        damageTimer = mb2TakesDamage.getMemory0CD0();
        damageTimer &= 0x00FF;
        mb2TakesDamage.setMemory0CD0(damageTimer);

        if (mb2TakesDamage.getMemory780E() < 0x0100) {
          mb2TakesDamage.setMemory780E(0x0000);
        }

        updatedOutputs.add(mb2TakesDamage);
      } else {
        updatedOutputs.add(output);
      }
    }

    return updatedOutputs;
  }

  private Set<MB2FightData> bankA9_8EF5(MB2FightData in) {
    int counter = in.getMemory0FB2();
    counter--;

    if (counter < 0x0000) {
      // 8002 = 0x9B7F
      // 8000 = 0x0001
      in.setMemory0FB2(0x0100);
      in.setMemory0FA8(0x8F14);
      // 8068 = 0x0040
      return bankA9_8F14(in);
    } else {
      in.setMemory0FB2(counter);
      return Collections.singleton(in);
    }
  }

  private Set<MB2FightData> bankA9_8F14(MB2FightData in) {
    int counter = in.getMemory0FB2();
    counter--;

    if (counter < 0x0000) {
      in.setMemory0FB2(0x0040);
      in.setMemory0FA8(0x8F33);
      // 8064 = 0x0002,
      // 8066 = 0x0004
      return bankA9_8F33(in);
    } else {
      in.setMemory0FB2(counter);
      return Collections.singleton(in);
    }
  }

  private Set<MB2FightData> bankA9_8F33(MB2FightData in) {
    int counter = in.getMemory0FB2();
    counter--;
    in.setMemory0FB2(counter);

    if (counter < 0x0000) {
      in.setMemory0FA8(0xB605);
      // 7868 = 0x0001
    }

    return Collections.singleton(in);
  }

  private Set<MB2FightData> bankA9_B605(MB2FightData in) {
    long RNG = in.getMemory05E5();
    if ((in.getMemory0FCC() == 0x0000) /* && (in.getMemory7804() == 0x0000)*/) {
      in.setMemory0FA8(0xB8EB); // RAINBOW BEAM, SUCCESS!
      in.setMemory0FA8(0xB91A);
      in.setMemory0FB2(0x0100);
      in.setComments(RAINBOW_BEAM);

      // bankA9_B8EB(in);
    } else if (in.getMemory7804() == 0x0000) {
      if (in.getMemory0FCC() < 0x1194) {
        if ((0x0000l <= RNG) && (RNG < 0x2000l)) {
          return bankA9_C6B8(in);
        } else if ((0x2000l <= RNG) && (RNG < 0xA000l)) {
          in.setMemory0FA8(0xB87D); // KETCHUP, FAILURE
          in.setComments(KETCHUP);
        } else if ((0xA000l <= RNG) && (RNG <= 0xFFFFl)) {
          in.setMemory0FA8(0xB64B); // ATTACK NEXT FRAME
          in.setComments(ATTACK_NEXT_FRAME);
        }
      } else {
        if ((0x0000l <= RNG) && (RNG < 0x1000l)) {
          in.setMemory0FA8(0xB64B); // ATTACK NEXT FRAME
          in.setComments(ATTACK_NEXT_FRAME);
        } else if ((0x1000l <= RNG) && (RNG <= 0xFFFFl)) {
          return bankA9_C6B8(in);
        }
      }
    } else if (in.getMemory7804() == 0x0001) {
     /*
      *  Hack, use the upper byte of this counter to store the value
      *  to set the counter to once the step/forward is finished
      */
      if ((in.getMemory0F94() & 0x00FF) == 0x0000) {
        int newValue = in.getMemory0F94() >> 8;
        in.setMemory0F94(newValue);
        in.setMemory7804(0x0000);
      }
    }

    return Collections.singleton(in);
  }

  private Set<MB2FightData> bankA9_B64B(MB2FightData in) {
    switch (in.getMemory7830()) {
      case 0x0000: return bankA9_B65A(in);
      case 0x0001: return bankA9_B764(in);
      case 0x0002: return bankA9_B773(in);
    }

    // This should never happen, but just in case
    in.setMemory0FA8(0xFA17); // FAIL, filter out when copying outputs to inputs
    return Collections.singleton(in);
  }

  private Set<MB2FightData> bankA9_B65A(MB2FightData in) {
    int attackIndex = in.getMemory7830();
    attackIndex++;
    in.setMemory7830(attackIndex);
    in.setMemory0FB4(0x0040);

    Set<MB2FightData> consolidatedOutputs = new HashSet<>();
    Set<MB2FightData> outputs = bankA9_B6E2(in);
    for (MB2FightData work : outputs) {
      if ((work.getMemory0A1F() & 0xFF00) == 0x0000) {
        int rngLsb = work.getMemory05E5() & 0x00FF;

        MB2FightData one = work.toBuilder().build();
        MB2FightData two = work.toBuilder().build();
        one.setMemory0AFA(0x0050); // near MB2's head
        two.setMemory0AFA(0x00C0); // near ground

        // unset the upper byte so that outputs do not have different values representing the same condition
        int pose;
        pose = one.getMemory0A1F();
        pose &= 0x00FF;
        one.setMemory0A1F(pose);
        pose = two.getMemory0A1F();
        pose &= 0x00FF;
        two.setMemory0A1F(pose);

        consolidatedOutputs.add(one);
        consolidatedOutputs.add(two);

        int[] rngComparisonLimits1 = { 0x10, 0x20, 0xD0 };
        int rngAttackIndex1 = 0;
        for (int rngThreshold : rngComparisonLimits1) {
          if (rngLsb < rngThreshold) {
            break;
          }
          rngAttackIndex1++;
        }

        int[] rngComparisonLimits2 = { 0x40, 0x80, 0xC0 };
        int rngAttackIndex2 = 0;
        for (int rngThreshold : rngComparisonLimits2) {
          if (rngLsb < rngThreshold) {
            break;
          }
          rngAttackIndex2++;
        }


        switch(rngAttackIndex1) {
          case 0x00:
          one.setComments(DO_NOTHING_64);
            // [7E:8002] = 0x9C87;
            // [7E:8000] = 0x0001;
          break;
          case 0x01:
          one.setComments(ONION_RINGS);
            // [7E:8002] = 0x9D3D;
            // [7E:8000] = 0x0001;
          break;
          case 0x02:
          one.setComments(FRENCH_FRY1);
            one.setMemory0FA8(0xB80E);
            bankA9_B80E(one);
          break;
          case 0x03:
            // Meatball
            if (one.getMemory784A() < 0x0001) {
              one.setMemory0FA8(0xB781);
              bankA9_B781(one);
            } else {
              one.setComments(MEATBALL_ALREADY_ON_SCREEN);
            }
          break;
        }

        switch(rngAttackIndex2) {
          case 0x00:
            two.setComments(DO_NOTHING_64);
            // [7E:8002] = 0x9C87;
            // [7E:8000] = 0x0001;
          break;
          case 0x01:
            two.setComments(ONION_RINGS);
            // [7E:8002] = 0x9D3D;
            // [7E:8000] = 0x0001;
          break;
          case 0x02:
            two.setComments(FRENCH_FRY1);
            two.setMemory0FA8(0xB80E);
            bankA9_B80E(two); // need to merge these because of for-loop
          break;
          case 0x03:
            // Meatball
            if (two.getMemory784A() < 0x0001) {
              two.setMemory0FA8(0xB781);
              bankA9_B781(two);
            } else {
              two.setComments(MEATBALL_ALREADY_ON_SCREEN);
              // Do nothing for 64 frames because a meatball is already on screen
            }

          break;
        }
      } else { // already processed
        consolidatedOutputs.add(work);
      }
    }

    return consolidatedOutputs;
  }

  private Set<MB2FightData> bankA9_B6E2(MB2FightData in) {
    MB2FightData one = in.toBuilder().build();
    MB2FightData two = in.toBuilder().build();

    int rngLsb = in.getMemory05E5() & 0x00FF;
    if (rngLsb < 0x0080) {
      // do nothing
      one.setMemory0A1F(0x0001); // not processed

      // french fry
      two.setComments(FRENCH_FRY1);
      two.setMemory0A1F(0xFF02); // processed
      two.setMemory0FA8(0xB80E);
      bankA9_B80E(two);
    } else {
      // meatball
      if (one.getMemory784A() < 0x0001) {
        one.setMemory0A1F(0xFF01); // processed
        one.setMemory0FA8(0xB781);
        bankA9_B781(one);
      } else {
        one.setMemory0A1F(0x0001); // not processed
      }

      two.setComments(ONION_RINGS);
      two.setMemory0A1F(0xFF02); // processed
      // [7E:8002] = 9D3D;
      // [7E:8000] = 0x0001;
    }

    Set<MB2FightData> outputs = new HashSet<>();
    outputs.add(one); // ([7E:0A1F] && 0x00FF) NOT IN (02, 03, 06, 08, 0C, 0D, 12, 13, 14)
    outputs.add(two); // ([7E:0A1F] && 0x00FF)     IN (02, 03, 06, 08, 0C, 0D, 12, 13, 14)

    return outputs;
  }

  private Set<MB2FightData> bankA9_B764(MB2FightData in) {
    int counter = in.getMemory0FB4();
    counter--;
    in.setMemory0FB4(counter);

    if (in.getMemory0FB4() == 0x0000) {
      int index = in.getMemory7830();
      index++;
      in.setMemory7830(index);
    }

    return Collections.singleton(in);
  }

  private Set<MB2FightData> bankA9_B773(MB2FightData in) {
    in.setMemory7830(0x0000);
    in.setMemory0FA8(0xB605);
    return Collections.singleton(in);
  }

  private Set<MB2FightData> bankA9_B781(MB2FightData in) {
    int comparison = in.getMemory05E5();
    long rng = in.getMemory05E5();
    if (rng < 0xFF80l) {
      if (rng >= 0x6000l) {
        comparison = 0x0040;
      } else {
        comparison = 0x0060;
      }
    }

    boolean carry = true;
    if (comparison < in.getMemory0F7A()) {
      carry = bankA9_C647(in, comparison);
    }
    if (carry) {
      return bankA9_B7B7(in);
    }

    return Collections.singleton(in);
  }

  // choose between standing or squatting meatball
  private Set<MB2FightData> bankA9_B7B7(MB2FightData in) {
    int oldRng = in.getMemory05E5();
    int newRng = rngService.advanceRng(oldRng);
    in.setMemory05E5(newRng);

    if ((long)newRng >= 0x8000) {
      in.setComments(MEATBALL_SQUAT1);
      in.setMemory7804(0x0002);
      in.setMemory0FA8(0xB7C6);
      in.setMemory0F94(0x0118); // writes to 0x0001, then dec, then write as 0x0008
      /*
       * Double hack. Use the counter to count down once, rather that 3*8 = 0x18.
       * Also, use the upper byte to keep the value positive after the meatball code is done.
       * Then I can check positive negative to either return to attack selection or unsquat.
       */

      bankA9_B7C6(in);
    } else {
      in.setComments(MEATBALL_STAND);
      in.setMemory7804(0x0003);
      in.setMemory0FA8(0xB7E8);
      in.setMemory0FB2(0x002C);
    }

    return Collections.singleton(in);
  }

  // squat before meatball
  private Set<MB2FightData> bankA9_B7C6(MB2FightData in) {
    // flopped the two if statements so the second statement triggers one frame before the first if statement
    if (bankA9_C68E(in)) {
      in.setComments(MEATBALL_SQUAT2);
      in.setMemory0FA8(0xB7E8);
      in.setMemory0FB2(0x002C);
    }

    if ((in.getMemory0F94() & 0x00FF) == 0x0000) {
      in.setMemory7804(0x0003); // squat to meatball hack
    }


    return Collections.singleton(in);
  }

  // meatball
  private Set<MB2FightData> bankA9_B7E8(MB2FightData in) {
    int counter = in.getMemory0FB2();
    counter--;
    in.setMemory0FB2(counter);

    if (in.getMemory0FB2() == 0x0007) {
      in.setMemory784A(0xB401); // meatball on screen. Use upper byte as a counter to know when to clear meatball
    }

    if (in.getMemory0FB2() < 0x0000) {
      boolean unsquatAfterMeatball = (in.getMemory0F94() > 0x0000);
      if (unsquatAfterMeatball) {
      in.setComments(MEATBALL_SQUAT3);
        in.setMemory7804(0x0002);
        in.setMemory0FA8(0xB7F8);
        in.setMemory0F94(0x0020);
        return bankA9_B7F8(in);
      } else {
        in.setMemory7804(0x0000);
        in.setMemory0FA8(0xB605);
      }
    }

    // removed the remaining code
    return Collections.singleton(in);
  }

  private Set<MB2FightData> bankA9_B7F8(MB2FightData in) {
    //boolean unsquatAfterMeatball = bankA9_C670(in);

    int counter = in.getMemory0FB2();
    counter--;
    in.setMemory0FB2(counter);


    if (in.getMemory0F94() < 0x0000) {
      in.setMemory7804(0x0000);
      in.setMemory0FA8(0xB605);
      in.setMemory0FB2(0xFFFF);
      // removed 0xC15C for when this is not MB2 fight
    }

    return Collections.singleton(in);
  }

  private Set<MB2FightData> bankA9_B80E(MB2FightData in) {
    in.setMemory0FA8(0xB839);
    in.setMemory0FB2(0x0004);

    return Collections.singleton(in);
  }

  // french fry 1/2
  private Set<MB2FightData> bankA9_B839(MB2FightData in) {
    int counter = in.getMemory0FB2();
    counter--;
    in.setMemory0FB2(counter);

    if (counter < 0x0000l) {
      in.setComments(FRENCH_FRY2);
      in.setMemory0FA8(0xB863);
      in.setMemory0FB2(0x0010);
    }

    return Collections.singleton(in);
  }

  // french fry 2/2
  private Set<MB2FightData> bankA9_B863(MB2FightData in) {
    int counter = in.getMemory0FB2();
    counter--;
    in.setMemory0FB2(counter);

    if (counter < 0x0000l) {
      in.setMemory0FA8(0xB605);
    }

    return Collections.singleton(in);
  }


/*
# C42D (MB2 step forward or backward, but called with many other parameters too)
# A = 0x97A4 -> step forwards
# A = 0x98C6 -> step backwards
[7E:0F92] = A;
[7E:0F94] = 0x0001;
[7E:0F90] = 0x0000;
RTS;
 */

  private boolean bankA9_C647(MB2FightData in, int comparison) {
    boolean carry = false;
    if (comparison >= in.getMemory0F7A()) {
      carry = true;
    } else if (in.getMemory7804() != 0x0000) {
      carry = false;
    } else if (in.getMemory0F7A() < 0x0030) {
      carry = true;
    } else {
/*
      int param = comparison;
      switch(comparison) {
        case 0: param = 0xC664; break;
        case 1: param = 0x988C; break;
        case 2: param = 0x98C6; break;
        case 3: param = 0x9900; break;
        case 4: param = 0x9852; break;
        case 5: param = 0x993A; break;
      }
*/
      // C42D
      // [7E:0F92] = param;
      in.setMemory0F94(0x0001);
      // [7E:0F90] = 0x0000;
    }
    return carry;
  }

  private boolean bankA9_C68E(MB2FightData in) {
    return (in.getMemory7804() == 0x0003);
  }

  private Set<MB2FightData> bankA9_C6B8(MB2FightData in) {
    boolean checkRng = false;
    boolean checkStepForwards = false;

    if (in.getMemory7804() == 0x0000) {
      if (in.getMemory780E() == 0x0000) {
        in.setMemory780E(0x0001);
        if (in.getMemory0F7A() >= 0x0030) {
          /*
           *  The code to step/forward backwards is mainly data
           *  Typically, the position updates 7 times, with 4 or 6
           *  frames in between each update, depending on which
           *  direction MB is stepping. Backwards is 4 frames.
           *
           *  I am hacking in some stuff here to count down from
           *  32 or 48 frames, and only set the position once.
           *  Also, I need to manage the MB2 state as well.
           */
          int xPosition = in.getMemory0F7A();
          xPosition -= 0x0018;
          in.setMemory0F7A(xPosition);
          in.setMemory0F94(0x0420);
          in.setMemory7804(0x0001);
          in.setMemory780E(0x0000);
          in.setComments(STEP_BACKWARDS);

          return Collections.singleton(in);
        }
        checkRng = true; // here
      } else {
        int stepForwardCounter = in.getMemory780E();
        stepForwardCounter += 0x0006;
        in.setMemory780E(stepForwardCounter);

        if (in.getMemory780E() >= 0x0100) {
          checkStepForwards = true;
        } else if (in.getMemory0F7A() < 0x0630) {
          checkRng = true;
        }
      }
    }

    if (checkRng) {
      long RNG = in.getMemory05E5() & 0x0FFF;
      if ((0x0000l <= RNG) && (RNG < 0x0FC0l)) {
      in.setComments(DO_NOTHING_1);
        return Collections.singleton(in);
      }
      checkStepForwards = true;
    }

    if (checkStepForwards) {
      in.setMemory780E(0x0080);

      if (in.getMemory0F7A() < 0x0080) {
        /*
         *  The code to step/forward backwards is mainly data
         *  Typically, the position updates 7 times, with 4 or 6
         *  frames in between each update, depending on which
         *  direction MB is stepping. Forwards is 6 frames.
         *
         *  I am hacking in some stuff here to count down from
         *  32 or 48 frames, and only set the position once.
         *  Also, I need to manage the MB2 state as well.
         */
        int xPosition = in.getMemory0F7A();
        xPosition += 0x0018;
        in.setMemory0F7A(xPosition);
        in.setMemory0F94(0x0630);
        in.setMemory7804(0x0001);
        in.setComments(STEP_FORWARDS);

        return Collections.singleton(in);
      }
    }

    in.setComments(DO_NOTHING_1);
    return Collections.singleton(in);
  }
}
