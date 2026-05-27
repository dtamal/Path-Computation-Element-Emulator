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
package com.pcee.protocol.message.objectframe.impl;

import com.pcee.protocol.message.PceComputationFactory;
import com.pcee.protocol.message.PceConstantValues;
import com.pcee.protocol.message.objectframe.PceCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PceObjectFrame;

/**
 *
 *
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Reserved    |      Flags    |   Error-Type  |  Error-value  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                     Optional TLVs                           //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceErrorObject implements PceObjectFrame {

  private final String NAME = "Error";

  private String reserved;
  private String type;
  private String value;
  private String flags;

  private PceCommonObjectHeader objectHeader;

  private int reservedStartBit = PceConstantValues.ERROR_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.ERROR_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.ERROR_OBJECT_RESERVED_LENGTH;

  private int flagsStartBit = PceConstantValues.ERROR_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.ERROR_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.ERROR_OBJECT_FLAGS_LENGTH;

  private int typeStartBit = PceConstantValues.ERROR_OBJECT_TYPE_START_BIT;
  private int typeEndBit = PceConstantValues.ERROR_OBJECT_TYPE_END_BIT;
  private int typeLength = PceConstantValues.ERROR_OBJECT_TYPE_LENGTH;

  private int valueStartBit = PceConstantValues.ERROR_OBJECT_VALUE_START_BIT;
  private int valueEndBit = PceConstantValues.ERROR_OBJECT_VALUE_END_BIT;
  private int valueLength = PceConstantValues.ERROR_OBJECT_VALUE_LENGTH;

  public PceErrorObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceErrorObject(PceCommonObjectHeader objectHeader, int type, int value) {
    this.setObjectHeader(objectHeader);
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setTypeDecimalValue(type);
    this.setValueDecimalValue(value);
    this.updateHeaderLength();
  }

  private void updateHeaderLength() {
    int objectFrameByteLength = this.getObjectFrameByteLength();
    this.getObjectHeader().setLengthDecimalValue(objectFrameByteLength);
  }

  /** Object */
  public PceCommonObjectHeader getObjectHeader() {
    return objectHeader;
  }

  public void setObjectHeader(PceCommonObjectHeader objectHeader) {
    this.objectHeader = objectHeader;
  }

  public String getObjectBinaryString() {
    String binaryString = reserved + flags + type + value;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
    String valueBinaryString = binaryString.substring(valueStartBit, valueEndBit + 1);

    this.setReservedBinaryString(reservedBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setTypeBinaryString(typeBinaryString);
    this.setValueBinaryString(valueBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength = reserved.length() + flags.length() + type.length() + value.length();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + headerLength) / 8;
    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  /** reserved */
  public int getReservedDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(reserved);
    return decimalValue;
  }

  public String getReservedBinaryString() {
    return this.reserved;
  }

  public void setReservedDecimalValue(int decimalValue) {
    int binaryLength = reservedLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.reserved = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setReservedBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, reservedLength);
    this.reserved = checkedBinaryString;
  }

  public void setReservedBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(reserved, startingBit, binaryString, reservedLength);
    this.reserved = checkedBinaryString;
  }

  /** type */
  public int getTypeDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(type);
    return decimalValue;
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
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, typeLength);
    this.type = checkedBinaryString;
  }

  public void setTypeBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(type, startingBit, binaryString, typeLength);
    this.type = checkedBinaryString;
  }

  /** value */
  public int getValueDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(value);
    return decimalValue;
  }

  public String getValueBinaryString() {
    return this.value;
  }

  public void setValueDecimalValue(int decimalValue) {
    int binaryLength = valueLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.value = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setValueBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, valueLength);
    this.value = checkedBinaryString;
  }

  public void setValueBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(value, startingBit, binaryString, valueLength);
    this.value = checkedBinaryString;
  }

  /** flags */
  public int getFlagsDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flags);
    return decimalValue;
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
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public void setFlagsBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public String toString() {
    String reservedInfo = "Reserved=" + this.getReservedDecimalValue();
    String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
    String typeInfo = ",Type=" + this.getTypeDecimalValue();
    String valueInfo = ",Value=" + this.getValueDecimalValue();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = NAME + ":" + reservedInfo + flagsInfo + typeInfo + valueInfo + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String reservedBinaryInfo = getReservedBinaryString();
    String flagsInfo = "'" + this.getFlagsBinaryString();
    String typeBinaryInfo = "'" + getTypeBinaryString();
    String valueBinaryInfo = "'" + getValueBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo =
        "[" + reservedBinaryInfo + flagsInfo + typeBinaryInfo + valueBinaryInfo + "]";

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
