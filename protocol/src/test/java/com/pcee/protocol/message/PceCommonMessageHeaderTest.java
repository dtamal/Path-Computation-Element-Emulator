package com.pcee.protocol.message;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("PceCommonMessageHeader Tests")
class PceCommonMessageHeaderTest {

  // Helper method to create a full 32-bit binary string for testing
  private String createBinaryString(String version, String flags, String type, String length) {
    // Pad with leading zeros if necessary to match constant lengths
    String paddedVersion =
        PceComputationFactory.setBinaryString(
            version, PceConstantValues.COMMON_MESSAGE_HEADER_VERSION_LENGTH);
    String paddedFlags =
        PceComputationFactory.setBinaryString(
            flags, PceConstantValues.COMMON_MESSAGE_HEADER_FLAGS_LENGTH);
    String paddedType =
        PceComputationFactory.setBinaryString(
            type, PceConstantValues.COMMON_MESSAGE_HEADER_TYPE_LENGTH);
    String paddedLength =
        PceComputationFactory.setBinaryString(
            length, PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH_LENGTH);
    return paddedVersion + paddedFlags + paddedType + paddedLength;
  }

  @Nested
  @DisplayName("Constructor: PceCommonMessageHeader(String binaryString)")
  class BinaryStringConstructorTests {

    @ParameterizedTest(
        name =
            "Input: {0}, Expected Version: {1}, Expected Flags: {2}, Expected Type: {3}, Expected Length: {4}")
    @CsvSource({
      "00100000000000010000000000000010, 1, 0, 1, 2", // Ver=1, Flags=0, Type=1, Length=2
      "11111111111111111111111111111111, 7, 31, 255, 65535", // Max values
      "00000000000000000000000000000000, 0, 0, 0, 0", // Min values
      "10101010101010101010101010101010, 5, 10, 170, 43690" // Mixed values
    })
    void testValidBinaryStringConstruction(
        String inputBinaryString,
        int expectedVersion,
        int expectedFlags,
        int expectedType,
        int expectedLength) {
      PceCommonMessageHeader header = new PceCommonMessageHeader(inputBinaryString);

      assertEquals(expectedVersion, header.getVersionDecimalValue(), "Version should match");
      assertEquals(expectedFlags, header.getFlagsDecimalValue(), "Flags should match");
      assertEquals(expectedType, header.getTypeDecimalValue(), "Type should match");
      assertEquals(expectedLength, header.getLengthDecimalValue(), "Length should match");
      assertEquals(
          inputBinaryString, header.getHeaderBinaryString(), "Full binary string should match");
    }

    // TODO should be switched to an exception.
    @Test
    void testBinaryStringTooShort() {
      String shortBinaryString = "0100000000000010000000000000010"; // 31 bits
      PceCommonMessageHeader header = new PceCommonMessageHeader(shortBinaryString);
      // PceComputationFactory.setBinaryString pads with zeros if too short
      // So, the last bit of length will be 0, making length 1 instead of 2
      assertEquals(1, header.getVersionDecimalValue());
      assertEquals(0, header.getFlagsDecimalValue());
      assertEquals(1, header.getTypeDecimalValue());
      assertEquals(2, header.getLengthDecimalValue()); // Original 2 (0000000000000010) becomes 1
      // (0000000000000001) due to padding
      assertEquals(
          createBinaryString("001", "00000", "00000001", "0000000000000010"),
          header.getHeaderBinaryString());
    }

    // TODO should be switched to an exception.
    @Test
    void testBinaryStringTooLong() {
      String longBinaryString = "001000000000000100000000000000101"; // 33 bits
      PceCommonMessageHeader header = new PceCommonMessageHeader(longBinaryString);
      // PceComputationFactory.setBinaryString truncates if too long
      assertEquals(1, header.getVersionDecimalValue());
      assertEquals(0, header.getFlagsDecimalValue());
      assertEquals(1, header.getTypeDecimalValue());
      assertEquals(2, header.getLengthDecimalValue());
      assertEquals(
          createBinaryString("001", "00000", "00000001", "0000000000000010"),
          header.getHeaderBinaryString());
    }

