package com.github.dan_tas.tas.framework.solver.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import com.github.dan_tas.tas.framework.solver.FrameMappingData;
import com.github.dan_tas.tas.framework.solver.FrameRouteJoiner;
import com.github.dan_tas.tas.framework.solver.ManyToManyMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultFrameRouteJoinerImpl implements FrameRouteJoiner {
  @Override public Set<Integer> backOneFrame(Set<Integer> specificOutputs, FrameMappingData frameMappingData) {
    Map<Integer, Set<Integer>> inputsGroupedByOutput = outputsToInputs(specificOutputs, frameMappingData);
    Set<Integer> specificInputs = accumulateValues(inputsGroupedByOutput);

    Map<Integer, Set<Integer>> outputsGroupedByInput = inputsToOutputs(specificInputs, frameMappingData);
    return accumulateValues(outputsGroupedByInput);
  }

  private Map<Integer, Set<Integer>> outputsToInputs(Set<Integer> specificOutputs, FrameMappingData frameOutputData) {
    Map<Integer, Set<Integer>> outputInputsMapWithDuplicates = new HashMap<>();

    Map<Integer, Set<Integer>> outputInputsMap = frameOutputData.getInputOutputMap().groupByValues();
    Map<Integer, Set<Integer>> inputInputsMap = frameOutputData.getInputInputMap().groupByKeys();
    Set<Entry<Integer, Set<Integer>>> outputInputsMapEntries = outputInputsMap.entrySet();
    if (outputInputsMapEntries != null) {
      for (Entry<Integer, Set<Integer>> outputInputsMapEntry : outputInputsMapEntries) {
        if (specificOutputs.contains(outputInputsMapEntry.getKey())) {
          Set<Integer> inputsWithDuplicates = addDuplicateInputs(outputInputsMapEntry.getValue(), inputInputsMap);
          outputInputsMapWithDuplicates.put(outputInputsMapEntry.getKey(), inputsWithDuplicates);
        }
      }
    }

    return outputInputsMapWithDuplicates;
  }
  private Set<Integer> addDuplicateInputs(Set<Integer> specificInputs, Map<Integer, Set<Integer>> inputInputsMap) {
    Set<Integer> outputInputsMapEntryWithDuplicates = new HashSet<>();
    outputInputsMapEntryWithDuplicates.addAll(specificInputs);

    for (Integer input : specificInputs) {
      Set<Integer> duplicateInputs = inputInputsMap.get(input);
      if (duplicateInputs != null) {
        outputInputsMapEntryWithDuplicates.addAll(duplicateInputs);
      }
    }

    return outputInputsMapEntryWithDuplicates;
  }

  private Set<Integer> accumulateValues(Map<Integer, Set<Integer>> map) {
    log.debug("Accumulating map values: {}", map);

    return map.values().stream().flatMap(x -> x.stream()).collect(Collectors.toSet());
  }

  private Map<Integer, Set<Integer>> inputsToOutputs(Set<Integer> specificInputs, FrameMappingData frameMappingData) {
    ManyToManyMap<Integer, Integer> outputInputMap = frameMappingData.getOutputInputMap();
    Map<Integer, Set<Integer>> inputOutputsMap = outputInputMap.groupByValues();
    Map<Integer, Set<Integer>> filteredInputOutputsMap = inputOutputsMap.entrySet().stream()
      .filter(i -> specificInputs.contains(i.getKey()))
      .filter(o -> !o.getValue().isEmpty())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    return filteredInputOutputsMap;
  }
}
