package com.github.dan_tas.tas.framework.solver.demo;

import java.util.HashSet;
import java.util.Set;

import com.github.dan_tas.tas.framework.solver.InputProcessor;

public class TestDataInputProcessorImpl implements InputProcessor<TestData> {
  @Override public Set<TestData> process(TestData in) {
    Set<TestData> outputs = new HashSet<>();

    int sum1 = in.getSum() - in.getAdjust();
    int sum2 = in.getSum() + in.getAdjust();
    int nextAdjust = in.getAdjust() + 1;

    TestData testData1 = new TestData(nextAdjust, sum1);
    TestData testData2 = new TestData(nextAdjust, sum2);

    outputs.add(testData1);
    outputs.add(testData2);

    return outputs;
  }
}
