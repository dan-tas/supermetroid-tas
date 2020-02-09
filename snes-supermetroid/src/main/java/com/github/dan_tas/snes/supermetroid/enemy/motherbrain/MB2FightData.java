package com.github.dan_tas.snes.supermetroid.enemy.motherbrain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MB2FightData {
  private int frame;
  private int memory0CD0; // charge counter, can fire charge shot when >= 0x003C. I repurposed the upper byte to be a counter, if greater than 0, then a shot charge shot can hit MB2.

  private int memory0FCC; // MB2 health, starts at 0x4650 and ketchup phase when strictly less than 25%
  private int memory05E5; // RNG counter
  private int memory7804; // MB2 attack indicator: 0 - idle, 1 - stepping forward/backward, 2 - squat/unsquat, 3 - meatball standing/squatting, 4 - ketchup
  private int memory0FA8; // MB2 AI pointer
  private int memory0FB2; // Delay timer for meatball (standing/squatting), french fry parts 1/2
  private int memory0FB4; // Delay timer for 64 frame attacks (onion rings/do nothing)
  private int memory0F94; // Delay timer for ketchup, some other delay not described at beginning though
  private int memory780E; // Counter for frames doing nothing. Goes from 00 -> 01 then adds 06 until >= 0x0100. At this point, step forward because no damage taken in 44 frames
  private int memory7830; // Attack selection index, 0x0000 = choose attack, 0x0001 = wait for 64 frames, 0x0002 = return to main AI. French fry cuts out early with value 0x0001, so next time an attack is picked, it will be do nothing for 64 frames.
  private int memory784A; // Flag, 0001 means a meatball is on screen, otherwise 0000. Prevents a second meatball, or delays ketchup. Intended for lag reduction?
  private int memory0F7A; // Enemy 00 X pixel, used to check boundaries before step forward/backward
  private int memory0FBE; // Enemy 01 Y pixel, used to change attack selection criteria, with Samus Y pixel

  // These three fields below are read in as empty to de-duplicate the input data
  private int memory0AFA; // Samus    Y pixel, can make french fry attack more likely when Samus is up near MB2's head
  private int memory0A1F; // Samus pose (only the lower byte 0A1F && 0x00FF is used)
  private String comments; // Any comments to write out but not read in
}
