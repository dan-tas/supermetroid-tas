package com.github.dan_tas.snes.supermetroid.enemy.motherbrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.github.dan_tas.tas.framework.converter.ByteArrayConverter;
import com.github.dan_tas.tas.framework.converter.PathConverter;
import com.github.dan_tas.tas.framework.converter.impl.DefaultPathConverterImpl;
import com.github.dan_tas.tas.framework.converter.impl.IntegerIntegerManyToManyMapByteArrayConverterImpl;
import com.github.dan_tas.tas.framework.solver.FrameProcessor;
import com.github.dan_tas.tas.framework.solver.FrameRouteJoiner;
import com.github.dan_tas.tas.framework.solver.InputProcessor;
import com.github.dan_tas.tas.framework.solver.JobProcessor;
import com.github.dan_tas.tas.framework.solver.JobRouteJoiner;
import com.github.dan_tas.tas.framework.solver.impl.DefaultFrameProcessorImpl;
import com.github.dan_tas.tas.framework.solver.impl.DefaultFrameRouteJoinerImpl;
import com.github.dan_tas.tas.framework.solver.impl.FileBasedJobProcessorRouteJoinerImpl;

import lombok.extern.slf4j.Slf4j;

import com.github.dan_tas.snes.supermetroid.rng.RngService;
import com.github.dan_tas.snes.supermetroid.rng.impl.LcgRngServiceImpl;
import com.github.dan_tas.tas.framework.FileUtility;
import com.github.dan_tas.tas.framework.impl.DefaultFileUtilityImpl;

@Slf4j
public class MB2FightJobProcessor {
  public static void main(String[] args) {
    FileUtility fileUtility = new DefaultFileUtilityImpl();
    ByteArrayConverter<MB2FightData> byteArrayConverter = new MB2FightByteArrayConverterImpl();
    IntegerIntegerManyToManyMapByteArrayConverterImpl manyMapByteArrayConverterImpl = new IntegerIntegerManyToManyMapByteArrayConverterImpl();

    RngService rngService = new LcgRngServiceImpl();
    InputProcessor<MB2FightData> inputProcessor = new MB2FightInputProcessorImpl(rngService);
    FrameProcessor<MB2FightData> mb2FightFrameProcessor = new DefaultFrameProcessorImpl<>(inputProcessor, byteArrayConverter);
    FrameRouteJoiner frameRouteJoiner = new DefaultFrameRouteJoinerImpl();

    List<String> rngValues = Collections.singletonList("B079"); // Arrays.asList("42BB", "4EB8", "8AA9", "B65E", "90E7", "D594", "2CF5", "E1DA", "6A53", "14B0", "6881", "0B96", "3AFF", "280C", "C94D", "EF92", "AEEB", "6BA8", "1B59", "89CE", "B217", "7B84", "6AA5", "164A", "7083", "33A0", "0332", "110B", "5648", "B079", "736E", "4237", "4C24", "7DC5", "75EA", "4EA3", "8A40", "B451", "86A6", "A24F", "2C9C", "E01D", "61A2", "E93B", "8F38", "CD29", "02DE", "0F67", "4E14", "8775", "A65A", "40D3", "4530", "5B01", "C816", "E97F", "908C", "D3CD", "2412", "B56B", "8C28");
    for (String initialRng : rngValues) {
      List<String> baseDirectory = new ArrayList<String>(Arrays.asList("C:", "Users", "Dan-TAS", "Desktop", "sniq-100-mb2", initialRng));
      PathConverter pathConverter = new DefaultPathConverterImpl(baseDirectory);
      JobProcessor<MB2FightData> jobProcessor = new FileBasedJobProcessorRouteJoinerImpl<MB2FightData>(pathConverter, fileUtility, byteArrayConverter, manyMapByteArrayConverterImpl, mb2FightFrameProcessor, frameRouteJoiner);

      boolean processPostProcess = false;
      if (args != null && args.length > 0) {
        processPostProcess = ("process".equals(args[0]));
      }

      if (processPostProcess) {
          log.info("Starting new MB2 fight with RNG: " + initialRng);
          Predicate<MB2FightData> acceptOutput = output -> {
            return (output.getMemory0FA8() != 0xB8EB) && (output.getMemory0FA8() != 0xB891)  // rainbow beam
                && (output.getMemory0FA8() != 0xB87D) && (output.getMemory0FA8() != 0xFA17); // ketchup, error
          };
          jobProcessor.process(211792, 1155, acceptOutput);
      } else {
        int endingFrame = 212939;
        Set<Integer> outputsToTraceBackwards = Collections.singleton(6);
        log.info("Tracing output of MB2 fight backwards: ending frame {}, specific outputs: {}", endingFrame, outputsToTraceBackwards);
        ((JobRouteJoiner)jobProcessor).postProcess(endingFrame, outputsToTraceBackwards);
      }
    }
  }
}
