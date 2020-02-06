package com.github.dan_tas.tas.framework.solver.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.dan_tas.tas.framework.FileUtility;
import com.github.dan_tas.tas.framework.converter.ByteArrayConverter;
import com.github.dan_tas.tas.framework.converter.PathConverter;
import com.github.dan_tas.tas.framework.converter.impl.IntegerIntegerManyToManyMapByteArrayConverterImpl;
import com.github.dan_tas.tas.framework.solver.FrameMappingData;
import com.github.dan_tas.tas.framework.solver.FrameOutputData;
import com.github.dan_tas.tas.framework.solver.FrameProcessor;
import com.github.dan_tas.tas.framework.solver.FrameRouteJoiner;
import com.github.dan_tas.tas.framework.solver.JobProcessor;
import com.github.dan_tas.tas.framework.solver.JobRouteJoiner;

import lombok.extern.slf4j.Slf4j;

/**
 * Processes and post-processes a job by writing and reading, respectively, five data
 * files to frame-specific directories.
 *
 * @param <T> The class containing all relevant data for a given simulation
 */
@Slf4j
public class FileBasedJobProcessorRouteJoinerImpl<T> implements JobProcessor<T>, JobRouteJoiner {
  private static final String FILENAME_INPUTS = "inputs.txt";
  private static final String FILENAME_INPUT_INPUT_MAP = "input-input-map.txt";
  private static final String FILENAME_INPUT_OUTPUT_MAP = "input-output-map.txt";
  private static final String FILENAME_OUTPUTS = "outputs.txt";
  private static final String FILENAME_OUTPUT_INPUT_MAP = "output-input-map.txt";

  private static final String FAILED_TO_OPEN_INPUTS_FILE = "Failed to open inputs file: {}";
  private static final String FAILED_TO_OPEN_OUTPUTS_FILE = "Failed to open outputs file: {}";
  private static final String OUTPUT_UNACCEPTABLE = "Output is not acceptable: {}";


  private PathConverter pathConverter;
  private FileUtility fileUtility;

  private ByteArrayConverter<T> byteArrayConverter;
  private IntegerIntegerManyToManyMapByteArrayConverterImpl manyMapByteArrayConverterImpl;

  private FrameProcessor<T> frameProcessor;
  private FrameRouteJoiner frameRouteJoiner;

  public FileBasedJobProcessorRouteJoinerImpl(
    PathConverter pathConverter, FileUtility fileUtility, ByteArrayConverter<T> byteArrayConverter,
    IntegerIntegerManyToManyMapByteArrayConverterImpl manyMapByteArrayConverterImpl,
    FrameProcessor<T> frameProcessor, FrameRouteJoiner frameRouteJoiner) {
    this.pathConverter = pathConverter;
    this.fileUtility = fileUtility;
    this.byteArrayConverter = byteArrayConverter;
    this.manyMapByteArrayConverterImpl = manyMapByteArrayConverterImpl;
    this.frameProcessor = frameProcessor;
    this.frameRouteJoiner = frameRouteJoiner;
  }

  @Override public void process(int startingFrame, int framesToProcess, Predicate<T> isOutputAcceptable) {
    int framesProcessed = 0;
    int currentFrame = startingFrame;

    while (framesProcessed < framesToProcess) {
      log.debug("Processing frame {} ({} of {})", currentFrame, framesProcessed, framesToProcess);
      /* Re-read inputs because it de-duplicates inputs with different values for memory addresses
       * pertaining to Samus. Those are only used for output, not for input.
       */
      List<T> inputList = readInputs(currentFrame);
      FrameOutputData<T> frameOutputData = frameProcessor.process(inputList);
      persistOutputs(currentFrame, frameOutputData);

      framesProcessed++;
      currentFrame++;

      persistInputs(currentFrame, frameOutputData.getOutputList(), isOutputAcceptable);
    }
  }

  private List<T> readInputs(int frame) {
    List<T> inputList = new ArrayList<T>();
    Path workingDirectory = pathConverter.fromFrame(frame);
    Path inputPath = workingDirectory.resolve(FILENAME_INPUTS);

    try (Scanner inputs = new Scanner(inputPath)) {
      while(inputs.hasNext()) {
        String input = inputs.nextLine();
        T inputData = byteArrayConverter.fromByteArray(input.getBytes());
        inputList.add(inputData);
      }
    } catch (IOException e) {
      log.error(FAILED_TO_OPEN_INPUTS_FILE, inputPath.toString(), e);
    }

    return inputList;
  }

