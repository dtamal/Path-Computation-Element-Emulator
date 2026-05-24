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
package com.pcee.protocol.message.objectframe.impl.erosubobjects;

import com.pcee.protocol.message.PceComputationFactory;

/**
 * <pre>
 *    0                   1                   2                   3
 *    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |L|    Type     |     Length    |    Reserved (MUST be zero)    |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |switching cap  |  encoding     |            Reserved           |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |               Optional TLV (Not Implemented)                  |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * <pre>
 */
public class MlDelimiter extends EroSubobjects {

  private int reservedStartBit = 16;
  private int reservedEndBit = 31;
  private int reservedLength = 16;

  private String reserved;
  private String reserved1;
  private String swCap;
  private String encoding;

  private int swCapStartBit = 32;
  private int swCapEndBit = 39;
  private int swCapLength = 8;

  private int encodingStartBit = 40;
  private int encodingEndBit = 47;
  private int encodingLength = 8;

  private int reserved1StartBit = 48;
  private int reserved1EndBit = 63;
  private int reserved1Length = 16;

  public MlDelimiter(String binaryString) {
    NAME = "MlDelimiter";
    this.setObjectBinaryString(binaryString);
  }

  public MlDelimiter() {
    NAME = "MlDelimiter";
    this.setLFlag(false);
    this.setTypeDecimalValue(EroSubobjects.PCEPMLDelimiterType);
    this.setLengthDecimalValue(8);
    this.setReservedDecimalValue(0);
    this.setReserved1DecimalValue(0);
    this.setSwCapDecimalValue(1);
    this.setEncodingDecimalValue(1);
  }

  public static void main(String[] args) {
    MlDelimiter e = new MlDelimiter();
    String eBinary = e.getObjectBinaryString();
    MlDelimiter e2 = new MlDelimiter(eBinary);

    System.out.println(e.binaryInformation());
    System.out.println(e2.binaryInformation());

    System.out.println(e.toString());
    System.out.println(e2.toString());
  }

  /** Object */
  public String getObjectBinaryString() {
    String binaryString = lFlag + type + length + reserved + swCap + encoding + reserved1;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);
    String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
    String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
    String switchingCapBinaryString = binaryString.substring(swCapStartBit, swCapEndBit + 1);
    String encodingBinaryString = binaryString.substring(encodingStartBit, encodingEndBit + 1);
    String reserved1BinaryString = binaryString.substring(reserved1StartBit, reserved1EndBit + 1);

    this.setLFlagBinaryString(lFlagBinaryString);
    this.setTypeBinaryString(typeBinaryString);
    this.setLengthBinaryString(lengthBinaryString);
    this.setReservedBinaryString(reservedBinaryString);
    this.setSwCapBinaryString(switchingCapBinaryString);
    this.setEncodingBinaryString(encodingBinaryString);
    this.setReserved1BinaryString(reserved1BinaryString);
  }

  public int getByteLength() {
    int objectLength =
        lFlag.length()
            + type.length()
            + length.length()
            + reserved.length()
            + swCap.length()
            + encoding.length()
            + reserved1.length();
    int objectFrameByteLength = objectLength / 8;

    return objectFrameByteLength;
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
    // String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString,
    // reservedLength);
    this.reserved = binaryString;
  }

  /** swCap */
  public String getSwCapBinaryString() {
    return swCap;
  }

  public int getSwCapDecimalValue() {
    int temp = (int) PceComputationFactory.getDecimalValue(swCap);
    return temp;
  }

  public void setSwCapBinaryString(String binaryString) {
    // String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString,
    // routerIDLength);
    this.swCap = binaryString;
  }

  public void setSwCapDecimalValue(int decimalValue) {
    int binaryLength = swCapLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength - 1);

    this.swCap = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  /** encoding */
  public String getEncodingBinaryString() {
    return encoding;
  }

  public int getEncodingDecimalValue() {
    int temp = (int) PceComputationFactory.getDecimalValue(encoding);
    return temp;
  }

  public void setEncodingBinaryString(String binaryString) {
    // String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString,
    // routerIDLength);
    this.encoding = binaryString;
  }

  public void setEncodingDecimalValue(int decimalValue) {
    int binaryLength = encodingLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength - 1);

    this.encoding = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  /** reserved1 */
  public int getReserved1DecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(reserved1);
    return decimalValue;
  }

  public String getReserved1BinaryString() {
    return this.reserved1;
  }

  public void setReserved1DecimalValue(int decimalValue) {
    int binaryLength = reserved1Length;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.reserved1 = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setReserved1BinaryString(String binaryString) {
    // String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString,
    // reservedLength);
    this.reserved1 = binaryString;
  }

  /** OUTPUT */
  public String toString() {
    String lFlagInfo = "lFlag=" + this.getLFlagDecimalValue();
    String typeInfo = ", type=" + this.getTypeDecimalValue();
    String lengthInfo = ", length=" + this.getLengthDecimalValue();
    String reservedInfo = ", Reserved=" + this.getReservedDecimalValue();
    String swCapInfo = ", Switching Capacity=" + this.getSwCapDecimalValue();
    String encodingInfo = ", Encoding=" + this.getEncodingDecimalValue();
    String reserved1Info = ", Reserved1=" + this.getReserved1DecimalValue();

    String objectInfo =
        NAME
            + ":"
            + lFlagInfo
            + typeInfo
            + lengthInfo
            + reservedInfo
            + swCapInfo
            + encodingInfo
            + reserved1Info
            + ">";
    return objectInfo;
  }

  public String binaryInformation() {
    String lFlagBinaryInfo = getLFlagBinaryString();
    String typeBinaryInfo = "'" + getTypeBinaryString();
    String lengthBinaryInfo = "'" + getLengthBinaryString();
    String reservedInfo = "'" + getReservedBinaryString();
    String swCapInfo = "'" + getSwCapBinaryString();
    String encodingInfo = "'" + getEncodingBinaryString();
    String reserved1Info = "'" + getReserved1BinaryString();

    String objectInfo =
        "["
            + lFlagBinaryInfo
            + typeBinaryInfo
            + lengthBinaryInfo
            + reservedInfo
            + swCapInfo
            + encodingInfo
            + reserved1Info
            + "]";

    return objectInfo;
  }
}
