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
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROUnnumberedInterface;
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
public class PCEPExplicitRouteObjectWSON extends PCEPExplicitRouteObject {

	private final String NAME = "Explicit Route Object WSON";

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;
	private ArrayList<PCEPAddress> traversedVertexList;
	private ArrayList<EROUnnumberedInterface> interfaceList;

	// private String subObjectsString;

	public PCEPExplicitRouteObjectWSON(PCEPCommonObjectHeader objectHeader, String binaryString) {
		traversedVertexList = new ArrayList<PCEPAddress>();
		interfaceList = new ArrayList<EROUnnumberedInterface>();

		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPExplicitRouteObjectWSON(PCEPCommonObjectHeader objectHeader, ArrayList<PCEPAddress> travVertexList) {
		traversedVertexList = new ArrayList<PCEPAddress>();
		interfaceList = new ArrayList<EROUnnumberedInterface>();

		ArrayList<EROUnnumberedInterface> interfList = new ArrayList<EROUnnumberedInterface>();
		for (int i = 0; i < travVertexList.size(); i++) { // hack
			EROUnnumberedInterface e = new EROUnnumberedInterface(false, 1, 3);
			interfList.add(e);
		}
		this.setObjectHeader(objectHeader);
		this.setTraversedVertexList(travVertexList);
		this.setInterfaceList(interfList);
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

		for (int i = 0; i < interfaceList.size(); i++) {
			EROUnnumberedInterface interf = interfaceList.get(i);
			subObjectsStringBuffer.append(interf.getObjectBinaryString());
		}

		for (int i = 0; i < traversedVertexList.size(); i++) {
			PCEPAddress address = traversedVertexList.get(i);
			subObjectsStringBuffer.append(address.serialize());
		}

		return subObjectsStringBuffer.toString();
	}

	public void setObjectBinaryString(String binaryString) {

		ArrayList<PCEPAddress> vertexList = new ArrayList<PCEPAddress>();
		ArrayList<EROUnnumberedInterface> interfList = new ArrayList<EROUnnumberedInterface>();

		while (binaryString.length() > 0) {

			if (binaryString.length() >= 96) {
				System.out.println("Cutting the first 96 bits");
				String eroUnnumberedInterfaceString = binaryString.substring(0, 96);
				EROUnnumberedInterface e = new EROUnnumberedInterface(eroUnnumberedInterfaceString);
				interfList.add(e);

				binaryString = binaryString.substring(96);
			} else {
				System.out.println("Cutting the last 64 bits");
				String addressBinaryString = binaryString.substring(0, 64); // FIXME
				PCEPAddress address = new PCEPAddress(addressBinaryString, true);
				vertexList.add(address);

				binaryString = binaryString.substring(64);

			}

		}

		this.setTraversedVertexList(vertexList);
		this.setInterfaceList(interfList);

	}

	public int getObjectFrameByteLength() {
		int objectsBinaryLength = this.getTraversedVertexListBinaryLength() + this.getInterfaceListBinaryLength();
		int headerLength = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH / 8;
		int objectFrameByteLength = objectsBinaryLength + headerLength;

		return objectFrameByteLength;
	}

	public String getObjectFrameBinaryString() {
		String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
		String objectBinaryString = this.getObjectBinaryString();

		return headerBinaryString + objectBinaryString;
	}

	/**
	 * TraversedVertexes
	 */
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

	public String getTraversedVertexes() {
		String traversedVertexesList = new String();

		for (PCEPAddress address : traversedVertexList) {
			traversedVertexesList = traversedVertexesList + address.getIPv4Address(false) + "-";
		}

		return traversedVertexesList;
	}

	/**
	 * Interfaces
	 */
	public void setInterfaceList(ArrayList<EROUnnumberedInterface> subObjects) {
		this.interfaceList = subObjects;
	}

	public ArrayList<EROUnnumberedInterface> getInterfaceList() {
		return this.interfaceList;
	}

	private int getInterfaceListBinaryLength() {
		int length = 0;
		for (int i = 0; i < interfaceList.size(); i++) {
			length += ((interfaceList.get(i).getByteLength()));
		}
		return length;
	}

	public String printPath() {
		String path = new String();

		for (EROUnnumberedInterface interf : interfaceList) {
			String routerID = interf.getRouterIDDecimalValue();
			String itf = String.valueOf(interf.getInterfaceIDDecimalValue());

			path += routerID + ":" + itf + "-";
		}
		for (PCEPAddress address : traversedVertexList) {
			String a = address.getIPv4Address(false);
			path += a;
		}

		return path;
	}

	/**
	 * Output
	 */

	public String toString() {
		String info = "[ERO:";

		for (int i = 0; i < interfaceList.size(); i++) {
			info += " UIntf ";
		}

		for (int i = 0; i < traversedVertexList.size(); i++) {
			info += " Address ";
		}
		return info + "]";
	}

	public String binaryInformation() {

		String headerInfo = this.getObjectHeader().binaryInformation();

		StringBuffer objectInfo = new StringBuffer();

		for (int i = 0; i < interfaceList.size(); i++) {
			objectInfo.append(interfaceList.get(i).binaryInformation());
		}

		for (int i = 0; i < traversedVertexList.size(); i++) {
			objectInfo.append(traversedVertexList.get(i).binaryInformation());
		}

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		String EROName = "[" + NAME;
		String subObjectsName = new String();

		for (EROUnnumberedInterface intf : interfaceList) {
			subObjectsName = subObjectsName + intf.contentInformation();
		}
		for (PCEPAddress address : traversedVertexList) {
			subObjectsName = subObjectsName + address.contentInformation();
		}

		return EROName + subObjectsName + "]";
	}

	public static void main(String[] args) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(7, 1, "1", "0");

		ArrayList<PCEPAddress> temp = new ArrayList<PCEPAddress>();
		temp.add(new PCEPAddress("192.168.1.2", false));
		// temp.add(new PCEPAddress("192.168.1.3", false));
		PCEPExplicitRouteObjectWSON e1 = new PCEPExplicitRouteObjectWSON(objectHeader, temp);

		System.out.println("e1: " + e1.getObjectFrameBinaryString());
		String header = e1.getObjectFrameBinaryString().substring(0, 32);
		String object = e1.getObjectFrameBinaryString().substring(32);

		System.out.println(header.length());
		System.out.println(object.length());

		PCEPCommonObjectHeader newHeader = new PCEPCommonObjectHeader(header);

		PCEPExplicitRouteObjectWSON e2 = new PCEPExplicitRouteObjectWSON(newHeader, object);
		System.out.println("e2: " + e2.getObjectFrameBinaryString());

	}

}
