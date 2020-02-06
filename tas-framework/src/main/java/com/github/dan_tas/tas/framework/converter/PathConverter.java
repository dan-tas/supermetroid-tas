package com.github.dan_tas.tas.framework.converter;

import java.nio.file.Path;

/**
 *  This class calculates a working directory for each frame
 */
@FunctionalInterface
public interface PathConverter {
  /**
   * Creates a path representing the working directory for the given frame
   * @param frame The given frame for conversion
   * @return The working directory for this given frame
   */
  Path fromFrame(int frame);
}
