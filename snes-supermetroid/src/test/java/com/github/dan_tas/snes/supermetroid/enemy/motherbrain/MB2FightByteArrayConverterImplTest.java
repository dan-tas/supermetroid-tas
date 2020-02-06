package com.github.dan_tas.snes.supermetroid.enemy.motherbrain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class MB2FightByteArrayConverterImplTest {
  private MB2FightByteArrayConverterImpl mb2FightByteArrayConverter = new MB2FightByteArrayConverterImpl();

  @Test void testInputNull() {
    MB2FightData output = mb2FightByteArrayConverter.fromByteArray(null);

    assertNull(output);
  }

  @Test void testDecimalMinusOne() {
    // String -1 is a valid integer, but it does not match the regex [0-9]{4}
    String invalidData = "211793 -1 0020 42CC 4237 0000 8F14 00FF 0000 FFD7 0000 0000 0000 003B 004A 0000 0000";

    MB2FightData output = mb2FightByteArrayConverter.fromByteArray(invalidData.getBytes());

    assertNull(output);
  }

  @Test void testInputParsesCorrectly() {
  String inputData = "012345 6789 0123 4567 89AB CDEF 10FE DCBA 9876 5432 2345 6789 ABCD EF10 FEDC BA98 # comment that gets ignored";

  MB2FightData frameData = mb2FightByteArrayConverter.fromByteArray(inputData.getBytes());

  assertNotNull(frameData);
  assertEquals(12345, frameData.getFrame());

  assertEquals(0x6789, frameData.getMemory0CD0());
  assertEquals(0x0123, frameData.getMemory0FCC());
  assertEquals(0x4567, frameData.getMemory05E5());
  assertEquals(0x89AB, frameData.getMemory7804());
  assertEquals(0xCDEF, frameData.getMemory0FA8());

  assertEquals(0x10FE, frameData.getMemory0FB2());
  assertEquals(0xDCBA, frameData.getMemory0FB4());
  assertEquals(0x9876, frameData.getMemory0F94());
  assertEquals(0x5432, frameData.getMemory780E());
  assertEquals(0x2345, frameData.getMemory7830());

  assertEquals(0x6789, frameData.getMemory784A());
  assertEquals(0xABCD, frameData.getMemory0F7A());
  assertEquals(0xEF10, frameData.getMemory0FBE());

  assertEquals(0x0000, frameData.getMemory0AFA());
  assertEquals(0x0000, frameData.getMemory0A1F());
  assertEquals("", frameData.getComments());
  }

  @Test void testDataFromEmulator() {
    String inputData = "211791 011D 4650 B079 0000 8EAA 0000 0000 FFD7 0000 0000 0000 003B 004A 00C3 0600 # another ignored comment";

  MB2FightData frameData = mb2FightByteArrayConverter.fromByteArray(inputData.getBytes());

    assertNotNull(frameData);
    assertEquals(211791, frameData.getFrame());

    assertEquals(0x011D, frameData.getMemory0CD0());
    assertEquals(0x4650, frameData.getMemory0FCC());
    assertEquals(0xB079, frameData.getMemory05E5());
    assertEquals(0x0000, frameData.getMemory7804());
    assertEquals(0x8EAA, frameData.getMemory0FA8());

    assertEquals(0x0000, frameData.getMemory0FB2());
    assertEquals(0x0000, frameData.getMemory0FB4());
    assertEquals(0xFFD7, frameData.getMemory0F94());
    assertEquals(0x0000, frameData.getMemory780E());
    assertEquals(0x0000, frameData.getMemory7830());

    assertEquals(0x0000, frameData.getMemory784A());
    assertEquals(0x003B, frameData.getMemory0F7A());
    assertEquals(0x004A, frameData.getMemory0FBE());

    assertEquals(0x0000, frameData.getMemory0AFA());
    assertEquals(0x0000, frameData.getMemory0A1F());
    assertEquals("", frameData.getComments());
  }

  @Test void testOutputThenInputSame() {
  String inputData = "012345 6789 0123 4567 89AB CDEF 10FE DCBA 9876 5432 2345 6789 ABCD EF10 0000 0000";

  MB2FightData frameData = mb2FightByteArrayConverter.fromByteArray(inputData.getBytes());

  byte[] outputDataBytes = mb2FightByteArrayConverter.toByteArray(frameData);
  String outputData = new String(outputDataBytes);

  assertEquals(inputData, outputData);
  }
}
