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
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Nature of Issue|C|          Flags              |   Reserved    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                      Optional TLVs                          //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceNoPathObject implements PceObjectFrame {

  private final String NAME = "No-Path";

  private String natureOfIssue;
  private String reserved;
  private String flags;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;

  private int natureOfIssueStartBit = PceConstantValues.NO_PATH_OBJECT_NATURE_OF_ISSUE_START_BIT;
  private int natureOfIssueEndBit = PceConstantValues.NO_PATH_OBJECT_NATURE_OF_ISSUE_END_BIT;
  private int natureOfIssueLength = PceConstantValues.NO_PATH_OBJECT_NATURE_OF_ISSUE_LENGTH;

  private int flagsStartBit = PceConstantValues.NO_PATH_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.NO_PATH_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.NO_PATH_OBJECT_FLAGS_LENGTH;

  private int constraintsFlagStartBit =
      PceConstantValues.NO_PATH_OBJECT_FLAG_FLAG_CONSTRAINTS_START_BIT;
  private int constraintsFlagEndBit =
      PceConstantValues.NO_PATH_OBJECT_FLAG_FLAG_CONSTRAINTS_END_BIT;
  private int constraintsFlagLength = PceConstantValues.NO_PATH_OBJECT_FLAG_FLAG_CONSTRAINTS_LENGTH;

  private int reservedStartBit = PceConstantValues.NO_PATH_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.NO_PATH_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.NO_PATH_OBJECT_RESERVED_LENGTH;

  public PceNoPathObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceNoPathObject(
      PceCommonObjectHeader objectHeader, int natureOfIssue, String constraintsFlag) {
    this.setObjectHeader(objectHeader);
    this.setNatureOfIssueDecimalValue(natureOfIssue);
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setConstraintsFlagBinaryString(constraintsFlag);
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
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
    String binaryString = natureOfIssue + flags + reserved;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String natureOfIssueBinaryString =
        binaryString.substring(natureOfIssueStartBit, natureOfIssueEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String constraintsFlagBinaryString =
        binaryString.substring(constraintsFlagStartBit, constraintsFlagEndBit + 1);
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);

    this.setNatureOfIssueBinaryString(natureOfIssueBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setConstraintsFlagBinaryString(constraintsFlagBinaryString);
    this.setReservedBinaryString(reservedBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength = natureOfIssue.length() + flags.length() + reserved.length();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + headerLength) / 8;
    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  /** natureOfIssue */
  public int getNatureOfIssueDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(natureOfIssue);
    return decimalValue;
  }

  public String getNatureOfIssueBinaryString() {
    return this.natureOfIssue;
  }

  public void setNatureOfIssueDecimalValue(int decimalValue) {
    int binaryLength = natureOfIssueLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.natureOfIssue =
        PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setNatureOfIssueBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, natureOfIssueLength);
    this.natureOfIssue = checkedBinaryString;
  }

  public void setNatureOfIssueBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            natureOfIssue, startingBit, binaryString, natureOfIssueLength);
    this.natureOfIssue = checkedBinaryString;
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

  /** constraintsFlag */
  public int getConstraintsFlagDecimalValue() {
    int relativeStartBit = (constraintsFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + constraintsFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getConstraintsFlagBinaryString() {
    String binaryString =
        flags.substring(0, (constraintsFlagStartBit - flagsStartBit) + constraintsFlagLength);
    return binaryString;
  }

  public void setConstraintsFlagBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            this.flags, (constraintsFlagStartBit - flagsStartBit), binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public String toString() {
    String natureOfIssueInfo = "NatureOfIssue=" + this.getNatureOfIssueDecimalValue();
    String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
    String reservedInfo = ",Reserved=" + this.getReservedBinaryString();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = "<No-Path:" + natureOfIssueInfo + flagsInfo + reservedInfo + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String natureOfIssueBinaryInfo = getNatureOfIssueBinaryString();
    String flagsInfo = "'" + this.getFlagsBinaryString();
    String reservedBinaryInfo = "'" + getReservedBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo = "[" + natureOfIssueBinaryInfo + flagsInfo + reservedBinaryInfo + "]";

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
