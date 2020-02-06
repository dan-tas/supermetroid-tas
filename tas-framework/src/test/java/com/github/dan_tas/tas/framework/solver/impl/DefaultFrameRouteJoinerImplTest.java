package com.github.dan_tas.tas.framework.solver.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.dan_tas.tas.framework.solver.FrameMappingData;
import com.github.dan_tas.tas.framework.solver.FrameRouteJoiner;

class DefaultFrameRouteJoinerImplTest {
  private FrameRouteJoiner frameRouteJoiner = new DefaultFrameRouteJoinerImpl();

  @Test void testHappyPath() {
  Set<Integer> specificOutputsPreviousFrame = frameRouteJoiner.backOneFrame(specificOutputs000(), frameMappingData000());

  assertNotNull(specificOutputsPreviousFrame);
  assertEquals(3, specificOutputsPreviousFrame.size());

  assertTrue(specificOutputsPreviousFrame.contains(1));
  assertTrue(specificOutputsPreviousFrame.contains(2));
  assertTrue(specificOutputsPreviousFrame.contains(4));
  }

  private Set<Integer> specificOutputs000() {
    Set<Integer> specificOutputs = new HashSet<>();

    specificOutputs.add(2);
    specificOutputs.add(3);

    return specificOutputs;
  }

  private FrameMappingData frameMappingData000() {
    FrameMappingData frameOutputData = new FrameMappingData();

    frameOutputData.setInputInputMap(inputInputMap000());
    frameOutputData.setInputOutputMap(inputOutputMap000());
    frameOutputData.setOutputInputMap(outputInputMap000());

    return frameOutputData;
  }

  private IntegerIntegerManyToManyMapImpl inputInputMap000() {
    IntegerIntegerManyToManyMapImpl manyToManyMapImpl = new IntegerIntegerManyToManyMapImpl();

      manyToManyMapImpl.add(2, 4);
      manyToManyMapImpl.add(3, 5);

      return manyToManyMapImpl;
  }

  private IntegerIntegerManyToManyMapImpl outputInputMap000() {
    IntegerIntegerManyToManyMapImpl manyToManyMapImpl = new IntegerIntegerManyToManyMapImpl();

    manyToManyMapImpl.add(1, 1);
    manyToManyMapImpl.add(2, 2);
    manyToManyMapImpl.add(3, 3);
    manyToManyMapImpl.add(4, 4);

    return manyToManyMapImpl;

  }
  private IntegerIntegerManyToManyMapImpl inputOutputMap000() {
    IntegerIntegerManyToManyMapImpl manyMapImpl = new IntegerIntegerManyToManyMapImpl();

    manyMapImpl.add(1, 1);
    manyMapImpl.add(2, 3);
    manyMapImpl.add(3, 5);
    manyMapImpl.add(1, 2);
    manyMapImpl.add(2, 4);

    return manyMapImpl;
  }
}
