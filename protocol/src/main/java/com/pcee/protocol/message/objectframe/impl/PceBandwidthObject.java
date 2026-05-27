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
 * |                        Bandwidth                              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

// TODO Change to IEEE Floating point object
public class PceBandwidthObject implements PceObjectFrame {

  /*public static void main(String[] args) {
  	PceBandwidthObject b = new PceBandwidthObject(
  			new PceCommonObjectHeader(6, 1, "1", "0"), 101.11f);

  	System.out.println(b.getBandwidthFloatValue());

  	String headerBinaryString = b.getObjectHeader().getHeaderBinaryString();
  	String objectBinaryString = b.getObjectBinaryString();
  	System.out.println(objectBinaryString);

  	PceCommonObjectHeader header = new PceCommonObjectHeader(
  			headerBinaryString);

  	PceBandwidthObject b2 = new PceBandwidthObject(header,
  			objectBinaryString);
  	System.out.println(b2.getBandwidthFloatValue());
  }*/

  private final String NAME = "Bandwidth";

  private String bandwidth;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;

  private int bandwidthStartBit = PceConstantValues.BANDWIDTH_OBJECT_BANDWIDTH_START_BIT;
  private int bandwidthEndBit = PceConstantValues.BANDWIDTH_OBJECT_BANDWIDTH_END_BIT;
  private int bandwidthLength = PceConstantValues.BANDWIDTH_OBJECT_BANDWIDTH_LENGTH;

  public PceBandwidthObject(
      PceCommonObjectHeader objectHeader, String bandwidthBinaryRepresentation) {
    this.setObjectHeader(objectHeader);
    this.setBandwidthBinaryString(bandwidthBinaryRepresentation);
    this.updateHeaderLength();
  }

  public PceBandwidthObject(PceCommonObjectHeader objectHeader, float bandwidth) {
    this.setObjectHeader(objectHeader);
    this.setBandwidthFloatValue(bandwidth);
    this.updateHeaderLength();
  }

  private void updateHeaderLength() {
    this.getObjectHeader().setLengthDecimalValue(this.getObjectFrameByteLength());
  }

  /** Object */
  public PceCommonObjectHeader getObjectHeader() {
    return objectHeader;
  }

  public void setObjectHeader(PceCommonObjectHeader objectHeader) {
    this.objectHeader = objectHeader;
  }

  public String getObjectBinaryString() {
    String binaryString = bandwidth;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String bandwidthBinaryString = binaryString.substring(bandwidthStartBit, bandwidthEndBit + 1);

    this.setBandwidthBinaryString(bandwidthBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength = bandwidth.length();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + headerLength) / 8;
    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  /** bandwidth */
  public float getBandwidthFloatValue() {
    return Float.intBitsToFloat(Integer.parseInt(bandwidth, 2));
  }

  public String getBandwidthBinaryString() {
    return this.bandwidth;
  }

  public void setBandwidthFloatValue(float floatValue) {

    int floatBits = Float.floatToIntBits(floatValue);

    String s = Integer.toBinaryString(floatBits);

    if (s.length() > 32) {
      System.out.println(
          "ERROR in PCEPBandwithObject.setBandwidthFloatValue(...)!!! Bandwith value is bigger than 32 bits, exiting now to let you fix it! :P");
      System.exit(0);
    }
    setBandwidthBinaryString(s);
  }

  public void setBandwidthBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, bandwidthLength);
    this.bandwidth = checkedBinaryString;
  }

  public String toString() {
    String bandwidthInfo = "Bandwidth=" + this.getBandwidthFloatValue();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = "<Bandwidth:" + bandwidthInfo + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String bandwidthBinaryInfo = getBandwidthBinaryString();
    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo = "[" + bandwidthBinaryInfo + "]";

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
