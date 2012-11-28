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

import java.util.ArrayList;
import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

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

// TODO Generate methods to split and identify objects
public class PCEPExplicitRouteObjectImpl extends PCEPExplicitRouteObject {

	private final String NAME = "Explicit Route Object";

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;
	private ArrayList<PCEPAddress> traversedVertexList = new ArrayList<PCEPAddress>();

	// private String subObjectsString;

	public PCEPExplicitRouteObjectImpl(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPExplicitRouteObjectImpl(PCEPCommonObjectHeader objectHeader, ArrayList<PCEPAddress> traversedVertexList) {
		this.setObjectHeader(objectHeader);
		this.setTraversedVertexList(traversedVertexList);
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

		for (int i = 0; i < traversedVertexList.size(); i++) {
			PCEPAddress address = traversedVertexList.get(i);
			subObjectsStringBuffer.append(address.serialize());
		}

		return subObjectsStringBuffer.toString();
	}

	public void setObjectBinaryString(String binaryString) {

		ArrayList<PCEPAddress> vertexList = new ArrayList<PCEPAddress>();

		while (binaryString.length() > 0) {
			String addressBinaryString = binaryString.substring(0, 64); // FIXME
			PCEPAddress address = new PCEPAddress(addressBinaryString, true);
			vertexList.add(address);

			binaryString = binaryString.substring(64);
		}

		setTraversedVertexList(vertexList);

	}

	public int getObjectFrameByteLength() {
		int objectsBinaryLength = this.getTraversedVertexListBinaryLength();
		int headerLength = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH / 8;
		int objectFrameByteLength = objectsBinaryLength + headerLength;

		return objectFrameByteLength;
	}

	public String getObjectFrameBinaryString() {
		String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
		String objectBinaryString = this.getObjectBinaryString();

		return headerBinaryString + objectBinaryString;
	}

	public void setTraversedVertexList(ArrayList<PCEPAddress> subObjects) {
		this.traversedVertexList = subObjects;
	}

	public ArrayList<PCEPAddress> getTraversedVertexList() {
		return this.traversedVertexList;
	}

	private int getTraversedVertexListBinaryLength() {
		int length = 0;
		for (int i = 0; i < traversedVertexList.size(); i++) {
			length += ((traversedVertexList.get(i).getByteLength()));
		}
		return length;
	}

	public String printPath() {
		String traversedVertexesList = new String();

		for (PCEPAddress address : traversedVertexList) {
			traversedVertexesList = traversedVertexesList + address.getIPv4Address(false) + "-";
		}

		return traversedVertexesList;
	}

	// public String getTraversedVertexes() {
	// String traversedVertexesList = new String();
	//
	// for (int i = 0; i < traversedVertexList.size(); i++) {
	//
	// traversedVertexesList = traversedVertexesList +
	// traversedVertexList.get(i).getAddress() + "-->";
	//
	// if (i != traversedVertexList.size()) {
	// traversedVertexesList = traversedVertexesList + "--";
	// }
	// }
	//
	// return traversedVertexesList;
	// }

	public String toString() {

		String headerInfo = this.getObjectHeader().toString();

		StringBuffer objectInfo = new StringBuffer();

		objectInfo.append("<Include Route Object:");
		for (int i = 0; i < traversedVertexList.size(); i++) {
			objectInfo.append(traversedVertexList.get(i).toString());
		}
		objectInfo.append(">");

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {

		String headerInfo = this.getObjectHeader().binaryInformation();

		StringBuffer objectInfo = new StringBuffer();

		for (int i = 0; i < traversedVertexList.size(); i++) {
			objectInfo.append(traversedVertexList.get(i).binaryInformation());
		}

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		String EROName = "[" + NAME;
		String subObjectsName = new String();

		for (PCEPAddress address : traversedVertexList) {
			subObjectsName = subObjectsName + address.contentInformation();
		}

		return EROName + subObjectsName + "]";
	}

	public static void main(String[] args) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(7, 1, "1", "0");

		ArrayList<PCEPAddress> temp = new ArrayList<PCEPAddress>();
		temp.add(new PCEPAddress("192.168.1.2", false));
		temp.add(new PCEPAddress("192.168.1.3", false));
		PCEPExplicitRouteObjectImpl a = new PCEPExplicitRouteObjectImpl(objectHeader, temp);

		System.out.println(a.getObjectFrameBinaryString());

		String header = a.getObjectFrameBinaryString().substring(0, 32);

		PCEPCommonObjectHeader newHeader = new PCEPCommonObjectHeader(header);
		System.out.println(a.getObjectFrameBinaryString().substring(32).length());
		PCEPExplicitRouteObjectImpl b = new PCEPExplicitRouteObjectImpl(newHeader, a.getObjectFrameBinaryString().substring(32));
		System.out.println(b);

	}

}
