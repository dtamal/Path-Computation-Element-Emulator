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

import com.pcee.logger.PceeLoggerFactory;
import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PceCloseObject;
import java.util.LinkedList;
import org.slf4j.Logger;

public class PceCloseFrameFactory {

  private static final Logger logger = PceeLoggerFactory.getLogger(PceCloseFrameFactory.class);

  public static PceCloseFrame generateCloseFrame(PceCloseObject closeObject) {
    if (closeObject == null) {
      throw new NullPointerException("CloseObject cannot be null");
    }
    return new PceCloseFrame(closeObject);
  }

  public static PceCloseFrame generateCloseFrame(int reason, String pFlag, String iFlag) {

    PceCloseObject closeObject =
        PceObjectFrameFactory.generatePCEPCloseObject(pFlag, iFlag, reason);

    return new PceCloseFrame(closeObject);
  }

  public static PceCloseFrame getCloseFrame(PceMessage message) {

    if (message == null) {
      throw new NullPointerException("Message cannot be null");
    }

    LinkedList<PceObjectFrame> objectList = message.getObjectsList();

    if (objectList.size() != 1) {
      logger.error(
          "Wrong Close Message Format! Close Message should only have one object. The size of the object list is : {}",
          objectList.size());
      return null; // TODO replace this with an exception.
    }

    PceCloseObject closeObject = (PceCloseObject) objectList.getFirst();

    return new PceCloseFrame(closeObject);
  }
}
