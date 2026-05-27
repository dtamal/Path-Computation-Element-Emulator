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
package com.pcee.protocol.message;

import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PceAddress;
import java.util.LinkedList;

public class PceMessage {

  private final String NAME = "Message Header";

  private final PceCommonMessageHeader messageHeader;
  private final LinkedList<PceObjectFrame> objectsList;
  private PceAddress address;

  public PceMessage(byte[] messageByteArray) {
    String rawMessageString = PceComputationFactory.byteArrayToRawMessage(messageByteArray);

    String messageHeaderString =
        rawMessageString.substring(0, PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH);
    String objectsRawString =
        rawMessageString.substring(PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH);

    this.messageHeader = new PceCommonMessageHeader(messageHeaderString);
    this.objectsList = PceObjectFrameFactory.PCEPObjectFabrication(objectsRawString);
  }

  //	public PceMessage(String rawMessageString) {
  //		String messageHeaderString = rawMessageString.substring(0,
  // PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH);
  //		String objectsRawString =
  // rawMessageString.substring(PceConstantValues.COMMON_MESSAGE_HEADER_LENGTH);
  //
  //		this.messageHeader = new PceCommonMessageHeader(messageHeaderString);
  //		this.objectsList = PceObjectFrameFactory.PCEPObjectFabrication(objectsRawString);
  //	}

  public PceMessage(PceCommonMessageHeader header, LinkedList<PceObjectFrame> objectsList) {
    this.messageHeader = header;
    this.objectsList = objectsList;
  }

  public PceCommonMessageHeader getMessageHeader() {
    return messageHeader;
  }

  public LinkedList<PceObjectFrame> getObjectsList() {
    return objectsList;
  }

  public String getMessageString() {

    StringBuilder messageStringBuffer = new StringBuilder();
    messageStringBuffer.append(messageHeader.getHeaderBinaryString());

    for (PceObjectFrame pceObjectFrame : objectsList) {

      String currentHeaderString = pceObjectFrame.getObjectHeader().getHeaderBinaryString();
      String currentObjectString = pceObjectFrame.getObjectBinaryString();
      messageStringBuffer.append(currentHeaderString).append(currentObjectString);
    }

    return messageStringBuffer.toString();
  }

  public byte[] getMessageByteArray() {
    return PceComputationFactory.rawMessageToByteArray(getMessageString());
  }

  public PceAddress getAddress() {
    return address;
  }

  public void setAddress(PceAddress address) {
    this.address = address;
  }

  public String toString() {
    String messageHeaderInfo = messageHeader.toString();
    StringBuilder objectFrameBuffer = new StringBuilder();

    for (PceObjectFrame pceObjectFrame : objectsList) {
      objectFrameBuffer.append(pceObjectFrame.toString());
    }

    return messageHeaderInfo + objectFrameBuffer;
  }

  public String binaryInformation() {
    String messageHeaderInfo = messageHeader.binaryInformation();
    StringBuilder objectFrameBuffer = new StringBuilder();

    for (PceObjectFrame pceObjectFrame : objectsList) {
      String msg = pceObjectFrame.binaryInformation();
      objectFrameBuffer.append(msg);
    }

    return messageHeaderInfo + objectFrameBuffer;
  }

  public String contentInformation() {
    StringBuilder objectFrameBuffer = new StringBuilder();

    objectFrameBuffer.append("[" + NAME + "]");

    for (PceObjectFrame pceObjectFrame : objectsList) {
      /*			System.out.println("*****************************************************");
      System.out.println("objectList.size() = " + objectsList.size());
      System.out.println("contentInformation() "+ i + " : " + objectsList.get(i));
      System.out.println("*****************************************************"); */
      String msg = pceObjectFrame.contentInformation();
      objectFrameBuffer.append(msg);
    }

    return objectFrameBuffer.toString();
  }
}
