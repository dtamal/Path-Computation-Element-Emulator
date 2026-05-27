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
package com.pcee.protocol.open;

import com.pcee.protocol.message.PceMessageFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PceOpenObject;
import java.util.LinkedList;

public class PceOpenFrame implements PceMessageFrame {

  public final int MESSAGE_TYPE = 1;

  private PceOpenObject openObject;

  public PceOpenFrame(PceOpenObject openObject) {
    this.openObject = openObject;
  }

  public PceOpenObject getOpenObject() {
    return openObject;
  }

  public int getByteLength() {
    int length = 0;

    length += openObject.getObjectFrameByteLength();

    return length;
  }

  public String getBinaryString() {

    StringBuffer objectsString = new StringBuffer();

    objectsString.append(openObject.getObjectFrameBinaryString());

    return objectsString.toString();
  }

  public LinkedList<PceObjectFrame> getObjectFrameLinkedList() {

    LinkedList<PceObjectFrame> requestObjects = new LinkedList<PceObjectFrame>();
    requestObjects.add(openObject);

    return requestObjects;
  }

  public int getMessageType() {
    return MESSAGE_TYPE;
  }
}
