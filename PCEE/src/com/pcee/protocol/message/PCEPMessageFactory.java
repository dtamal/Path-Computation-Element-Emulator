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
import com.pcee.protocol.message.objectframe.*;
import com.pcee.protocol.message.objectframe.impl.*;

public class PCEPMessageFactory {

	static short MESSAGE_HEADER_LENGTH = PCEPConstantValues.COMMON_MESSAGE_HEADER_LENGTH;
	static short OBJECT_HEADER_LENGTH = 32;

	public static String getPCEPCommonMessageHeaderString(String rawMessageString) {
		return rawMessageString.substring(0, MESSAGE_HEADER_LENGTH);
	}

	public static String getPCEPObjectCollectionString(String rawMessageString) {
		return rawMessageString.substring(MESSAGE_HEADER_LENGTH);
	}

	/**
	 * Simplificated Error fabrication
	 */
	public static PCEPMessage generateSIMPLEErrorMessage(int type, int value, String pFlag, String iFlag) {

		PCEPCommonMessageHeader messageHeader = new PCEPCommonMessageHeader(1, 6);

		PCEPErrorObject errorObject = PCEPObjectFrameFactory.generatePCEPErrorObject(pFlag, iFlag, type, value);

		int messageHeaderByteLength = PCEPConstantValues.COMMON_MESSAGE_HEADER_LENGTH / 8;
		int objectFrameByteLength = errorObject.getObjectFrameByteLength();

		messageHeader.setLengthDecimalValue(messageHeaderByteLength + objectFrameByteLength);

		String messageHeaderBinaryString = messageHeader.getHeaderBinaryString();
		String objectFrameBinaryString = errorObject.getObjectFrameBinaryString();

		String rawMessageString = messageHeaderBinaryString + objectFrameBinaryString;

		byte[] byteArray = PCEPComputationFactory.rawMessageToByteArray(rawMessageString);

		return new PCEPMessage(byteArray);
	}

	public static PCEPMessage generateMessage(PCEPMessageFrame messageFrame){
		PCEPCommonMessageHeader messageHeader = new PCEPCommonMessageHeader(1, messageFrame.getMessageType());
		LinkedList<PCEPObjectFrame> objectsList = messageFrame.getObjectFrameLinkedList();
		int messageHeaderByteLength = PCEPConstantValues.COMMON_MESSAGE_HEADER_LENGTH / 8;
		int objectsByteLength = messageFrame.getByteLength();

		messageHeader.setLengthDecimalValue(messageHeaderByteLength + objectsByteLength);

		return new PCEPMessage(messageHeader, objectsList);
	}

	public static void log(String logString){
		System.out.println("PCEPMessageFactory::: " + logString);
	}
}
