package com.github.dan_tas.snes.supermetroid.enemy.motherbrain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.dan_tas.snes.supermetroid.rng.RngService;
import com.github.dan_tas.snes.supermetroid.rng.impl.LcgRngServiceImpl;

class MB2FightInputProcessorImplTest {
  private MB2FightInputProcessorImpl mb2InputProcessor;
  private MB2FightByteArrayConverterImpl mb2SolverByteArrayConverter;
  private MB2FightData testData;

  @BeforeEach void onSetupTest() {
  RngService rngService = new LcgRngServiceImpl();
    mb2InputProcessor = new MB2FightInputProcessorImpl(rngService);
    mb2SolverByteArrayConverter = new MB2FightByteArrayConverterImpl();
  }

  @Test void debug0000_doNotSplitOutputsOnLastFrameOfDamage() {
    testData = mb2SolverByteArrayConverter.fromByteArray("211791 011D 4650 B079 0000 8EAA 0000 0000 FFD7 0000 0000 0000 003B 004A 00C3 0600".getBytes());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(1, outputs.size());
    Iterator<MB2FightData> outputIterators = outputs.iterator();

    MB2FightData output1 = outputIterators.next();
    assertEquals(0x001E, output1.getMemory0CD0());
    assertEquals(0x42CC, output1.getMemory0FCC());
  }

  @Test void debug0001_doNotSplitOutputsOnLastFrameOfDamage() {
    testData = mb2SolverByteArrayConverter.fromByteArray("211792 0000 42CC 736E 0000 8EF5 0017 0000 FFD6 0000 0000 0000 003B 004A 0000 0000".getBytes());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(1, outputs.size());
  }

  @Test void debug0002_splitOutputsOnDamage() {
    testData = mb2SolverByteArrayConverter.fromByteArray("211852 003C 42CC D4F2 0000 8F14 00DB 0000 FF9A 0000 0000 0000 003B 004A 0000 0000".getBytes());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterators = outputs.iterator();

    MB2FightData output1 = outputIterators.next();
    assertEquals(0x1F00, output1.getMemory0CD0());
    assertEquals(0x42CC, output1.getMemory0FCC());

    MB2FightData output2 = outputIterators.next();
    assertEquals(0x0000, output2.getMemory0CD0());
    assertEquals(0x3F48, output2.getMemory0FCC());
  }

  @Test void debug0003_doNotAddSplitExtras() {
    testData = mb2SolverByteArrayConverter.fromByteArray("211853 0000 3F48 29CB 0000 8F14 00DA 0000 FF99 0000 0000 0000 003B 004A 0000 0000".getBytes());

    Set<MB2FightData> outputs1 = mb2InputProcessor.process(testData);

    assertEquals(1, outputs1.size());

    testData = mb2SolverByteArrayConverter.fromByteArray("211853 1F00 42CC 29CB 0000 8F14 00DA 0000 FF99 0000 0000 0000 003B 004A 0000 0000".getBytes());
    Set<MB2FightData> outputs2 = mb2InputProcessor.process(testData);
    assertEquals(2, outputs2.size());
  }

  @Test void debug0004_healthChangesEarly() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212128 1800 3138 BAF7 0000 8F33 0007 0000 FE86 0000 0000 0000 003B 004A 0000 0000".getBytes());
    assertEquals(0x8F33, testData.getMemory0FA8());
    assertEquals(0x3138, testData.getMemory0FCC());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterators = outputs.iterator();

    MB2FightData output1 = outputIterators.next();
    assertEquals(0x8F33, output1.getMemory0FA8());
    assertEquals(0x3138, output1.getMemory0FCC());

    MB2FightData output2 = outputIterators.next();
    assertEquals(0x8F33, output2.getMemory0FA8());
    assertEquals(0x2DB4, output2.getMemory0FCC());

