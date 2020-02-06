package com.github.dan_tas.tas.framework.solver;

import java.util.Set;

/**
 * Processes the given data for a single frame of emulation.
 *
 * @param <T> The class containing all relevant data for a given simulation
 */
@FunctionalInterface
public interface InputProcessor<T> {
  /**
   * Processes the given input data for one frame
   * @param in The data to process as part of a simulation
   * @return A collection of all possible outcomes for the given input
   */
  Set<T> process(T in);
}
