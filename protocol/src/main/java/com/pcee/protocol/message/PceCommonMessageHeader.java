/**
 * This file is part of Path Computation Element Emulator (PCEE).
 *
 * <p>PCEE is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>PCEE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with PCEE. If not, see
 * http://www.gnu.org/licenses/.
 */
package com.pcee.protocol.message;

/**
 *
 *
 * <pre>
 * <b>Header Layout:</b>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Ver |  Flags  |  Message-Type |       Message-Length          |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceCommonMessageHeader {

  private final String NAME = "Message Header";

  private String version;
  private String type;
  private String length;
  private String flags;

  private final int versionStartBit = PceConstantValues.COMMON_MESSAGE_HEADER_VERSION_START_BIT;
  private int versionEndBit = PceConstantValues.COMMON_MESSAGE_HEADER_VERSION_END_BIT;
  private int versionLength = PceConstantValues.COMMON_MESSAGE_HEADER_VERSION_LENGTH;

  private int flagsStartBit = PceConstantValues.COMMON_MESSAGE_HEADER_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.COMMON_MESSAGE_HEADER_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.COMMON_MESSAGE_HEADER_FLAGS_LENGTH;

  private int typeStartBit = PceConstantValues.COMMON_MESSAGE_HEADER_TYPE_START_BIT;
  private int typeEndBit = PceConstantValues.COMMON_MESSAGE_HEADER_TYPE_END_BIT;
  private int typeLength = PceConstantValues.COMMON_MESSAGE_HEADER_TYPE_LENGTH;

  private int lengthStartBit = PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH_START_BIT;
  private int lengthEndBit = PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH_END_BIT;
  private int lengthLength = PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH_LENGTH;

  public PceCommonMessageHeader(String binaryString) {
    this.setHeaderBinaryString(binaryString);
  }

  public PceCommonMessageHeader(int version, int type) {
    this.setVersionDecimalValue(version);
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setTypeDecimalValue(type);
    this.setLengthDecimalValue(0);
  }

  /** Header */
  public String getHeaderBinaryString() {
    return version + flags + type + length;
  }

  public void setHeaderBinaryString(String rawBinaryString) {
    String binaryString =
        PceComputationFactory.setBinaryString(
            rawBinaryString, PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH);
    String versionBinaryString = binaryString.substring(versionStartBit, versionEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
    String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);

    this.setVersionBinaryString(versionBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setTypeBinaryString(typeBinaryString);
    this.setLengthBinaryString(lengthBinaryString);
  }

  /** version */
  public int getVersionDecimalValue() {
    return (int) PceComputationFactory.getDecimalValue(version);
  }

  public String getVersionBinaryString() {
    return this.version;
  }

  public void setVersionDecimalValue(int decimalValue) {
    int binaryLength = versionLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.version = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setVersionBinaryString(String binaryString) {
    this.version = PceComputationFactory.setBinaryString(binaryString, versionLength);
  }

  public void setVersionBinaryString(int startingBit, String binaryString) {
    this.version =
        PceComputationFactory.setBinaryString(version, startingBit, binaryString, versionLength);
  }

  /** type */
  public int getTypeDecimalValue() {
    return (int) PceComputationFactory.getDecimalValue(type);
  }

  public String getTypeBinaryString() {
    return this.type;
  }

  public void setTypeDecimalValue(int decimalValue) {
    int binaryLength = typeLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.type = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setTypeBinaryString(String binaryString) {
    this.type = PceComputationFactory.setBinaryString(binaryString, typeLength);
  }

  public void setTypeBinaryString(int startingBit, String binaryString) {
    this.type = PceComputationFactory.setBinaryString(type, startingBit, binaryString, typeLength);
  }

  /** length */
  public int getLengthDecimalValue() {
    return (int) PceComputationFactory.getDecimalValue(length);
  }

  public String getLengthBinaryString() {
    return this.length;
  }

  public void setLengthDecimalValue(int decimalValue) {
    int binaryLength = lengthLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.length = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setLengthBinaryString(String binaryString) {
    this.length = PceComputationFactory.setBinaryString(binaryString, lengthLength);
  }

  public void setLengthBinaryString(int startingBit, String binaryString) {
    this.length =
        PceComputationFactory.setBinaryString(length, startingBit, binaryString, lengthLength);
  }

  /** flags */
  public int getFlagsDecimalValue() {
    return (int) PceComputationFactory.getDecimalValue(flags);
  }

  public String getFlagsBinaryString() {
    return this.flags;
  }

  public void setFlagsDecimalValue(int decimalValue) {
    int binaryLength = flagsLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.flags = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setFlagsBinaryString(String binaryString) {
    this.flags = PceComputationFactory.setBinaryString(binaryString, flagsLength);
  }

  public void setFlagsBinaryString(int startingBit, String binaryString) {
    this.flags =
        PceComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
  }

  @Override
  public String toString() {
    // Using String.format for better readability and consistency.
    // All fields are now represented by their decimal values.
    return String.format(
        "<%s:Version=%d,Type=%d,Length=%d,Flags=%d>",
        NAME,
        this.getVersionDecimalValue(),
        this.getTypeDecimalValue(),
        this.getLengthDecimalValue(),
        this.getFlagsDecimalValue());
  }

  public String binaryInformation() {
    // Using String.format to maintain the existing binary string representation
    // with single quote separators.
    return String.format(
        "[%s'%s'%s'%s]",
        getVersionBinaryString(),
        getTypeBinaryString(),
        getLengthBinaryString(),
        getFlagsBinaryString());
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
