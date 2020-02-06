package com.github.dan_tas.tas.framework.converter;

/**
 * Converts an in-memory object to a byte array for external storage
 *
 * @param <T> The data class to serialize and deserialize into a byte array.
 */
public interface ByteArrayConverter<T> {
  /**
   * Serializes the given input to a byte array
   * @param in The instance of T to convert into a byte array
   * @return A byte array representing the input
   */
  byte[] toByteArray(T in);

  /**
   * Deserialize the given input to an instance of T
   * @param in The byte array to convert into an instance of T
   * @return A new instance of T representing the input
   */
  T fromByteArray(byte[] in);
}
