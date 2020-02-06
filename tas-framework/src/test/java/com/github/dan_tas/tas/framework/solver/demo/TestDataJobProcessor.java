package com.github.dan_tas.tas.framework.solver.demo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.github.dan_tas.tas.framework.FileUtility;
import com.github.dan_tas.tas.framework.converter.ByteArrayConverter;
import com.github.dan_tas.tas.framework.converter.PathConverter;
import com.github.dan_tas.tas.framework.converter.impl.DefaultPathConverterImpl;
import com.github.dan_tas.tas.framework.converter.impl.IntegerIntegerManyToManyMapByteArrayConverterImpl;
import com.github.dan_tas.tas.framework.impl.DefaultFileUtilityImpl;
import com.github.dan_tas.tas.framework.solver.FrameProcessor;
import com.github.dan_tas.tas.framework.solver.FrameRouteJoiner;
import com.github.dan_tas.tas.framework.solver.InputProcessor;
import com.github.dan_tas.tas.framework.solver.JobProcessor;
import com.github.dan_tas.tas.framework.solver.JobRouteJoiner;
import com.github.dan_tas.tas.framework.solver.impl.DefaultFrameProcessorImpl;
import com.github.dan_tas.tas.framework.solver.impl.DefaultFrameRouteJoinerImpl;
import com.github.dan_tas.tas.framework.solver.impl.FileBasedJobProcessorRouteJoinerImpl;

abstract class TestDataJobProcessor {
  // git checkout src/test/resources/test-data
  // before running this test
  @Test void testDataJobDebuggingTest() {
  FileUtility fileUtility = new DefaultFileUtilityImpl();
  ByteArrayConverter<TestData> byteArrayConverter = new TestDataByteArrayConverterImpl();
    IntegerIntegerManyToManyMapByteArrayConverterImpl manyMapByteArrayConverterImpl = new IntegerIntegerManyToManyMapByteArrayConverterImpl();

    InputProcessor<TestData> inputProcessor = new TestDataInputProcessorImpl();
    FrameProcessor<TestData> testDataFrameProcessor = new DefaultFrameProcessorImpl<>(inputProcessor, byteArrayConverter);
    FrameRouteJoiner frameRouteJoiner = new DefaultFrameRouteJoinerImpl();

    List<String> baseDirectory = Arrays.asList("src", "test", "resources", "test-data");
    PathConverter pathConverter = new DefaultPathConverterImpl(baseDirectory);
    JobProcessor<TestData> jobProcessor = new FileBasedJobProcessorRouteJoinerImpl<TestData>(pathConverter, fileUtility, byteArrayConverter, manyMapByteArrayConverterImpl, testDataFrameProcessor, frameRouteJoiner);

    Predicate<TestData> acceptOutput = output -> {
      return ((0 < output.getSum()) && (output.getSum() < 20));
    };
    jobProcessor.process(0, 21, acceptOutput);

    ((JobRouteJoiner)jobProcessor).postProcess(18, new HashSet<Integer>(Arrays.asList(Integer.valueOf(2))));
  }
}
