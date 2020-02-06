package com.github.dan_tas.snes.supermetroid.enemy.motherbrain;

import com.github.dan_tas.tas.framework.converter.ByteArrayConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MB2FightByteArrayConverterImpl implements ByteArrayConverter<MB2FightData> {
  private static final String DATA_FORMAT = "%06d %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X%s";
  private static final String DATA_REGEX = "^[0-9]{6}( [0-9A-F]{4}){15}.*$";

  @Override public byte[] toByteArray(MB2FightData in) {
    String output = String.format(DATA_FORMAT, in.getFrame(),
      in.getMemory0CD0() & 0xFFFF, in.getMemory0FCC() & 0xFFFF, in.getMemory05E5() & 0xFFFF, in.getMemory7804() & 0xFFFF, in.getMemory0FA8() & 0xFFFF,
      in.getMemory0FB2() & 0xFFFF, in.getMemory0FB4() & 0xFFFF, in.getMemory0F94() & 0xFFFF, in.getMemory780E() & 0xFFFF, in.getMemory7830() & 0xFFFF,
      in.getMemory784A() & 0xFFFF, in.getMemory0F7A() & 0xFFFF, in.getMemory0FBE() & 0xFFFF, in.getMemory0AFA() & 0xFFFF, in.getMemory0A1F() & 0xFFFF,
      in.getComments());

    return output.getBytes();
  }

  @Override public MB2FightData fromByteArray(byte[] in) {
    if (in == null) {
      return null;
    }

    String frameString = new String(in);
    if (frameString.matches(DATA_REGEX)) {
      try {
      MB2FightData data = new MB2FightData();
        data.setFrame(Integer.parseInt(frameString.substring(0, 6)));

        data.setMemory0CD0(Integer.parseInt(frameString.substring(07, 11), 0x10));
        data.setMemory0FCC(Integer.parseInt(frameString.substring(12, 16), 0x10));
        data.setMemory05E5(Integer.parseInt(frameString.substring(17, 21), 0x10));
        data.setMemory7804(Integer.parseInt(frameString.substring(22, 26), 0x10));
        data.setMemory0FA8(Integer.parseInt(frameString.substring(27, 31), 0x10));

        data.setMemory0FB2(Integer.parseInt(frameString.substring(32, 36), 0x10));
        data.setMemory0FB4(Integer.parseInt(frameString.substring(37, 41), 0x10));
        data.setMemory0F94(Integer.parseInt(frameString.substring(42, 46), 0x10));
        data.setMemory780E(Integer.parseInt(frameString.substring(47, 51), 0x10));
        data.setMemory7830(Integer.parseInt(frameString.substring(52, 56), 0x10));

        data.setMemory784A(Integer.parseInt(frameString.substring(57, 61), 0x10));
        data.setMemory0F7A(Integer.parseInt(frameString.substring(62, 66), 0x10));
        data.setMemory0FBE(Integer.parseInt(frameString.substring(67, 71), 0x10));
        data.setMemory0AFA(Integer.parseInt(frameString.substring(72, 76), 0x10));
        data.setMemory0A1F(Integer.parseInt(frameString.substring(77, 81), 0x10));

        // Since 0AFA and 0A1F are chosen to split the inputs into outputs, always read these in as 0x0000 to de-duplicate inputs next frame
        data.setMemory0AFA(0x0000);
        data.setMemory0A1F(0x0000);
        data.setComments("");

        return data;
      } catch (NumberFormatException nfe) {
        log.warn("Failed to process input: {}", frameString);
        log.trace("Failed to process input: {}", frameString, nfe);
      }
    }

    return null;
  }
}
