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

package com.pcee.protocol.open;

import java.util.LinkedList;

import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPOpenObject;

public class PCEPOpenFrameFactory {

	public static PCEPOpenFrame generateOpenFrame(PCEPOpenObject openObject) {

		PCEPOpenFrame openFrame = new PCEPOpenFrame(openObject);

		return openFrame;
	}

	public static PCEPOpenFrame generateOpenFrame(int keepAlive, int deadTimer, String pFlag, String iFlag) {

		PCEPOpenObject openObject = PCEPObjectFrameFactory.generatePCEPOpenObject(pFlag, iFlag, keepAlive, deadTimer);
		PCEPOpenFrame open = new PCEPOpenFrame(openObject);

		return open;
	}

	public static PCEPOpenFrame getOpenFrame(PCEPMessage message) {

		LinkedList<PCEPObjectFrame> objectList = message.getObjectsList();

		if (objectList.size() != 1) {
			localLogger("Wrong OpenMessage Format!");
			localDebugger("Object list inside the OPEN Frame is not equal to 1. The size of the object list is " + objectList.size());
			return null; // TODO
		}

		PCEPOpenObject openObject = (PCEPOpenObject) objectList.get(0);

		return new PCEPOpenFrame(openObject);

	}

	private static void localLogger(String event) {
		Logger.logSystemEvents("[PCEPOpenFrameFactory] " + event);
	}

	private static void localDebugger(String event) {
		Logger.debugger("[PCEPOpenFrameFactory] " + event);
	}

}
