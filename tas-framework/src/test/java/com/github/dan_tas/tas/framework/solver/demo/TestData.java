package com.github.dan_tas.tas.framework.solver.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestData implements Comparable<TestData> {
  private int adjust;
  private int sum;

  @Override public int compareTo(TestData arg0) {
  return Integer.compare(this.hashCode(), arg0.hashCode());
  }
}
