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

import com.pcee.protocol.message.PCEPMessageFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PCEPOpenObject;

public class PCEPOpenFrame implements PCEPMessageFrame {

	public final int MESSAGE_TYPE = 1;

	private PCEPOpenObject openObject;

	public PCEPOpenFrame(PCEPOpenObject openObject) {
		this.openObject = openObject;
	}

	public PCEPOpenObject getOpenObject() {
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

	public LinkedList<PCEPObjectFrame> getObjectFrameLinkedList() {

		LinkedList<PCEPObjectFrame> requestObjects = new LinkedList<PCEPObjectFrame>();
		requestObjects.add(openObject);

		return requestObjects;
	}

	public int getMessageType() {
		return MESSAGE_TYPE;
	}

}
