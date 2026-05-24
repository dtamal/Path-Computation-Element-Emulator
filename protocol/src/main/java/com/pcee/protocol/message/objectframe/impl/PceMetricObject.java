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
 * |          Reserved             |    Flags  |C|B|      Type     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          metric-value                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceMetricObject implements PceObjectFrame {

  private final String NAME = "Metric";

  private String reserved;
  private String type;
  private String metricValue;
  private String flags;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;

  private int reservedStartBit = PceConstantValues.METRIC_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.METRIC_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.METRIC_OBJECT_RESERVED_LENGTH;

  private int flagsStartBit = PceConstantValues.METRIC_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.METRIC_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.METRIC_OBJECT_FLAGS_LENGTH;

  private int cFlagStartBit = PceConstantValues.METRIC_OBJECT_FLAG_C_START_BIT;
  private int cFlagEndBit = PceConstantValues.METRIC_OBJECT_FLAG_C_END_BIT;
  private int cFlagLength = PceConstantValues.METRIC_OBJECT_FLAG_C_LENGTH;

  private int bFlagStartBit = PceConstantValues.METRIC_OBJECT_FLAG_B_START_BIT;
  private int bFlagEndBit = PceConstantValues.METRIC_OBJECT_FLAG_B_END_BIT;
  private int bFlagLength = PceConstantValues.METRIC_OBJECT_FLAG_B_LENGTH;

  private int typeStartBit = PceConstantValues.METRIC_OBJECT_TYPE_START_BIT;
  private int typeEndBit = PceConstantValues.METRIC_OBJECT_TYPE_END_BIT;
  private int typeLength = PceConstantValues.METRIC_OBJECT_TYPE_LENGTH;

  private int metricValueStartBit = PceConstantValues.METRIC_OBJECT_METRIC_VALUE_START_BIT;
  private int metricValueEndBit = PceConstantValues.METRIC_OBJECT_METRIC_VALUE_END_BIT;
  private int metricValueLength = PceConstantValues.METRIC_OBJECT_METRIC_VALUE_LENGTH;

  /**
   * @param objectHeader
   * @param binaryString
   */
  public PceMetricObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  /**
   * @param objectHeader
   * @param cFlag
   * @param bFlag
   * @param type
   * @param metricValue
   */
  public PceMetricObject(
      PceCommonObjectHeader objectHeader, String cFlag, String bFlag, int type, float metricValue) {
    this.setObjectHeader(objectHeader);
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setCFlagBinaryString(cFlag);
    this.setBFlagBinaryString(bFlag);
    this.setTypeDecimalValue(type);
    this.setMetricValueBinaryString(Integer.toBinaryString(Float.floatToIntBits(metricValue)));
    this.updateHeaderLength();
  }

  public PceMetricObject() {}

  /** */
  private void updateHeaderLength() {
    int objectFrameByteLength = this.getObjectFrameByteLength();
    this.getObjectHeader().setLengthDecimalValue(objectFrameByteLength);
  }

  /** Object */
  public PceCommonObjectHeader getObjectHeader() {
    return objectHeader;
  }

  /* (non-Javadoc)
   * @see com.pcee.protocol.message.objectframe.PceObjectFrame#setObjectHeader(com.pcee.protocol.message.objectframe.PceCommonObjectHeader)
   */
  public void setObjectHeader(PceCommonObjectHeader objectHeader) {
    this.objectHeader = objectHeader;
  }

  /* (non-Javadoc)
   * @see com.pcee.protocol.message.objectframe.PceObjectFrame#getObjectBinaryString()
   */
  public String getObjectBinaryString() {
    String binaryString = reserved + flags + type + metricValue;
    return binaryString;
  }

  /* (non-Javadoc)
   * @see com.pcee.protocol.message.objectframe.PceObjectFrame#setObjectBinaryString(java.lang.String)
   */
  public void setObjectBinaryString(String binaryString) {
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String cFlagBinaryString = binaryString.substring(cFlagStartBit, cFlagEndBit + 1);
    String bFlagBinaryString = binaryString.substring(bFlagStartBit, bFlagEndBit + 1);
    String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
    String metricValueBinaryString =
        binaryString.substring(metricValueStartBit, metricValueEndBit + 1);

    this.setReservedBinaryString(reservedBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setCFlagBinaryString(cFlagBinaryString);
    this.setBFlagBinaryString(bFlagBinaryString);
    this.setTypeBinaryString(typeBinaryString);
    this.setMetricValueBinaryString(metricValueBinaryString);
  }

  /* (non-Javadoc)
   * @see com.pcee.protocol.message.objectframe.PceObjectFrame#getObjectFrameByteLength()
   */
  public int getObjectFrameByteLength() {
    int objectLength =
        reserved.length() + flags.length() + type.length() + (metricValue + "").length();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + headerLength) / 8;
    return objectFrameByteLength;
  }

  /* (non-Javadoc)
   * @see com.pcee.protocol.message.objectframe.PceObjectFrame#getObjectFrameBinaryString()
   */
  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  /** reserved */
  public String getReservedBinaryString() {
    return this.reserved;
  }

  /**
   * @param binaryString
   */
  public void setReservedBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, reservedLength);
    this.reserved = checkedBinaryString;
  }

  /**
   * @param startingBit
   * @param binaryString
   */
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

  /**
   * @return
   */
  public String getTypeBinaryString() {
    return this.type;
  }

  /**
   * @param decimalValue
   */
  public void setTypeDecimalValue(int decimalValue) {
    int binaryLength = typeLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.type = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  /**
   * @param binaryString
   */
  public void setTypeBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, typeLength);
    this.type = checkedBinaryString;
  }

  /**
   * @param startingBit
   * @param binaryString
   */
  public void setTypeBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(type, startingBit, binaryString, typeLength);
    this.type = checkedBinaryString;
  }

  /** metricValue */
  public String getMetricValueBinaryString() {
    return this.metricValue + "";
  }

  /**
   * @param binaryString
   */
  public void setMetricValueBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, metricValueLength);
    this.metricValue = checkedBinaryString;
  }

  /**
   * @param startingBit
   * @param binaryString
   */
  public void setMetricValueBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            String.valueOf(metricValue), startingBit, binaryString, metricValueLength);
    this.metricValue = checkedBinaryString;
  }

  /** flags */
  public int getFlagsDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flags);
    return decimalValue;
  }

  /**
   * @return
   */
  public String getFlagsBinaryString() {
    return this.flags;
  }

  /**
   * @param decimalValue
   */
  public void setFlagsDecimalValue(int decimalValue) {
    int binaryLength = flagsLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.flags = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  /**
   * @param binaryString
   */
  public void setFlagsBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /**
   * @param startingBit
   * @param binaryString
   */
  public void setFlagsBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** cFlag */
  public int getCFlagDecimalValue() {
    int relativeStartBit = (cFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + cFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  /**
   * @return
   */
  public String getCFlagBinaryString() {
    String binaryString = flags.substring(0, (cFlagStartBit - flagsStartBit) + cFlagLength);
    return binaryString;
  }

  /**
   * @param binaryString
   */
  public void setCFlagBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            this.flags, (cFlagStartBit - flagsStartBit), binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** bFlag */
  public int getBFlagDecimalValue() {
    int relativeStartBit = (bFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + bFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  /**
   * @return
   */
  public String getBFlagBinaryString() {
    int relativeStartBit = (bFlagStartBit - flagsStartBit);
    String binaryString = flags.substring(relativeStartBit, relativeStartBit + bFlagLength);
    return binaryString;
  }

  /**
   * @param binaryString
   */
  public void setBFlagBinaryString(String binaryString) {
    int relativeStartBit = (bFlagStartBit - flagsStartBit);
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    String reservedInfo = "Reserved=" + this.getReservedBinaryString();
    String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
    String typeInfo = ",Type=" + this.getTypeDecimalValue();
    String metricValueInfo = ",MetricValue=" + this.getMetricValueBinaryString();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = "<Metric:" + reservedInfo + flagsInfo + typeInfo + metricValueInfo + ">";

    return headerInfo + objectInfo;
  }

  /* (non-Javadoc)
   * @see com.pcee.protocol.message.objectframe.PceObjectFrame#binaryInformation()
   */
  public String binaryInformation() {
    String reservedBinaryInfo = getReservedBinaryString();
    String flagsInfo = "'" + this.getFlagsBinaryString();
    String typeBinaryInfo = "'" + getTypeBinaryString();
    String metricValueBinaryInfo = "'" + getMetricValueBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo =
        "[" + reservedBinaryInfo + flagsInfo + typeBinaryInfo + metricValueBinaryInfo + "]";

    return headerInfo + objectInfo;
  }

  /* (non-Javadoc)
   * @see com.pcee.protocol.message.objectframe.PceObjectFrame#contentInformation()
   */
  public String contentInformation() {
    return "[" + NAME + "]";
  }

  public static void main(String[] args) {
    PceMetricObject object = new PceMetricObject();
    object.setMetricValueBinaryString(Integer.toBinaryString(Float.floatToIntBits((float) 245.9)));
    String value = Integer.toBinaryString(Float.floatToIntBits(245.9f));
    Integer.valueOf(value, 2);
    System.out.println("length: " + Integer.toBinaryString(Float.floatToIntBits(245.9f)).length());
    System.out.println("float value : " + Integer.toBinaryString(Float.floatToIntBits(245.9f)));
    System.out.println(
        "Binary presentation of metric value: " + Integer.valueOf(object.metricValue, 2));
    System.out.println("value of value after : " + Integer.valueOf(value, 2));
  }
}