  private void persistOutputs(int frame, FrameOutputData<T> frameOutputData) {
    Path workingDirectory = pathConverter.fromFrame(frame);

    // Frame Outputs
    List<T> outputList = frameOutputData.getOutputList();
    Path outputPath = workingDirectory.resolve(FILENAME_OUTPUTS);
    String outputString = outputList.stream()
      .map(output -> new String(byteArrayConverter.toByteArray(output)))
      .collect(Collectors.joining(System.lineSeparator()));
    fileUtility.writeFile(outputPath, outputString.getBytes());

    // Input -> Input Duplicates Map
    Path inputInputMapPath = workingDirectory.resolve(FILENAME_INPUT_INPUT_MAP);
    IntegerIntegerManyToManyMapImpl inputInputMap = frameOutputData.getInputInputMap();
    byte[] inputInputByteArray = manyMapByteArrayConverterImpl.toByteArray(inputInputMap);
    fileUtility.writeFile(inputInputMapPath, inputInputByteArray);

    // Input -> Output Processed Map
    Path inputOutputMapPath = workingDirectory.resolve(FILENAME_INPUT_OUTPUT_MAP);
    IntegerIntegerManyToManyMapImpl inputOutputMap = frameOutputData.getInputOutputMap();
    byte[] inputOutputByteArray = manyMapByteArrayConverterImpl.toByteArray(inputOutputMap);
    fileUtility.writeFile(inputOutputMapPath, inputOutputByteArray);
  }

  private void persistInputs(int frame, List<T> outputList, Predicate<T> outputIsAcceptable) {
    IntegerIntegerManyToManyMapImpl outputInputMap = new IntegerIntegerManyToManyMapImpl();
    Set<String> manyToManyOutputInputMap = outputInputMap.getManyToManyMap();
    List<T> nextFrameInputs = new ArrayList<T>();
    int currentOutput = -1;

    for (T output : outputList) {
      currentOutput++;
      if (outputIsAcceptable.test(output)) {
        outputInputMap.add(currentOutput, manyToManyOutputInputMap.size());
        nextFrameInputs.add(output);
      } else {
        byte[] outputByteArray = byteArrayConverter.toByteArray(output);
        log.info(OUTPUT_UNACCEPTABLE, new String(outputByteArray));
      }
    }

    Path workingDirectory = pathConverter.fromFrame(frame);
    fileUtility.createDirectory(workingDirectory);

    // Frame Inputs
    Path inputPath = workingDirectory.resolve(FILENAME_INPUTS);
    String inputString = nextFrameInputs.stream()
      .map(byteArrayConverter::toByteArray).map(String::new)
      .collect(Collectors.joining(System.lineSeparator()));
    fileUtility.writeFile(inputPath, inputString.getBytes());

    // Output -> Input Map
    Path outputInputMapPath = workingDirectory.resolve(FILENAME_OUTPUT_INPUT_MAP);
    byte[] outputInputMapByteArray = manyMapByteArrayConverterImpl.toByteArray(outputInputMap);
    fileUtility.writeFile(outputInputMapPath, outputInputMapByteArray);
  }

  @Override public void postProcess(int endingFrame, Set<Integer> outputsToTraceBackwards) {
    int workingFrame = endingFrame;
    Path workingPath = pathConverter.fromFrame(workingFrame);

    Set<Integer> workingOutputs = outputsToTraceBackwards;
    while (Files.exists(workingPath)) {
      Path outputsPath = workingPath.resolve(FILENAME_OUTPUTS);
      printSelectedLineNumbers(outputsPath, workingOutputs);

      IntegerIntegerManyToManyMapImpl inputInputMap = getMap(workingPath, FILENAME_INPUT_INPUT_MAP);
      IntegerIntegerManyToManyMapImpl inputOutputMap = getMap(workingPath, FILENAME_INPUT_OUTPUT_MAP);
      IntegerIntegerManyToManyMapImpl outputInputMap = getMap(workingPath, FILENAME_OUTPUT_INPUT_MAP);

      FrameMappingData frameMappingData = new FrameMappingData();
      frameMappingData.setInputInputMap(inputInputMap);
      frameMappingData.setInputOutputMap(inputOutputMap);
      frameMappingData.setOutputInputMap(outputInputMap);

      Set<Integer> previousFrameOutputs = frameRouteJoiner.backOneFrame(workingOutputs, frameMappingData);

      workingFrame--;
      workingPath = pathConverter.fromFrame(workingFrame);
      workingOutputs = previousFrameOutputs;
    }
  }

  private IntegerIntegerManyToManyMapImpl getMap(Path workingDirectory, String relativeFile) {
  Path workingFile = workingDirectory.resolve(relativeFile);
  byte[] mapByteArray = fileUtility.readFile(workingFile);

  if (mapByteArray == null) {
    return new IntegerIntegerManyToManyMapImpl(); // Valid case for when a map has 0 entries
  }
  return manyMapByteArrayConverterImpl.fromByteArray(mapByteArray);
  }

  private void printSelectedLineNumbers(Path outputsPath, Set<Integer> outputsToTraceBackwards) {
    try (Scanner outputs = new Scanner(outputsPath)) {
      int lineNumber = 0;
      while(outputs.hasNext()) {
        String output = outputs.nextLine();
        if (outputsToTraceBackwards.contains(lineNumber)) {
          log.info(String.format("  %06d: %s", lineNumber, output));
        }
        lineNumber++;
      }
    } catch (IOException e) {
      log.error(FAILED_TO_OPEN_OUTPUTS_FILE, outputsPath.toString(), e);
    }
  }
}
