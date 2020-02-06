package com.github.dan_tas.tas.framework.solver;

import java.util.Set;

/**
 * This takes all data from a simulation and identifies how to get to the given outputs
 * of interest from the beginning of the simulation.
 */
@FunctionalInterface
public interface JobRouteJoiner {
  /**
   * Traverses all simulation data from the end of the simulation to the beginning.
   * Retains only relevant inputs/outputs
   * @param endingFrame The frame to start at when working backwards
   * @param outputsToTraceBackwards The set of specific outputs used to map backwards
   */
  void postProcess(int endingFrame, Set<Integer> outputsToTraceBackwards);
}
