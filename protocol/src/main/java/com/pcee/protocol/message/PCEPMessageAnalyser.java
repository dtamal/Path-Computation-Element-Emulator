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

package com.pcee.protocol.message;

import java.util.LinkedList;

import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;

public class PCEPMessageAnalyser {

	public static boolean checkMessageFormat(PCEPMessage message) {
		PCEPCommonMessageHeader messageHeader = message.getMessageHeader();
		LinkedList<PCEPObjectFrame> objectsList = message.getObjectsList();

		int messageType = messageHeader.getTypeDecimalValue();

		switch (messageType) {

		case 0: { // UNDEFIND MSG
			return false;
		}

		case 1: {

			if (objectsList.size() > 1) {
				return false;
			}

			PCEPCommonObjectHeader objectHeader = objectsList.get(0).getObjectHeader();

			if (objectHeader.getClassDecimalValue() != 1 || objectHeader.getTypeDecimalValue() != 1) {
				return false;
			}

			return true; // TODO
		}

		case 2: { // KEEPALIVE MSG
			return true; // TODO
		}
		case 3: { // PATH COMPUTATION REQUEST MSG
			return true; // TODO
		}
		case 4: { // PATH COMPUTATION REPLY MSG
			return true; // TODO
		}
		case 5: { // NOTIFICATION MSG
			return true; // TODO
		}
		case 6: { // ERROR MSG
			return true; // TODO
		}
		case 7: { // CLOSE MSG
			return true; // TODO
		}
		default: { // UNDEFIND MSG
			return true;
		}

		}

	}

	
	public static int checkSessionCharacteristics(PCEPMessage message) {
		//TODO
		// 1=acceptable ; 0 = unacceptable&nagotiable ; -1 =
		// unacceptable&un-nagotiable
		return 1;
	}

}
