package com.github.dan_tas.tas.framework.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.dan_tas.tas.framework.FileUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultFileUtilityImpl implements FileUtility {
  @Override public boolean createDirectory(Path directoryToCreate) {
      try {
        Files.createDirectory(directoryToCreate);
        return true;
      } catch (IOException e) {
        log.error("Failed to create directory \"{}\": {}", directoryToCreate.toString(), e.getMessage(), e);
        return false;
      }
  }

  @Override public boolean writeFile(Path fileToWrite, byte[] content) {
    try {
      Files.write(fileToWrite, content);
      return true;
    } catch (IOException e) {
      log.error("Failed to write file \"{}\" with {} bytes: {}", fileToWrite.toString(), content.length, e.getMessage(), e);
      return false;
    }
  }

  @Override public byte[] readFile(Path fileToRead) {
    byte[] fileBytes = null;
    try {
      fileBytes = Files.readAllBytes(fileToRead);
    } catch (IOException e) {
      log.error("Failed to read file \"{}\": {}", fileToRead.toString(), e.getMessage(), e);
    }

    return fileBytes;
  }
}
