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
 * |           Reserved            |     Flags     |     Max-LSP   |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        Min-Bandwidth                          |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceLoadBalancingObject implements PceObjectFrame {

  private final String NAME = "Load Balancing";

  private String reserved;
  private String maxLSP;
  private String minBandwidth;
  private String flags;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;

  private int reservedStartBit = PceConstantValues.LOAD_BALANCING_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.LOAD_BALANCING_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.LOAD_BALANCING_OBJECT_RESERVED_LENGTH;

  private int maxLSPStartBit = PceConstantValues.LOAD_BALANCING_OBJECT_MAX_LSP_START_BIT;
  private int maxLSPEndBit = PceConstantValues.LOAD_BALANCING_OBJECT_MAX_LSP_END_BIT;
  private int maxLSPLength = PceConstantValues.LOAD_BALANCING_OBJECT_MAX_LSP_LENGTH;

  private int minBandwidthStartBit =
      PceConstantValues.LOAD_BALANCING_OBJECT_MIN_BANDWIDTH_START_BIT;
  private int minBandwidthEndBit = PceConstantValues.LOAD_BALANCING_OBJECT_MIN_BANDWIDTH_END_BIT;
  private int minBandwidthLength = PceConstantValues.LOAD_BALANCING_OBJECT_MIN_BANDWIDTH_LENGTH;

  private int flagsStartBit = PceConstantValues.LOAD_BALANCING_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.LOAD_BALANCING_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.LOAD_BALANCING_OBJECT_FLAGS_LENGTH;

  public PceLoadBalancingObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceLoadBalancingObject(
      PceCommonObjectHeader objectHeader, int maxLSP, String minBandwidth) {
    this.setObjectHeader(objectHeader);
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
    this.setMaxLSPDecimalValue(maxLSP);
    this.setMinBandwidthBinaryString(minBandwidth);
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
    String binaryString = reserved + flags + maxLSP + minBandwidth;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String maxLSPBinaryString = binaryString.substring(maxLSPStartBit, maxLSPEndBit + 1);
    String minBandwidthBinaryString =
        binaryString.substring(minBandwidthStartBit, minBandwidthEndBit + 1);

    this.setReservedBinaryString(reservedBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setMaxLSPBinaryString(maxLSPBinaryString);
    this.setMinBandwidthBinaryString(minBandwidthBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength = reserved.length() + flags.length() + maxLSP.length() + minBandwidth.length();
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

  /** maxLSP */
  public int getMaxLSPDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(maxLSP);
    return decimalValue;
  }

  public String getMaxLSPBinaryString() {
    return this.maxLSP;
  }

  public void setMaxLSPDecimalValue(int decimalValue) {
    int binaryLength = maxLSPLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.maxLSP = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setMaxLSPBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, maxLSPLength);
    this.maxLSP = checkedBinaryString;
  }

  public void setMaxLSPBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(maxLSP, startingBit, binaryString, maxLSPLength);
    this.maxLSP = checkedBinaryString;
  }

  /** minBandwidth */
  // public int getMinBandwidthDecimalValue() {
  // int decimalValue = (int) getDecimalValue(minBandwidth);
  // return decimalValue;
  // }
  public String getMinBandwidthBinaryString() {
    return this.minBandwidth;
  }

  // public void setMinBandwidthDecimalValue(int decimalValue) {
  // int binaryLength = minBandwidthLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.minBandwidth = this.setDecimalValue(decimalValue, maxValue,
  // binaryLength);
  // }
  public void setMinBandwidthBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, minBandwidthLength);
    this.minBandwidth = checkedBinaryString;
  }

  public void setMinBandwidthBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            minBandwidth, startingBit, binaryString, minBandwidthLength);
    this.minBandwidth = checkedBinaryString;
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
    String maxLSPInfo = ",MaxLSP=" + this.getMaxLSPDecimalValue();
    String minBandwidthInfo = ",MinBandwidth=" + this.getMinBandwidthBinaryString();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo =
        "<Load-Balancing:" + reservedInfo + flagsInfo + maxLSPInfo + minBandwidthInfo + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String reservedBinaryInfo = getReservedBinaryString();
    String flagsInfo = "'" + this.getFlagsBinaryString();
    String maxLSPBinaryInfo = "'" + getMaxLSPBinaryString();
    String minBandwidthBinaryInfo = "'" + getMinBandwidthBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo =
        "[" + reservedBinaryInfo + flagsInfo + maxLSPBinaryInfo + minBandwidthBinaryInfo + "]";

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
