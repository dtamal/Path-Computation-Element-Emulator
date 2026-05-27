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

import com.pcee.logger.PceeLoggerFactory;
import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PceOpenObject;
import java.util.LinkedList;
import org.slf4j.Logger;

public class PceOpenFrameFactory {

  private static final Logger logger = PceeLoggerFactory.getLogger(PceOpenFrameFactory.class);

  public static PceOpenFrame generateOpenFrame(PceOpenObject openObject) {
    if (openObject == null) {
      throw new NullPointerException("openObject cannot be null");
    }
    return new PceOpenFrame(openObject);
  }

  public static PceOpenFrame generateOpenFrame(
      int keepAlive, int deadTimer, String pFlag, String iFlag) {

    PceOpenObject openObject =
        PceObjectFrameFactory.generatePCEPOpenObject(pFlag, iFlag, keepAlive, deadTimer);

    return new PceOpenFrame(openObject);
  }

  public static PceOpenFrame getOpenFrame(PceMessage message) {
    if (message == null) {
      throw new NullPointerException("message cannot be null");
    }

    LinkedList<PceObjectFrame> objectList = message.getObjectsList();

    if (objectList.size() != 1) {
      logger.error(
          "Wrong OpenMessage Format! Object list inside the OPEN Frame is not equal to 1. The size of the object list is {}",
          objectList.size());
      return null; // TODO
    }

    PceOpenObject openObject = (PceOpenObject) objectList.getFirst();

    return new PceOpenFrame(openObject);
  }
}
