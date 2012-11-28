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
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

public class PCEPMessage {
	
	private final String NAME = "Message Header";

	
	private PCEPCommonMessageHeader messageHeader;
	private LinkedList<PCEPObjectFrame> objectsList;
	private PCEPAddress address;
	
	public PCEPMessage(byte[] messageByteArray){
		String rawMessageString = PCEPComputationFactory.byteArrayToRawMessage(messageByteArray);
		
		
		String messageHeaderString = rawMessageString.substring(0, PCEPConstantValues.COMMON_MESSAGE_HEADER_LENGTH);
		String objectsRawString = rawMessageString.substring(PCEPConstantValues.COMMON_MESSAGE_HEADER_LENGTH);

		this.messageHeader = new PCEPCommonMessageHeader(messageHeaderString);
		this.objectsList = PCEPObjectFrameFactory.PCEPObjectFabrication(objectsRawString);
		
	}
	
	public PCEPMessage(PCEPCommonMessageHeader header, LinkedList<PCEPObjectFrame> objectsList) {
		this.messageHeader = header;
		this.objectsList = objectsList;
	}

	public PCEPCommonMessageHeader getMessageHeader() {
		return messageHeader;
	}

	public LinkedList<PCEPObjectFrame> getObjectsList() {
		return objectsList;
	}

	public String getMessageString() {

		StringBuffer messageStringBuffer = new StringBuffer();
		messageStringBuffer.append(messageHeader.getHeaderBinaryString());

		for (short i = 0; i < objectsList.size(); i++) {

			String currentHeaderString = objectsList.get(i).getObjectHeader().getHeaderBinaryString();
			String currentObjectString = objectsList.get(i).getObjectBinaryString();
			messageStringBuffer.append(currentHeaderString + currentObjectString);
		}

		String outputString = messageStringBuffer.toString();

		return outputString;
	}
	
	
	public byte[] getMessageByteArray(){
		byte[] byteArray = PCEPComputationFactory.rawMessageToByteArray(getMessageString());
		
		return byteArray;
	}

	public PCEPAddress getAddress() {
		return address;
	}

	public void setAddress(PCEPAddress address) {
		this.address = address;
	}

	public String toString() {
		String messageHeaderInfo = messageHeader.toString();
		StringBuffer objectFrameBuffer = new StringBuffer();

		for (short i = 0; i < objectsList.size(); i++) {
			objectFrameBuffer.append(objectsList.get(i).toString());
		}

		return messageHeaderInfo + objectFrameBuffer.toString();
	}

	public String binaryInformation() {
		String messageHeaderInfo = messageHeader.binaryInformation();
		StringBuffer objectFrameBuffer = new StringBuffer();

		for (short i = 0; i < objectsList.size(); i++) {
			String msg = objectsList.get(i).binaryInformation();
			objectFrameBuffer.append(msg);
		}

		return messageHeaderInfo + objectFrameBuffer.toString();
	}

	public String contentInformation() {
		StringBuffer objectFrameBuffer = new StringBuffer();

		objectFrameBuffer.append("[" + NAME + "]");

		for (short i = 0; i < objectsList.size(); i++) {
/*			System.out.println("*****************************************************");
			System.out.println("objectList.size() = " + objectsList.size());
			System.out.println("contentInformation() "+ i + " : " + objectsList.get(i));
			System.out.println("*****************************************************"); */
			String msg = objectsList.get(i).contentInformation();
			objectFrameBuffer.append(msg);
		}

		return objectFrameBuffer.toString();
	}

}
