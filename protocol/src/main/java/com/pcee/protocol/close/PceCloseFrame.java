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
package com.pcee.protocol.close;

import com.pcee.protocol.message.PceMessageFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PceCloseObject;
import java.util.LinkedList;

public class PceCloseFrame implements PceMessageFrame {

  public final int MESSAGE_TYPE = 7;

  private final PceCloseObject closeObject;

  public PceCloseFrame(PceCloseObject closeObject) {
    this.closeObject = closeObject;
  }

  public PceCloseObject getCloseObject() {
    return closeObject;
  }

  public int getByteLength() {
    int length = 0;

    length += closeObject.getObjectFrameByteLength();

    return length;
  }

  public String getBinaryString() {

    return closeObject.getObjectFrameBinaryString();
  }

  public LinkedList<PceObjectFrame> getObjectFrameLinkedList() {

    LinkedList<PceObjectFrame> objectList = new LinkedList<PceObjectFrame>();
    objectList.add(closeObject);

    return objectList;
  }

  public int getMessageType() {
    return MESSAGE_TYPE;
  }
}
