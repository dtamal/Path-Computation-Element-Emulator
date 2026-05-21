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
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPCloseObject;
import java.util.LinkedList;
import org.slf4j.Logger;

public class PCEPCloseFrameFactory {

  private static Logger logger = PceeLoggerFactory.getLogger(PCEPCloseFrameFactory.class);

  public static PCEPCloseFrame generateCloseFrame(PCEPCloseObject closeObject) {

    PCEPCloseFrame closeFrame = new PCEPCloseFrame(closeObject);

    return closeFrame;
  }

  public static PCEPCloseFrame generateCloseFrame(int reason, String pFlag, String iFlag) {

    PCEPCloseObject closeObject =
        PCEPObjectFrameFactory.generatePCEPCloseObject(pFlag, iFlag, reason);
    PCEPCloseFrame closeFrame = new PCEPCloseFrame(closeObject);

    return closeFrame;
  }

  public static PCEPCloseFrame getCloseFrame(PCEPMessage message) {

    LinkedList<PCEPObjectFrame> objectList = message.getObjectsList();

    if (objectList.size() != 1) {
      logger.error(
          "Wrong Close Message Format! Close Message should only have one object. The size of the object list is : "
              + objectList.size());
      return null; // TODO
    }

    PCEPCloseObject closeObject = (PCEPCloseObject) objectList.get(0);

    return new PCEPCloseFrame(closeObject);
  }
}
