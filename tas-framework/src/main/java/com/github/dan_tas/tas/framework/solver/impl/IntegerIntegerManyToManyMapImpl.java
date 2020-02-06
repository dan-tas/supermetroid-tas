package com.github.dan_tas.tas.framework.solver.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.dan_tas.tas.framework.solver.ManyToManyMap;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegerIntegerManyToManyMapImpl implements ManyToManyMap<Integer, Integer> {
  private static final String MAP_FORMAT = "%06d %06d";
  private static final String MAP_ENTRY_REGEX = "^\\d{6} \\d{6}$";

  @Getter private Set<String> manyToManyMap = new HashSet<>();

  @Override public boolean add(Integer key, Integer value) {
    String mapEntry = String.format(MAP_FORMAT, key, value);
    if (!mapEntry.matches(MAP_ENTRY_REGEX)) {
      return false;
    }

    return manyToManyMap.add(mapEntry);
  }

  @Override public Map<Integer, Set<Integer>> groupByKeys() {
    Map<Integer, Set<Integer>> groupByOutputMap = new HashMap<>();

    for (String mapEntry : manyToManyMap) {
      if (mapEntry.matches(MAP_ENTRY_REGEX)) {
        String inputString = mapEntry.substring(0, "123456".length());
        String outputString = mapEntry.substring("123456 ".length(), "123456 123456".length());

        Integer inputInteger = Integer.parseInt(inputString);
        Integer outputInteger = Integer.parseInt(outputString);

        Set<Integer> inputsSet = groupByOutputMap.computeIfAbsent(inputInteger, inputOutputsMapEntry -> new HashSet<>());
        inputsSet.add(outputInteger);
      } else {
        log.warn("Map entry did not match the expected regex when grouping by keys: {}", mapEntry);
      }
    }

    return groupByOutputMap;
  }

  @Override public Map<Integer, Set<Integer>> groupByValues() {
  Map<Integer, Set<Integer>> groupByOutputMap = new HashMap<>();

    for (String mapEntry : manyToManyMap) {
      if (mapEntry.matches(MAP_ENTRY_REGEX)) {
        String inputString = mapEntry.substring("".length(), "123456".length());
        String outputString = mapEntry.substring("123456 ".length(), "123456 123456".length());

        Integer inputInteger = Integer.parseInt(inputString);
        Integer outputInteger = Integer.parseInt(outputString);

        Set<Integer> inputsSet = groupByOutputMap.computeIfAbsent(outputInteger, outputInputsMapEntry -> new HashSet<>());
        inputsSet.add(inputInteger);
      } else {
        log.warn("Map entry did not match the expected regex when grouping by values: {}", mapEntry);
      }
    }

    return groupByOutputMap;
  }
}
