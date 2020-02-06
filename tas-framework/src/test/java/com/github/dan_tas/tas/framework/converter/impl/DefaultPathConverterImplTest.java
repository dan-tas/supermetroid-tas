package com.github.dan_tas.tas.framework.converter.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.dan_tas.tas.framework.converter.PathConverter;

class DefaultPathConverterImplTest {
  private PathConverter pathConverter;

  @Test void testNoDirectories() {
    pathConverter = new DefaultPathConverterImpl(Collections.emptyList());

    Path testPath = pathConverter.fromFrame(123456);

    assertTrue("123456".equals(testPath.toString()));
  }

  @Test void testRelativePath() {
    List<String> workingDirectory = new ArrayList<String>(Arrays.asList("one", "two", "uno", "dos"));
    pathConverter = new DefaultPathConverterImpl(workingDirectory);

    Path testPath = pathConverter.fromFrame(123);

    String expected = "one" + File.separator + "two" + File.separator + "uno" + File.separator + "dos" + File.separator + "000123";
    assertTrue(expected.equals(testPath.toString()));
  }

  @Test void testAbsolutePath() {
    List<String> workingDirectory = new ArrayList<String>(Arrays.asList("C:", "Users", "Dan-TAS"));
    pathConverter = new DefaultPathConverterImpl(workingDirectory);

    Path testPath = pathConverter.fromFrame(456);

    String expected = "C:" + File.separator + "Users" + File.separator + "Dan-TAS" + File.separator + "000456";
    assertTrue(expected.equals(testPath.toString()));
  }
}
