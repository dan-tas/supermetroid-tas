package com.github.dan_tas.tas.framework.solver;

import com.github.dan_tas.tas.framework.solver.impl.IntegerIntegerManyToManyMapImpl;

import lombok.Data;

@Data
public class FrameMappingData {
  private IntegerIntegerManyToManyMapImpl inputInputMap = new IntegerIntegerManyToManyMapImpl(); // duplicate inputs are added here
  private IntegerIntegerManyToManyMapImpl inputOutputMap = new IntegerIntegerManyToManyMapImpl();
  private IntegerIntegerManyToManyMapImpl outputInputMap = new IntegerIntegerManyToManyMapImpl(); // duplicates outputs are added here
}
