package com.github.dan_tas.tas.framework.solver;

import java.util.Set;

/**
 * Traces the outputs of interest from the end of the simulation to the beginning
 */
@FunctionalInterface
public interface FrameRouteJoiner {
  /**
   * Traces the outputs from the end of the simulation to the beginning, one frame at a time
   *
   * @param specificOutputs The outputs of interest from frame N+1
   * @param frameMappingData The data that maps the outputs from frame N to frame N+1
   * @return the outputs from frame N that generated the outputs of interest on frame N+1
   */
  Set<Integer> backOneFrame(Set<Integer> specificOutputs, FrameMappingData frameMappingData);
}
