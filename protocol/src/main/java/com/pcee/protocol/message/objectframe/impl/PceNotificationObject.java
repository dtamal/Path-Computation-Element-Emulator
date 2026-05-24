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
 * |   Reserved    |     Flags     |      NT       |     NV        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 *  //                      Optional TLVs                          //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public abstract class PceNotificationObject implements PceObjectFrame {

  private String jsonString;

  private PceCommonObjectHeader objectHeader;

  // private LinkedList<PceTlvObject> tlvList;

  protected void updateHeaderLength() {
    int objectFrameByteLength = this.getObjectFrameByteLength();
    this.getObjectHeader().setLengthDecimalValue(objectFrameByteLength);
  }

  public String getJsonString() {
    return jsonString;
  }

  public void setJsonString(String input) {
    this.jsonString = input;
  }

  /** Object */
  public PceCommonObjectHeader getObjectHeader() {
    return objectHeader;
  }

  public void setObjectHeader(PceCommonObjectHeader objectHeader) {
    this.objectHeader = objectHeader;
  }

  public String getObjectBinaryString() {
    String binaryString = PceComputationFactory.byteArrayToRawMessage(jsonString.getBytes());
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    jsonString = new String(PceComputationFactory.rawMessageToByteArray(binaryString));
  }

  public int getObjectFrameByteLength() {
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (headerLength + this.getObjectBinaryString().length()) / 8;
    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();
    return headerBinaryString + objectBinaryString;
  }

  /** reserved */
  public String toString() {
    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = "<Notification:" + jsonString + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    return this.getObjectHeader().binaryInformation() + this.getObjectBinaryString();
  }
}