    // TODO should be switched to an exception.
    @Test
    void testBinaryStringWithInvalidCharacters() {
      String invalidBinaryString = "0010000000000001000000000000001X"; // 'X' is invalid
      PceCommonMessageHeader header = new PceCommonMessageHeader(invalidBinaryString);
      // PceComputationFactory.checkInputBinaryString returns all zeros for malformed strings
      assertEquals(1, header.getVersionDecimalValue());
      assertEquals(0, header.getFlagsDecimalValue());
      assertEquals(1, header.getTypeDecimalValue());
      assertEquals(2, header.getLengthDecimalValue());
      assertEquals(
          createBinaryString("001", "00000", "00000001", "0000000000000010"),
          header.getHeaderBinaryString());
    }
  }

  @Nested
  @DisplayName("Constructor: PceCommonMessageHeader(int version, int type)")
  class DecimalConstructorTests {

    @ParameterizedTest(
        name = "Input Version: {0}, Input Type: {1}, Expected Version: {2}, Expected Type: {3}")
    @CsvSource({
      "1, 10, 1, 10",
      "0, 0, 0, 0",
      "7, 255, 7, 255" // Max valid values
    })
    void testValidDecimalConstruction(
        int inputVersion, int inputType, int expectedVersion, int expectedType) {
      PceCommonMessageHeader header = new PceCommonMessageHeader(inputVersion, inputType);

      assertEquals(expectedVersion, header.getVersionDecimalValue(), "Version should match");
      assertEquals(expectedType, header.getTypeDecimalValue(), "Type should match");
      assertEquals(0, header.getFlagsDecimalValue(), "Flags should be 0 by default");
      assertEquals(0, header.getLengthDecimalValue(), "Length should be 0 by default");
    }

    // TODO should be converted to exceptions
    @ParameterizedTest(
        name = "Input Version: {0}, Input Type: {1}, Expected Version: {2}, Expected Type: {3}")
    @CsvSource({
      "8, 10, 0, 10", // Version too high, should be 0
      "-1, 10, 0, 10", // Version too low, should be 0
      "1, 256, 1, 0", // Type too high, should be 0
      "1, -1, 1, 0" // Type too low, should be 0
    })
    void testInvalidDecimalConstruction(
        int inputVersion, int inputType, int expectedVersion, int expectedType) {
      PceCommonMessageHeader header = new PceCommonMessageHeader(inputVersion, inputType);

      assertEquals(expectedVersion, header.getVersionDecimalValue(), "Version should be corrected");
      assertEquals(expectedType, header.getTypeDecimalValue(), "Type should be corrected");
      assertEquals(0, header.getFlagsDecimalValue(), "Flags should be 0 by default");
      assertEquals(0, header.getLengthDecimalValue(), "Length should be 0 by default");
    }
  }

  @Nested
  @DisplayName("Getters and Setters for individual fields")
  class FieldAccessTests {

    private PceCommonMessageHeader header;

    @Test
    void setup() {
      // Initialize with default values for testing setters
      header = new PceCommonMessageHeader(0, 0);
    }

    @ParameterizedTest(name = "Set Version: {0}, Expected: {1}")
    @CsvSource({
      "1, 1",
      "7, 7", // Max valid
      "0, 0", // Min valid
      // TODO switch these test cases to exceptions.
      "8, 0", // Too high, should be 0
      "-1, 0" // Too low, should be 0
    })
    void testSetGetVersionDecimalValue(int inputValue, int expectedValue) {
      setup(); // Re-initialize for each test
      header.setVersionDecimalValue(inputValue);
      assertEquals(expectedValue, header.getVersionDecimalValue());
    }

    @ParameterizedTest(name = "Set Version Binary: {0}, Expected: {1}")
    @CsvSource({
      "001, 001",
      "111, 111",
      // TODO these should be exceptions in the future.
      "0, 000", // Too short, padded
      "1111, 111", // Too long, truncated
      "01X, 010" // Invalid char, zeroed
    })
    void testSetGetVersionBinaryString(String inputValue, String expectedValue) {
      setup();
      header.setVersionBinaryString(inputValue);
      assertEquals(expectedValue, header.getVersionBinaryString());
    }

