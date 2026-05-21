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

package com.pcee.protocol.message.objectframe.impl;

import java.util.LinkedList;

import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;

/**
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                        (Subobjects)                          //
 * |                                                               |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPIncludeRouteObject implements PCEPObjectFrame {

	private final String NAME = "Include Route Object";

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;
	private LinkedList<PCEPObjectFrame> subObjects;
	private String objectsString;

	public PCEPIncludeRouteObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPIncludeRouteObject(PCEPCommonObjectHeader objectHeader, LinkedList<PCEPObjectFrame> subObjects) {
		this.setObjectHeader(objectHeader);
		this.setSubObjects(subObjects);
		this.updateHeaderLength();
	}

	private void updateHeaderLength() {
		int objectFrameByteLength = this.getObjectFrameByteLength();
		this.getObjectHeader().setLengthDecimalValue(objectFrameByteLength);
	}

	/**
	 * Object
	 */
	public PCEPCommonObjectHeader getObjectHeader() {
		return objectHeader;
	}

	public void setObjectHeader(PCEPCommonObjectHeader objectHeader) {
		this.objectHeader = objectHeader;
	}

	public String getObjectBinaryString() {

		StringBuffer subObjectsStringBuffer = new StringBuffer();

		for (int i = 0; i < subObjects.size(); i++) {
			PCEPObjectFrame object = subObjects.get(i);
			subObjectsStringBuffer.append(object.getObjectFrameBinaryString());
		}
		this.objectsString = subObjectsStringBuffer.toString();
		return objectsString;
	}

	public void setObjectBinaryString(String binaryString) {
		this.objectsString = binaryString;
	}

	public int getObjectFrameByteLength() {
		int objectLength = this.getSubObjectsBinaryLength();
		int headerLength = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH;
		int objectFrameByteLength = (objectLength + headerLength / 8);

		return objectFrameByteLength;
	}

	public String getObjectFrameBinaryString() {
		String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
		String objectBinaryString = this.getObjectBinaryString();

		return headerBinaryString + objectBinaryString;
	}

	public void setSubObjects(LinkedList<PCEPObjectFrame> subObjects) {
		this.subObjects = subObjects;
	}

	public LinkedList<PCEPObjectFrame> getSubObjects() {
		return this.subObjects;
	}

	private int getSubObjectsBinaryLength() {
		int length = 0;
		for (int i = 0; i < subObjects.size(); i++) {
			length += subObjects.get(i).getObjectFrameByteLength();
		}
		return length;
	}

	public String toString() {

		String headerInfo = this.getObjectHeader().toString();

		StringBuffer objectInfo = new StringBuffer();

		objectInfo.append("<Include Route Object:");
		for (int i = 0; i < subObjects.size(); i++) {
			objectInfo.append(subObjects.get(i).toString());
		}
		objectInfo.append(">");

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {

		String headerInfo = this.getObjectHeader().binaryInformation();

		StringBuffer objectInfo = new StringBuffer();

		for (int i = 0; i < subObjects.size(); i++) {
			objectInfo.append(subObjects.get(i).binaryInformation());
		}

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
