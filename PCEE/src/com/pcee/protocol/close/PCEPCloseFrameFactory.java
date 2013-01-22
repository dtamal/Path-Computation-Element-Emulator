/**
 *  This file is part of Path Computation Element Emulator (PCEE).
 *
 *  PCEE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PCEE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PCEE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcee.protocol.close;

import java.util.LinkedList;

import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPCloseObject;

public class PCEPCloseFrameFactory {

	public static PCEPCloseFrame generateCloseFrame(PCEPCloseObject closeObject) {

		PCEPCloseFrame closeFrame = new PCEPCloseFrame(closeObject);

		return closeFrame;
	}

	public static PCEPCloseFrame generateCloseFrame(int reason, String pFlag, String iFlag) {

		PCEPCloseObject closeObject = PCEPObjectFrameFactory.generatePCEPCloseObject(pFlag, iFlag, reason);
		PCEPCloseFrame closeFrame = new PCEPCloseFrame(closeObject);

		return closeFrame;
	}

	public static PCEPCloseFrame getCloseFrame(PCEPMessage message) {

		LinkedList<PCEPObjectFrame> objectList = message.getObjectsList();

		if (objectList.size() != 1) {
			localLogger("Wrong Close Message Format!");
			localDebugger("Close Message should only have one object. The size of the object list is : " + objectList.size());
			return null; // TODO
		}

		PCEPCloseObject closeObject = (PCEPCloseObject) objectList.get(0);

		return new PCEPCloseFrame(closeObject);

	}

	private static void localLogger(String event) {
		Logger.logSystemEvents("[PCEPCloseFrameFactory] " + event);
	}

	private static void localDebugger(String event) {
		Logger.debugger("[PCEPCloseFrameFactory] " + event);
	}

}