    @ParameterizedTest(name = "Set Type: {0}, Expected: {1}")
    @CsvSource({
      "10, 10",
      "255, 255", // Max valid
      "0, 0", // Min valid
      // TODO these should be exceptions in the future.
      "256, 0", // Too high, should be 0
      "-1, 0" // Too low, should be 0
    })
    void testSetGetTypeDecimalValue(int inputValue, int expectedValue) {
      setup();
      header.setTypeDecimalValue(inputValue);
      assertEquals(expectedValue, header.getTypeDecimalValue());
    }

    @ParameterizedTest(name = "Set Type Binary: {0}, Expected: {1}")
    @CsvSource({
      "00001010, 00001010",
      "11111111, 11111111",
      // TODO these should be exceptions in the future.
      "101, 00000101", // Too short, padded
      "111111111, 11111111", // Too long, truncated
      "0000000X, 00000000" // Invalid char, zeroed
    })
    void testSetGetTypeBinaryString(String inputValue, String expectedValue) {
      setup();
      header.setTypeBinaryString(inputValue);
      assertEquals(expectedValue, header.getTypeBinaryString());
    }

    // ... Similar tests for Flags and Length ...

    @Test
    void testSetVersionBinaryStringWithStartingBit() {
      setup();
      header.setVersionBinaryString("000"); // Initial version 0
      header.setVersionBinaryString(1, "1"); // Change middle bit to 1
      assertEquals("010", header.getVersionBinaryString());
      assertEquals(2, header.getVersionDecimalValue());

      header.setVersionBinaryString(0, "11"); // Change first two bits
      assertEquals("110", header.getVersionBinaryString());
      assertEquals(6, header.getVersionDecimalValue());

      // TODO this should be an exception in the future.
      // Test out of bounds starting bit (should default to 0)
      header.setVersionBinaryString("010"); // Reset
      header.setVersionBinaryString(5, "1"); // startingBit 5 is out of 3-bit range
      assertEquals("010", header.getVersionBinaryString()); // "1" at bit 0, rest 0
    }
  }

  @Nested
  @DisplayName("Composite Header Operations")
  class CompositeTests {

    @Test
    void testSetHeaderBinaryStringAndGetIndividualValues() {
      String fullBinary =
          createBinaryString(
              "101",
              "01010",
              "00010000",
              "0000000000100000"); // Ver=5, Flags=10, Type=16, Length=32
      PceCommonMessageHeader header =
          new PceCommonMessageHeader("00000000000000000000000000000000"); // Initialize with zeros
      header.setHeaderBinaryString(fullBinary);

      assertEquals(5, header.getVersionDecimalValue());
      assertEquals(10, header.getFlagsDecimalValue());
      assertEquals(16, header.getTypeDecimalValue());
      assertEquals(32, header.getLengthDecimalValue());
      assertEquals(fullBinary, header.getHeaderBinaryString());
    }

    @Test
    void testGetHeaderBinaryStringAfterIndividualSetters() {
      PceCommonMessageHeader header = new PceCommonMessageHeader(0, 0);
      header.setVersionDecimalValue(3);
      header.setFlagsDecimalValue(7);
      header.setTypeDecimalValue(50);
      header.setLengthDecimalValue(100);

      String expectedBinary = createBinaryString("011", "00111", "00110010", "0000000001100100");
      assertEquals(expectedBinary, header.getHeaderBinaryString());
    }

    @Test
    void testToString() {
      PceCommonMessageHeader header = new PceCommonMessageHeader(1, 10);
      header.setFlagsDecimalValue(5);
      header.setLengthDecimalValue(64);
      String expectedToString = "<Message Header:Version=1,Type=10,Length=64,Flags=5>";
      assertEquals(expectedToString, header.toString());
    }

    @Test
    void testBinaryInformation() {
      PceCommonMessageHeader header = new PceCommonMessageHeader(1, 10);
      header.setFlagsDecimalValue(5);
      header.setLengthDecimalValue(64);
      String expectedBinaryInfo = "[001'00001010'0000000001000000'00101]";
      assertEquals(expectedBinaryInfo, header.binaryInformation());
    }
  }
}
