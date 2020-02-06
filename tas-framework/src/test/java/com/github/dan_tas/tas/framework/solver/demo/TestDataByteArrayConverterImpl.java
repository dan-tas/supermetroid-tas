package com.github.dan_tas.tas.framework.solver.demo;

import com.github.dan_tas.tas.framework.converter.ByteArrayConverter;

public class TestDataByteArrayConverterImpl implements ByteArrayConverter<TestData> {

  @Override public byte[] toByteArray(TestData in) {
  String output = String.format("%04X %04X", in.getAdjust() & 0xFFFF, in.getSum() & 0xFFFF);

  return output.getBytes();
  }

  @Override public TestData fromByteArray(byte[] in) {
  String inputString = new String(in);
  String adjustString = inputString.substring(0, 4);
  String sumString = inputString.substring(5, 9);

  int adjust = Integer.parseInt(adjustString, 0x10);
  int sum = Integer.parseInt(sumString, 0x10);

  return new TestData(adjust, sum);
  }
}
