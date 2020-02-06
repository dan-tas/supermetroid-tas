package com.github.dan_tas.tas.framework;

import java.nio.file.Path;

/**
 * This provides several utility methods for dealing with files
 * Any I/O exceptions are caught and not re-thrown
 */
public interface FileUtility {
  /**
   * Creates the given directory
   * @param directoryToCreate The directory to be created
   * @return true if the directory was created successfully
   */
  boolean createDirectory(Path directoryToCreate);

  /**
   * Writes the given content to the given path
   * @param fileToWrite The path where the content should be written
   * @param content The content to write to the given file
   * @return true if the content was written to the path successfully
   */
  boolean writeFile(Path fileToWrite, byte[] content);

  /**
   * Reads a file from disk, and logs an error if there is a failure
   * @param fileToRead The Path to the file to read from disk
   * @return The content of the file as a byte array
   */
  byte[] readFile(Path fileToRead);
}
