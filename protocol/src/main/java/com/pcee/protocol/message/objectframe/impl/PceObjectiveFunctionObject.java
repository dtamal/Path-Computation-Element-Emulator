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
 * |   OFCode                      |   Reserved                    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                     Optional TLVs                           //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceObjectiveFunctionObject implements PceObjectFrame {

  private final String NAME = "Error";

  private String reserved;
  private String ofCode;

  private PceCommonObjectHeader objectHeader;

  private int reservedStartBit = PceConstantValues.OF_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.OF_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.OF_OBJECT_RESERVED_LENGTH;

  private int ofCodeStartBit = PceConstantValues.OF_OBJECT_OFCODE_START_BIT;
  private int ofCodeEndBit = PceConstantValues.OF_OBJECT_OFCODE_END_BIT;
  private int ofCodeLength = PceConstantValues.OF_OBJECT_OFCODE_LENGTH;

  public PceObjectiveFunctionObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceObjectiveFunctionObject(PceCommonObjectHeader objectHeader, int ofCode) {
    this.setObjectHeader(objectHeader);
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
    this.setOfCodeDecimalValue(ofCode);
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

  public void setObjectBinaryString(String binaryString) {
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
    String ofCodeBinaryString = binaryString.substring(ofCodeStartBit, ofCodeEndBit + 1);

    this.setReservedBinaryString(reservedBinaryString);
    this.setOfCodeBinaryString(ofCodeBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength = reserved.length() + ofCode.length();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + headerLength) / 8;
    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  public String getObjectBinaryString() {
    String binaryString = ofCode + reserved;
    return binaryString;
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
  public int getOfCodeDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(ofCode);
    return decimalValue;
  }

  public String getOfCodeBinaryString() {
    return this.ofCode;
  }

  public void setOfCodeDecimalValue(int decimalValue) {
    int binaryLength = ofCodeLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.ofCode = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setOfCodeBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, ofCodeLength);
    this.ofCode = checkedBinaryString;
  }

  public String toString() {
    String reservedInfo = "Reserved=" + this.getReservedDecimalValue();
    String ofCodeInfo = ",OfCode=" + this.getOfCodeDecimalValue();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = NAME + ":" + reservedInfo + ofCodeInfo + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String reservedBinaryInfo = getReservedBinaryString();
    String ofCodeInfo = "'" + this.getOfCodeBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo = "[" + reservedBinaryInfo + ofCodeInfo + "]";

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
