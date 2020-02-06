package com.github.dan_tas.tas.framework.solver.impl;

import java.util.List;
import java.util.Set;

import com.github.dan_tas.tas.framework.converter.ByteArrayConverter;
import com.github.dan_tas.tas.framework.solver.FrameOutputData;
import com.github.dan_tas.tas.framework.solver.FrameProcessor;
import com.github.dan_tas.tas.framework.solver.InputProcessor;
import com.github.dan_tas.tas.framework.solver.ManyToManyMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultFrameProcessorImpl<T> implements FrameProcessor<T> {
  private static final String INPUT_DID_NOT_GENERATE_OUTPUT = "Failed to create any output for a frame input: {}";
  private static final String DUPLICATE_FOUND = "Duplicate %s for %06d: %s";

  private InputProcessor<T> inputProcessor;
  private ByteArrayConverter<T> byteArrayConverter;

  public DefaultFrameProcessorImpl(InputProcessor<T> inputProcessor, ByteArrayConverter<T> byteArrayConverter) {
    this.inputProcessor = inputProcessor;
    this.byteArrayConverter = byteArrayConverter;
  }

  @Override public FrameOutputData<T> process(List<T> inputList) {
    FrameOutputData<T> frameOutputData = new FrameOutputData<T>();
    ManyToManyMap<Integer, Integer> inputInputMap = frameOutputData.getInputInputMap();
    int currentInput = -1;

    next_input:
    for (T inputData : inputList) {
      currentInput++;
      int firstOccurence = inputList.indexOf(inputData);

      boolean duplicateInput = (currentInput > firstOccurence);
      if(duplicateInput) {
      inputInputMap.add(firstOccurence, currentInput);

        byte[] duplicateByteArray = byteArrayConverter.toByteArray(inputData);
        log.debug(String.format(DUPLICATE_FOUND, "input", currentInput, new String(duplicateByteArray)));

        continue next_input;
      }

      Set<T> outputDatas = inputProcessor.process(inputData);
      if(outputDatas == null || outputDatas.isEmpty()) {
        byte[] noOutputsByteArray = byteArrayConverter.toByteArray(inputData);
        log.warn(INPUT_DID_NOT_GENERATE_OUTPUT, new String(noOutputsByteArray));

        continue next_input;
      }

      processOutputs(currentInput, outputDatas, frameOutputData);
    }

    return frameOutputData;
  }

  private void processOutputs(int inputNumber, Set<T> outputDatas, FrameOutputData<T> frameOutputData) {
    List<T> outputList = frameOutputData.getOutputList();
    ManyToManyMap<Integer, Integer> inputOutputMap = frameOutputData.getInputOutputMap();

    for(T outputData : outputDatas) {
      int outputNumber = outputList.size();
      boolean duplicateOutput = outputList.contains(outputData);

      if (duplicateOutput) {
        outputNumber = outputList.indexOf(outputData);
        byte[] duplicateByteArray = byteArrayConverter.toByteArray(outputData);
        log.debug(String.format(DUPLICATE_FOUND, "output", outputNumber, new String(duplicateByteArray)));
      } else {
        outputList.add(outputData);
      }

      // Still log the duplicates, for tracing back the route purposes
      inputOutputMap.add(inputNumber, outputNumber);
    }
  }
}
