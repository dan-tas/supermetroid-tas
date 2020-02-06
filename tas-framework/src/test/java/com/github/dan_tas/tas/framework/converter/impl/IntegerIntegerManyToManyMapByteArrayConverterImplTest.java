package com.github.dan_tas.tas.framework.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.dan_tas.tas.framework.solver.impl.IntegerIntegerManyToManyMapImpl;

class IntegerIntegerManyToManyMapByteArrayConverterImplTest {
  private IntegerIntegerManyToManyMapByteArrayConverterImpl integerIntegerManyToManyMapByteArrayConverterImpl = new IntegerIntegerManyToManyMapByteArrayConverterImpl();

  @Test void invalidInputFromByteArray() {
    byte[] invalidInputByteArray = "1234567 12345".getBytes();
    Assertions.assertThrows(IllegalArgumentException.class, () -> integerIntegerManyToManyMapByteArrayConverterImpl.fromByteArray(invalidInputByteArray));
  }

  @Test void toByteArrayFromByteArraySame() {
    IntegerIntegerManyToManyMapImpl manyToManyMapImplIn = manyToManyMap000();

    byte[] manyToManyMapByteArrayOut = integerIntegerManyToManyMapByteArrayConverterImpl.toByteArray(manyToManyMapImplIn);
    IntegerIntegerManyToManyMapImpl manyToManyMapImplOut = integerIntegerManyToManyMapByteArrayConverterImpl.fromByteArray(manyToManyMapByteArrayOut);

    assertManyToManyMap(manyToManyMapImplIn);
    assertManyToManyMap(manyToManyMapImplOut);
  }

  @Test void testFromByteArrayToByteArraySame() {
    byte[] manyToManyMapByteArrayIn = manyToManyMapByteArray000();

    IntegerIntegerManyToManyMapImpl manyToManyMapImplOut = integerIntegerManyToManyMapByteArrayConverterImpl.fromByteArray(manyToManyMapByteArrayIn);
    byte[] manyToManyMapByteArrayOut = integerIntegerManyToManyMapByteArrayConverterImpl.toByteArray(manyToManyMapImplOut);

    assertManyToManyMap(manyToManyMapImplOut);

    String manyToManyMapByteArrayOutString = new String(manyToManyMapByteArrayOut);
    String manyToManyMapByteArray001String = new String(manyToManyMapByteArray001());
    assertEquals(manyToManyMapByteArray001String, manyToManyMapByteArrayOutString);
  }

  private IntegerIntegerManyToManyMapImpl manyToManyMap000() {
    IntegerIntegerManyToManyMapImpl manyToManyMapImpl = new IntegerIntegerManyToManyMapImpl();

    manyToManyMapImpl.add(0, 1);
    manyToManyMapImpl.add(2, 5);
    manyToManyMapImpl.add(3, 7);
    manyToManyMapImpl.add(5, 9);

    return manyToManyMapImpl;
  }

  private byte[] manyToManyMapByteArray000() {
  return ("000000 000001" + System.lineSeparator() + "000002 000005" + System.lineSeparator() + "000003 000007" + System.lineSeparator() + "000005 000009").getBytes();
  }
  private byte[] manyToManyMapByteArray001() {
  return ("000005 000009" + System.lineSeparator() + "000002 000005" + System.lineSeparator() + "000003 000007" + System.lineSeparator() + "000000 000001").getBytes();
  }

  private void assertManyToManyMap(IntegerIntegerManyToManyMapImpl manyToManyMap) {
    assertNotNull(manyToManyMap);

    Set<String> manyToManySet = manyToManyMap.getManyToManyMap();
    assertEquals(4, manyToManySet.size());

    assertTrue(manyToManySet.contains("000000 000001"));
    assertTrue(manyToManySet.contains("000002 000005"));
    assertTrue(manyToManySet.contains("000003 000007"));
    assertTrue(manyToManySet.contains("000005 000009"));
  }
}