    Set<MB2FightData> nextFrame = mb2InputProcessor.process(output2);
    Iterator<MB2FightData> outputIterators2 = nextFrame.iterator();
    while(outputIterators2.hasNext()) {
      MB2FightData output2ndFrame = outputIterators2.next();
      assertEquals(0x2DB4, output2ndFrame.getMemory0FCC());
    }
  }

  @Test void debug0005_firstSwitchToMainAI() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212135 1207 3138 8066 0000 8F33 0000 0000 FE7F 0000 0000 0000 003B 004A 0000 0000".getBytes());
    assertEquals(0x8F33, testData.getMemory0FA8());
    assertEquals(0x0000, testData.getMemory0FB2());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterator = outputs.iterator();
    while (outputIterator.hasNext()) {
      MB2FightData output = outputIterator.next();
      assertEquals(0xB605, output.getMemory0FA8());
      assertEquals(0xFFFF, (output.getMemory0FB2() & 0xFFFF));
    }
  }

  @Test void debug0006_stepBackwards() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212136 080F 3138 830F 0000 B605 0000 0000 FFD7 0000 0000 0000 003B 004A 0000 0000".getBytes());
    assertEquals(0x0000, testData.getMemory7804());
    assertEquals(0xB605, testData.getMemory0FA8());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterator = outputs.iterator();
    while (outputIterator.hasNext()) {
      MB2FightData output = outputIterator.next();
      assertEquals(0x0001, output.getMemory7804());
      assertEquals(0xB605, output.getMemory0FA8());
    }
  }

  @Test void debug0007_variable0F94SetTo4AfterSteppingBackward() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212168 1128 2DB4 E56F 0001 B605 FFFF 0000 0401 0000 0000 0000 0024 0053 00C3 0200".getBytes());
    assertEquals(0xB605, testData.getMemory0FA8());
    assertEquals(0x0001, testData.getMemory7804());
    assertEquals(0x0401, testData.getMemory0F94());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);
    assertEquals(2, outputs.size());

    Iterator<MB2FightData> outputIterator = outputs.iterator();
    while (outputIterator.hasNext()) {
      MB2FightData output = outputIterator.next();

      assertEquals(0xB605, output.getMemory0FA8());
      assertEquals(0x0000, output.getMemory7804());
      assertEquals(0x0004, output.getMemory0F94());
    }
  }

  @Test void debug0008_onionRings() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212193 1704 2DB4 07A4 0000 B64B FFFF 0000 FFEC 0085 0000 0000 0023 004A 0000 0000".getBytes());
    assertEquals(0xB64B, testData.getMemory0FA8());
    assertEquals(0x0000, testData.getMemory0FB4());
    assertEquals(0x0000, testData.getMemory7830());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);
    assertEquals(6, outputs.size());
    Iterator<MB2FightData> outputIterators = outputs.iterator();

    MB2FightData output1 = outputIterators.next();
    assertEquals(0xB839, output1.getMemory0FA8());
    assertEquals(0x0040, output1.getMemory0FB4());
    assertEquals(0x0001, output1.getMemory7830());

    MB2FightData output2 = outputIterators.next();
    assertEquals(0xB839, output2.getMemory0FA8());
    assertEquals(0x0040, output2.getMemory0FB4());
    assertEquals(0x0001, output2.getMemory7830());

    MB2FightData output3 = outputIterators.next();
    assertEquals(0xB839, output3.getMemory0FA8());
    assertEquals(0x0040, output3.getMemory0FB4());
    assertEquals(0x0001, output3.getMemory7830());

    MB2FightData output4 = outputIterators.next();
    assertEquals(0xB64B, output4.getMemory0FA8());
    assertEquals(0x0040, output4.getMemory0FB4());
    assertEquals(0x0001, output4.getMemory7830());

    MB2FightData output5 = outputIterators.next();
    assertEquals(0xB64B, output5.getMemory0FA8());
    assertEquals(0x0040, output5.getMemory0FB4());
    assertEquals(0x0001, output5.getMemory7830());

    MB2FightData output6 = outputIterators.next();
    assertEquals(0xB839, output6.getMemory0FA8());
    assertEquals(0x0040, output6.getMemory0FB4());
    assertEquals(0x0001, output6.getMemory7830());
  }

  @Test void debug0009_test64FrameCountdown() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212194 1605 2DB4 2745 0000 B64B FFFF 0040 FFEB 0085 0001 0000 0023 004A 00C0 0001".getBytes());
    assertEquals(0xB64B, testData.getMemory0FA8());
    assertEquals(0x0040, testData.getMemory0FB4());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);
    assertEquals(2, outputs.size());

    Iterator<MB2FightData> outputIterator = outputs.iterator();
    while (outputIterator.hasNext()) {
      MB2FightData output = outputIterator.next();

      assertEquals(0xB64B, output.getMemory0FA8());
      assertEquals(0x003F, output.getMemory0FB4());
    }
  }

  @Test void debug000A_testStepForwardCounterClearedWhenShot() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212212 0117 2DB4 6E53 0000 B64B FFFF 002E FFD9 0085 0001 0000 0023 004F 00C3 0200".getBytes());
    assertEquals(0xB64B, testData.getMemory0FA8());
    assertEquals(0x2DB4, testData.getMemory0FCC());
    assertEquals(0x0085, testData.getMemory780E());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(1, outputs.size());
    Iterator<MB2FightData> outputIterator = outputs.iterator();

    MB2FightData output1 = outputIterator.next();
    assertEquals(0xB64B, output1.getMemory0FA8());
    assertEquals(0x2A30, output1.getMemory0FCC());
    assertEquals(0x0000, output1.getMemory780E());
  }

  @Test void debug000B_stepForwardCounterClearedWhenShot() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212260 010A 2A30 DCE3 0000 B605 FFFF 0000 FFA9 0001 0000 0000 0023 004F 0051 1402".getBytes());
    assertEquals(0xB605, testData.getMemory0FA8());
    assertEquals(0x2A30, testData.getMemory0FCC());
    assertEquals(0x0001, testData.getMemory780E());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(1, outputs.size());
    Iterator<MB2FightData> outputIterator = outputs.iterator();

    MB2FightData output1 = outputIterator.next();
    assertEquals(0xB605, output1.getMemory0FA8());
    assertEquals(0x26AC, output1.getMemory0FCC());
    assertEquals(0x0000, output1.getMemory780E());
  }

  @Test void debug000C_stepForwardCounterSetTo80WhenSteppingForward() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212264 190E 26AC EC8F 0000 B605 FFFF 0000 FFA5 000D 0000 0000 0023 004F 0054 1402".getBytes());
    assertEquals(0xB605, testData.getMemory0FA8());
    assertEquals(0x0000, testData.getMemory7804());
    assertEquals(0x000D, testData.getMemory780E());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterator = outputs.iterator();

    MB2FightData output1 = outputIterator.next();
    assertEquals(0xB605, output1.getMemory0FA8());
    assertEquals(0x0001, output1.getMemory7804());
    assertEquals(0x0000, output1.getMemory780E());

    MB2FightData output2 = outputIterator.next();
    assertEquals(0xB605, output2.getMemory0FA8());
    assertEquals(0x0001, output2.getMemory7804());
    assertEquals(0x0080, output2.getMemory780E());
  }

  @Test void debug000D_variable0F94SetTo6AfterSteppingForward() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212312 1501 26AC 65C8 0001 B605 FFFF 0000 0601 0080 0000 0000 003C 0043 00C3 0600".getBytes());
    assertEquals(0xB605, testData.getMemory0FA8());
    assertEquals(0x0001, testData.getMemory7804());
    assertEquals(0x0601, testData.getMemory0F94());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterator = outputs.iterator();

    MB2FightData output1 = outputIterator.next();
    assertEquals(0xB605, output1.getMemory0FA8());
    assertEquals(0x0000, output1.getMemory7804());
    assertEquals(0x0006, output1.getMemory0F94());

    MB2FightData output2 = outputIterator.next();
    assertEquals(0xB605, output2.getMemory0FA8());
    assertEquals(0x0000, output2.getMemory7804());
    assertEquals(0x0006, output2.getMemory0F94());
  }

  @Test void debug000E_secondOnionRings() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212428 0038 1FA4 0F2D 0000 B64B FFFF 0000 FFFE 0019 0000 0000 0023 004A 0000 0000".getBytes());
    assertEquals(0xB64B, testData.getMemory0FA8());
    assertEquals(0xFFFE, testData.getMemory0F94());
    assertEquals(0x0019, testData.getMemory780E());
    assertEquals(0x0000, testData.getMemory7830());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterators = outputs.iterator();

    MB2FightData output1 = outputIterators.next();
    assertEquals(0xB7C6, output1.getMemory0FA8());
    assertEquals(0x0118, output1.getMemory0F94());
    assertEquals(0x0019, output1.getMemory780E());
    assertEquals(0x0001, output1.getMemory7830());

    MB2FightData output2 = outputIterators.next();
    assertEquals(0xB64B, output2.getMemory0FA8());
    assertEquals(0xFFFD, output2.getMemory0F94());
    assertEquals(0x0019, output2.getMemory780E());
    assertEquals(0x0001, output2.getMemory7830());
  }

  @Test void debug000F_meatballSquat() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212678 0901 1194 04F0 0000 B64B FFFF 0000 FF04 0007 0000 0000 0023 0042 00C3 0600".getBytes());
    assertEquals(0x0000, testData.getMemory7804());
    assertEquals(0xB64B, testData.getMemory0FA8());
    assertEquals(0x0000, testData.getMemory0FB4());
    assertEquals(0xFF04, testData.getMemory0F94());
    assertEquals(0x0007, testData.getMemory780E());
    assertEquals(0x0000, testData.getMemory7830());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(4, outputs.size());
    Iterator<MB2FightData> outputIterators = outputs.iterator();

    MB2FightData output1 = outputIterators.next();
    assertEquals(0x0002, output1.getMemory7804());
    assertEquals(0xB7C6, output1.getMemory0FA8());
    assertEquals(0x0040, output1.getMemory0FB4());
    assertEquals(0x0118, output1.getMemory0F94());
    assertEquals(0x0007, output1.getMemory780E());
    assertEquals(0x0001, output1.getMemory7830());

    MB2FightData output2 = outputIterators.next();
    assertEquals(0x0000, output2.getMemory7804());
    assertEquals(0xB64B, output2.getMemory0FA8());
    assertEquals(0x0040, output2.getMemory0FB4());
    assertEquals(0xFF03, output2.getMemory0F94());
    assertEquals(0x0000, output2.getMemory780E());
    assertEquals(0x0001, output2.getMemory7830());

    MB2FightData output3 = outputIterators.next();
    assertEquals(0x0000, output3.getMemory7804());
    assertEquals(0xB64B, output3.getMemory0FA8());
    assertEquals(0x0040, output3.getMemory0FB4());
    assertEquals(0xFF03, output3.getMemory0F94());
    assertEquals(0x0007, output3.getMemory780E());
    assertEquals(0x0001, output3.getMemory7830());

    MB2FightData output4 = outputIterators.next();
    assertEquals(0x0002, output4.getMemory7804());
    assertEquals(0xB7C6, output4.getMemory0FA8());
    assertEquals(0x0040, output4.getMemory0FB4());
    assertEquals(0x0118, output4.getMemory0F94());
    assertEquals(0x0000, output4.getMemory780E());
    assertEquals(0x0001, output4.getMemory7830());
  }

  @Test void debug0010_Memory0F94EightToSeven() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212679 1202 1194 81D6 0002 B7C6 FFFF 0040 0008 0007 0001 0000 0023 0042 00C3 0600".getBytes());
    assertEquals(0x0002, testData.getMemory7804());
    assertEquals(0xB7C6, testData.getMemory0FA8());
    assertEquals(0x0040, testData.getMemory0FB4());
    assertEquals(0x0008, testData.getMemory0F94());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterators = outputs.iterator();

    MB2FightData output1 = outputIterators.next();
    assertEquals(0x0002, output1.getMemory7804());
    assertEquals(0xB7C6, output1.getMemory0FA8());
    assertEquals(0x0040, output1.getMemory0FB4());
    assertEquals(0x0007, output1.getMemory0F94());

    MB2FightData output2 = outputIterators.next();
    assertEquals(0x0002, output2.getMemory7804());
    assertEquals(0xB7C6, output2.getMemory0FA8());
    assertEquals(0x0040, output2.getMemory0FB4());
    assertEquals(0x0007, output2.getMemory0F94());
  }

  @Test void debug0011_squatToMeatballTransition() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212702 0419 0E10 4826 0002 B7C6 FFFF 0040 0001 0000 0001 0000 0023 0056 00C3 0200".getBytes());
    assertEquals(0x0002, testData.getMemory7804());
    assertEquals(0xB7C6, testData.getMemory0FA8());
    assertEquals(0x0040, testData.getMemory0FB4());
    assertEquals(0x0001, testData.getMemory0F94());
    assertEquals(0x0000, testData.getMemory780E());
    assertEquals(0x0001, testData.getMemory7830());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);

    assertEquals(2, outputs.size());
    Iterator<MB2FightData> outputIterators = outputs.iterator();

    MB2FightData output1 = outputIterators.next();
    assertEquals(0x0003, output1.getMemory7804());
    assertEquals(0xB7C6, output1.getMemory0FA8()); // goes to B7E8 the following frame
    assertEquals(0x0040, output1.getMemory0FB4());
    assertEquals(0x0000, output1.getMemory780E());
    assertEquals(0x0001, output1.getMemory7830());

    MB2FightData output2 = outputIterators.next();
    assertEquals(0x0003, output2.getMemory7804());
    assertEquals(0xB7C6, output2.getMemory0FA8()); // goes to B7E8 the following frame
    assertEquals(0x0040, output2.getMemory0FB4());
    assertEquals(0x0000, output2.getMemory780E());
    assertEquals(0x0001, output2.getMemory7830());
  }

  @Test void debug0012_squatMeatballUnsquatReturn() {
    MB2FightData input = mb2SolverByteArrayConverter.fromByteArray("212678 1601 1194 04F0 0000 B64B FFFF 0000 FF04 0007 0000 0000 0023 0042 00C3 0600".getBytes());
    assertEquals(0x0000, input.getMemory7804());
    assertEquals(0xFFFF, input.getMemory0FB2());

    for (int i = 0; i < 104; i++) {
      Set<MB2FightData> outputs = mb2InputProcessor.process(input);
      MB2FightData output = outputs.iterator().next();

      input = output;
    }

    assertEquals(0x0000, input.getMemory7804());
    assertEquals(0xFFFF, input.getMemory0FB2());
  }

  @Test void debug0013_meatballFlagSet() {
    testData = mb2SolverByteArrayConverter.fromByteArray("212740 0002 0E10 DA98 0003 B7E8 0008 0040 FFE3 0000 0001 0000 0023 0072 00C3 0600".getBytes());
    assertEquals(0x0003, testData.getMemory7804());
    assertEquals(0xB7E8, testData.getMemory0FA8());
    assertEquals(0x0000, testData.getMemory784A());

    Set<MB2FightData> outputs = mb2InputProcessor.process(testData);
    assertEquals(1, outputs.size());

    MB2FightData output = outputs.iterator().next();
    assertEquals(0x0003, output.getMemory7804());
    assertEquals(0xB7E8, output.getMemory0FA8());
    assertEquals(0x0001, (output.getMemory784A() & 0x00FF));
  }

  @Test void debug0014_meatballFlagUnset() {
    MB2FightData input = mb2SolverByteArrayConverter.fromByteArray("212740 0002 0E10 DA98 0003 B7E8 0008 0040 FFE3 0000 0001 0000 0023 0072 00C3 0600".getBytes());
    assertTrue(input.getMemory784A() == 0x0000);

    for (int i = 0; i < 180; i++) {
      Set<MB2FightData> outputs = mb2InputProcessor.process(input);
      MB2FightData output = outputs.iterator().next();

      input = output;
      assertTrue(input.getMemory784A() > 0x0000);
    }

    Set<MB2FightData> outputs = mb2InputProcessor.process(input);
    MB2FightData output = outputs.iterator().next();
    input = output;

    assertTrue(input.getMemory784A() == 0x0000);
  }
}
