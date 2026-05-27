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
import java.util.LinkedList;

/**
 *
 *
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Reserved    |                   Flags                 |S|N|L|
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                     Request-ID-number #1                      |
 * //                                                             //
 * |                     Request-ID-number #M                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceSynchronizationVectorObject implements PceObjectFrame {

  private final String NAME = "Synchronization Vector";

  private String reserved;
  private String flags;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;
  private LinkedList<String> requestIDNumbers;

  private int reservedStartBit = PceConstantValues.SVEC_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.SVEC_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.SVEC_OBJECT_RESERVED_LENGTH;

  private int flagsStartBit = PceConstantValues.SVEC_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.SVEC_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.SVEC_OBJECT_FLAGS_LENGTH;

  private int sFlagStartBit = PceConstantValues.SVEC_OBJECT_FLAG_S_START_BIT;
  private int sFlagEndBit = PceConstantValues.SVEC_OBJECT_FLAG_S_END_BIT;
  private int sFlagLength = PceConstantValues.SVEC_OBJECT_FLAG_S_LENGTH;

  private int nFlagStartBit = PceConstantValues.SVEC_OBJECT_FLAG_N_START_BIT;
  private int nFlagEndBit = PceConstantValues.SVEC_OBJECT_FLAG_N_END_BIT;
  private int nFlagLength = PceConstantValues.SVEC_OBJECT_FLAG_N_LENGTH;

  private int lFlagStartBit = PceConstantValues.SVEC_OBJECT_FLAG_L_START_BIT;
  private int lFlagEndBit = PceConstantValues.SVEC_OBJECT_FLAG_L_END_BIT;
  private int lFlagLength = PceConstantValues.SVEC_OBJECT_FLAG_L_LENGTH;

  /** Testconstructor, not implemented for the requestIDNumbers */
  public PceSynchronizationVectorObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceSynchronizationVectorObject(
      PceCommonObjectHeader objectHeader,
      String sFlag,
      String nFlag,
      String lFlag,
      LinkedList<String> requestIDNumbers) {
    this.setObjectHeader(objectHeader);
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setSFlagBinaryString(sFlag);
    this.setNFlagBinaryString(nFlag);
    this.setLFlagBinaryString(lFlag);
    this.requestIDNumbers = requestIDNumbers;
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
    String binaryString = reserved + flags;

    StringBuffer requestIDNumbersStringBuffer = new StringBuffer();

    for (int i = 0; i < requestIDNumbers.size(); i++) {
      String reuqestIDNumberString = requestIDNumbers.get(i);
      requestIDNumbersStringBuffer.append(reuqestIDNumberString);
    }

    return binaryString + requestIDNumbersStringBuffer.toString();
  }

  public void setObjectBinaryString(String binaryString) {
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String sFlagBinaryString = binaryString.substring(sFlagStartBit, sFlagEndBit + 1);
    String nFlagBinaryString = binaryString.substring(nFlagStartBit, nFlagEndBit + 1);
    String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);

    this.setReservedBinaryString(reservedBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setSFlagBinaryString(sFlagBinaryString);
    this.setNFlagBinaryString(nFlagBinaryString);
    this.setLFlagBinaryString(lFlagBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength = reserved.length() + flags.length();
    int requestIDNumbersLength = getRequestIDNumbersLength();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + requestIDNumbersLength + headerLength) / 8;
    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  private int getRequestIDNumbersLength() {
    int length = 0;
    for (int i = 0; i < requestIDNumbers.size(); i++) {
      length += requestIDNumbers.get(i).length();
    }
    return length;
  }

  /** reserved */
  // public int getReservedDecimalValue() {
  // int decimalValue = (int) getDecimalValue(reserved);
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
  // this.reserved = this.setDecimalValue(decimalValue, maxValue,
  // binaryLength);
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

  /** flags */
  // public int getFlagsDecimalValue() {
  // int decimalValue = (int) getDecimalValue(flags);
  // return decimalValue;
  // }
  public String getFlagsBinaryString() {
    return this.flags;
  }

  // public void setFlagsDecimalValue(int decimalValue) {
  // int binaryLength = flagsLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.flags = this.setDecimalValue(decimalValue, maxValue, binaryLength);
  // }
  public void setFlagsBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public void setFlagsBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** sFlag */
  public int getSFlagDecimalValue() {
    int relativeStartBit = (sFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + sFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getSFlagBinaryString() {
    String binaryString = flags.substring(0, (sFlagStartBit - flagsStartBit) + sFlagLength);
    return binaryString;
  }

  public void setSFlagBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            this.flags, (sFlagStartBit - flagsStartBit), binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** nFlag */
  public int getNFlagDecimalValue() {
    int relativeStartBit = (nFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + nFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getNFlagBinaryString() {
    int relativeStartBit = (nFlagStartBit - flagsStartBit);
    String binaryString = flags.substring(relativeStartBit, relativeStartBit + nFlagLength);
    return binaryString;
  }

  public void setNFlagBinaryString(String binaryString) {
    int relativeStartBit = (nFlagStartBit - flagsStartBit);
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** lFlag */
  public int getLFlagDecimalValue() {
    int relativeStartBit = (lFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + lFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getLFlagBinaryString() {
    int relativeStartBit = (lFlagStartBit - flagsStartBit);
    String binaryString = flags.substring(relativeStartBit, relativeStartBit + lFlagLength);
    return binaryString;
  }

  public void setLFlagBinaryString(String binaryString) {
    int relativeStartBit = (lFlagStartBit - flagsStartBit);
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public String toString() {
    String reservedInfo = "Reserved=" + this.getReservedBinaryString();
    String flagsInfo = ",Flags=" + this.getFlagsBinaryString();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = "<SVEC:" + reservedInfo + flagsInfo;

    StringBuffer requestIDNumbersStringBuffer = new StringBuffer();

    for (int i = 0; i < requestIDNumbers.size(); i++) {
      String reuqestIDNumberString = requestIDNumbers.get(i);
      requestIDNumbersStringBuffer.append(",RequestID=" + reuqestIDNumberString);
    }
    requestIDNumbersStringBuffer.append(">");

    return headerInfo + objectInfo + requestIDNumbersStringBuffer.toString();
  }

  public String binaryInformation() {
    String reservedBinaryInfo = getReservedBinaryString();
    String flagsInfo = "'" + this.getFlagsBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo = "[" + reservedBinaryInfo + flagsInfo;

    StringBuffer requestIDNumbersStringBuffer = new StringBuffer();

    for (int i = 0; i < requestIDNumbers.size(); i++) {
      String reuqestIDNumberString = requestIDNumbers.get(i);
      requestIDNumbersStringBuffer.append("'" + reuqestIDNumberString);
    }
    requestIDNumbersStringBuffer.append("]");

    return headerInfo + objectInfo + requestIDNumbersStringBuffer.toString();
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
