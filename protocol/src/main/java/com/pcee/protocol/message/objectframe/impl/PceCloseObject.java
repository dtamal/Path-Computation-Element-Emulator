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
 * |          Reserved             |      Flags    |    Reason     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                         Optional TLVs                       //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceCloseObject implements PceObjectFrame {

  private final String NAME = "Close";

  private String reserved;
  private String flags;
  private String reason;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;

  private int reservedStartBit = PceConstantValues.CLOSE_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.CLOSE_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.CLOSE_OBJECT_RESERVED_LENGTH;

  private int flagsStartBit = PceConstantValues.CLOSE_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.CLOSE_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.CLOSE_OBJECT_FLAGS_LENGTH;

  private int reasonStartBit = PceConstantValues.CLOSE_OBJECT_REASON_START_BIT;
  private int reasonEndBit = PceConstantValues.CLOSE_OBJECT_REASON_END_BIT;
  private int reasonLength = PceConstantValues.CLOSE_OBJECT_REASON_LENGTH;

  public PceCloseObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceCloseObject(PceCommonObjectHeader objectHeader, int reason) {
    this.setObjectHeader(objectHeader);
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setReasonDecimalValue(reason);
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
    String binaryString = reserved + flags + reason;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String reasonBinaryString = binaryString.substring(reasonStartBit, reasonEndBit + 1);

    this.setReservedBinaryString(reservedBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setReasonBinaryString(reasonBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength = reserved.length() + flags.length() + reason.length();
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
  // public int getReservedDecimalValue() {
  // int decimalValue = (int)
  // PceComputationFactory.getDecimalValue(reserved);
  // return decimalValue;
  // }
  public String getReservedBinaryString() {
    return this.reserved;
  }

  // public void setReservedDecimalValue(int decimalValue) {
  // int binaryLength = reservedLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.reserved = PceComputationFactory.setDecimalValue(decimalValue,
  // maxValue, binaryLength);
  // }
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

  /** reason */
  public int getReasonDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(reason);
    return decimalValue;
  }

  public String getReasonBinaryString() {
    return this.reason;
  }

  public void setReasonDecimalValue(int decimalValue) {
    int binaryLength = reasonLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.reason = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setReasonBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, reasonLength);
    this.reason = checkedBinaryString;
  }

  public void setReasonBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(reason, startingBit, binaryString, reasonLength);
    this.reason = checkedBinaryString;
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
    String reservedInfo = "Reserved=" + this.getReservedBinaryString();
    String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
    String reasonInfo = ",Reason=" + this.getReasonDecimalValue();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = NAME + ":" + reservedInfo + flagsInfo + reasonInfo + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String reservedBinaryInfo = getReservedBinaryString();
    String flagsInfo = "'" + this.getFlagsBinaryString();
    String reasonBinaryInfo = "'" + getReasonBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo = "[" + reservedBinaryInfo + flagsInfo + reasonBinaryInfo + "]";

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
