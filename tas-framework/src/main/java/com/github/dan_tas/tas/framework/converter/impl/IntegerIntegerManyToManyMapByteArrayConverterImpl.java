package com.github.dan_tas.tas.framework.converter.impl;

import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.dan_tas.tas.framework.converter.ByteArrayConverter;
import com.github.dan_tas.tas.framework.solver.impl.IntegerIntegerManyToManyMapImpl;

/*
 * This class name is an atrocity. What have I done.
 */
public class IntegerIntegerManyToManyMapByteArrayConverterImpl implements ByteArrayConverter<IntegerIntegerManyToManyMapImpl> {

  @Override public byte[] toByteArray(IntegerIntegerManyToManyMapImpl in) {
    Set<String> manyToManyMap = in.getManyToManyMap();
    String manyToManyMapString = manyToManyMap.stream().collect(Collectors.joining(System.lineSeparator()));

    return manyToManyMapString.getBytes();
  }

  @Override public IntegerIntegerManyToManyMapImpl fromByteArray(byte[] in) {
    IntegerIntegerManyToManyMapImpl manyToManyMapImpl = new IntegerIntegerManyToManyMapImpl();

    String input = new String(in);
    try (Scanner scanner = new Scanner(input)) {
      while (scanner.hasNextLine()) {
        Integer key = scanner.nextInt();
        Integer value = scanner.nextInt();
        boolean added = manyToManyMapImpl.add(key, value);
        if (!added) {
          throw new IllegalArgumentException("Failure due to an unexpected entry: " + key + "=" + value);
        }
      }
    }

    return manyToManyMapImpl;
  }
}
