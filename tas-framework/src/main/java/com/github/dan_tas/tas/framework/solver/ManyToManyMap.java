package com.github.dan_tas.tas.framework.solver;

import java.util.Map;
import java.util.Set;

/**
 * A map where one key can be associated to multiple values, and at
 * the same time, one value can be associated to multiple keys.
 *
 * @param <K> The data type of the map key
 * @param <V> The data type of the map value
 */
public interface ManyToManyMap<K, V> {
  /**
   * Adds an entry to the map
   * @param key the map key to add
   * @param value the map value to add
   * @return true if the value was added or false if the value already exists
   */
  boolean add(K key, V value);

  /**
   * Group the map by key
   * @return The map, where values are grouped by key
   */
  Map<K, Set<V>> groupByKeys();
  /**
   * Group the map by value
   * @return The map, where keys are grouped by value
   */
  Map<V, Set<K>> groupByValues();
}
