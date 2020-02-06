package com.github.dan_tas.tas.framework.converter.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.dan_tas.tas.framework.converter.PathConverter;

public class DefaultPathConverterImpl implements PathConverter {
  private List<String> baseDirectory;

  public DefaultPathConverterImpl(List<String> workingDirectory) {
    this.baseDirectory = workingDirectory;
  }

  /**
   * Creates the given path by padding the frame into a 6-digit number
   */
  @Override public Path fromFrame(int frame) {
    String frameString = String.format("%06d", frame);
    if (baseDirectory.isEmpty()) {
      return Paths.get(frameString);
    } else {
      List<String> workingDirectory = new ArrayList<>(baseDirectory);
      workingDirectory.add(frameString);

      String firstDirectory = workingDirectory.get(0);
      workingDirectory.remove(0);
      String[] remainingDirectories = workingDirectory.toArray(new String[0]);

      return Paths.get(firstDirectory, remainingDirectories);
    }
  }
}
