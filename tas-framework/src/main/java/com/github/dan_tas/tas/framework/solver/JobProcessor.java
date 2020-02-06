package com.github.dan_tas.tas.framework.solver;

import java.util.function.Predicate;

/**
 * Runs a simulation for consecutive frames by filtering the output from
 * current frame and forwarding the outputs as inputs into the next frame
 *
 * @param <T> The class containing all relevant data for a given simulation
 */
@FunctionalInterface
public interface JobProcessor<T> {
  /**
   * Processes a simulation for multiple consecutive frames
   * @param startingFrame The starting frame for processing this simulation
   * @param framesToProcess The number of frames to process for this simulation
   * @param isOutputAcceptable A predicate deciding whether the output should be pruned or kept for further simulation
   */
  void process(int startingFrame, int framesToProcess, Predicate<T> isOutputAcceptable);
}
