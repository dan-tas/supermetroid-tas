package com.github.dan_tas.tas.framework.solver;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FrameOutputData<T> extends FrameMappingData {
  private List<T> outputList = new ArrayList<>();
}
