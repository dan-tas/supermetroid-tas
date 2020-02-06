package com.github.dan_tas.tas.framework.solver;

import java.util.List;

/**
 * Handles processing all possible combinations of input data for the given frame
 *
 * @param <T> The class containing all relevant data for a given simulation
 */
@FunctionalInterface
public interface FrameProcessor<T> {
  /**
   * Processes all data for a given frame, and return the resulting data relating the input and output data
   * @param inputList The list of inputs to process this frame
   * @return The unique outputs, and data mapping the inputs to unique outputs, or other inputs in case of duplicates
   */
  FrameOutputData<T> process(List<T> inputList);
}
