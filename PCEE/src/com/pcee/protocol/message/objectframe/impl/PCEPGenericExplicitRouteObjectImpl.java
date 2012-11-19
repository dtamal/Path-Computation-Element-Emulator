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

import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROUnnumberedInterface;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.LabelEROSubobject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.MLDelimiter;
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
public class PCEPGenericExplicitRouteObjectImpl extends PCEPExplicitRouteObject {

	private final String NAME = "Explicit Route Object";

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;
	private ArrayList<EROSubobjects> traversedVertexList = new ArrayList<EROSubobjects>();

	// private String subObjectsString;

	public PCEPGenericExplicitRouteObjectImpl(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPGenericExplicitRouteObjectImpl(PCEPCommonObjectHeader objectHeader, ArrayList<EROSubobjects> traversedVertexList) {
		this.setObjectHeader(objectHeader);
		this.setTraversedVertexList(traversedVertexList);
		this.updateHeaderLength();
	}

	private void updateHeaderLength(){
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
			EROSubobjects address = traversedVertexList.get(i);
			subObjectsStringBuffer.append(address.getObjectBinaryString());
		}

		return subObjectsStringBuffer.toString();
	}

	public void setObjectBinaryString(String binaryString) {

		ArrayList<EROSubobjects> vertexList = new ArrayList<EROSubobjects>();

		while (binaryString.length() > 0) {
			String lengthBinaryString = binaryString.substring(8, 16); 
			int length = (int) PCEPComputationFactory.getDecimalValue(lengthBinaryString);
			length = length * 8;
			String tempString = binaryString.substring(0, length);
			int type = (int) PCEPComputationFactory.getDecimalValue(tempString.substring(1, 8));
			EROSubobjects temp = null;
			if (type==EROSubobjects.PCEPIPv4AddressType) {
					temp=new PCEPAddress(tempString, true);
			} else if (type==EROSubobjects.PCEPUnnumberedInterfaceType) {
				temp = new EROUnnumberedInterface(tempString);
			} else if (type==EROSubobjects.PCEPMLDelimiterType) {
				temp = new MLDelimiter(tempString);
			} else if (type==EROSubobjects.PCEPLabelEROSubobjectType) {
				temp = LabelEROSubobject.getObjectFromBinaryString(tempString);
			}
			
			if (temp==null)
				System.out.println("[" + NAME + "] Problem in parsing the ERO Subobject in the setObjectBinaryString Function");
			
			vertexList.add(temp);

			binaryString = binaryString.substring(length);
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

	public void setTraversedVertexList(ArrayList<EROSubobjects> subObjects) {
		this.traversedVertexList = subObjects;
	}

	public ArrayList<EROSubobjects> getTraversedVertexList() {
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

		for (EROSubobjects address : traversedVertexList) {
			if (address instanceof PCEPAddress)
				traversedVertexesList = traversedVertexesList + ((PCEPAddress)address).getIPv4Address(false) + "-";
			else if (address instanceof EROUnnumberedInterface){
				traversedVertexesList = traversedVertexesList + "UI-" + ((EROUnnumberedInterface)address).getRouterIDDecimalValue() + ":" + ((EROUnnumberedInterface)address).getInterfaceIDDecimalValue() + "-";
			}
			else if (address instanceof MLDelimiter){
				traversedVertexesList = traversedVertexesList + "ML-";				
			}
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

		for (EROSubobjects address : traversedVertexList) {
			subObjectsName = subObjectsName + address.contentInformation();
		}

		return EROName + subObjectsName + "]";
	}

	public static void main(String[] args){
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(7, 1, "1", "0");

		ArrayList<EROSubobjects> temp= new ArrayList<EROSubobjects>();
		temp.add(new PCEPAddress("192.168.1.2", false));
		temp.add(new PCEPAddress("192.168.1.3", false));
		temp.add(new MLDelimiter());
		PCEPGenericExplicitRouteObjectImpl a = new PCEPGenericExplicitRouteObjectImpl (objectHeader, temp);

		System.out.println(a.getObjectFrameBinaryString());

		String header = a.getObjectFrameBinaryString().substring(0, 32);


		PCEPCommonObjectHeader newHeader = new PCEPCommonObjectHeader(header);
		System.out.println(a.getObjectFrameBinaryString().substring(32).length());
		PCEPGenericExplicitRouteObjectImpl b = new PCEPGenericExplicitRouteObjectImpl (newHeader, a.getObjectFrameBinaryString().substring(32));
		System.out.println(b);


	}

}
