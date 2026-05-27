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
package com.pcee.protocol.keepalive;

import com.pcee.protocol.message.PceMessageFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import java.util.LinkedList;

public class PceKeepaliveFrame implements PceMessageFrame {

  public final int MESSAGE_TYPE = 2;

  public PceKeepaliveFrame() {}

  public int getByteLength() {
    return 0;
  }

  public String getBinaryString() {
    return "";
  }

  public LinkedList<PceObjectFrame> getObjectFrameLinkedList() {
    return new LinkedList<PceObjectFrame>();
  }

  public int getMessageType() {
    return MESSAGE_TYPE;
  }
}
