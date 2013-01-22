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

package com.pcee.protocol.message.objectframe.impl.erosubobjects;


import com.pcee.protocol.message.PCEPComputationFactory;

/**
 * <pre>
 *    0                   1                   2                   3
 *    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |L|    Type     |     Length    |    Reserved (MUST be zero)    |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |                           Router ID                           |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |                     Interface ID (32 bits)                    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * <pre>
 */

public class EROUnnumberedInterface extends EROSubobjects {

	private String reserved;
	private String routerID;
	private String interfaceID; // in bits

	
	private int reservedStartBit = 16;
	private int reservedEndBit = 31;
	private int reservedLength = 16;



	private int routerIDStartBit = 32;
	private int routerIDEndBit = 63;
	private int routerIDLength = 32;

	private int interfaceIDStartBit = 64;
	private int interfaceIDEndBit = 95;
	private int interfaceIDLength = 32;

	public EROUnnumberedInterface(String binaryString) {
		NAME= "EROUnnumberedInterface";
		this.setObjectBinaryString(binaryString);
	}

	public EROUnnumberedInterface(boolean isLooseHop, int routerID, int interfaceID) {
		NAME= "EROUnnumberedInterface";
		this.setLFlag(isLooseHop);
		this.setTypeDecimalValue(EROSubobjects.PCEPUnnumberedInterfaceType);
		this.setLengthDecimalValue(12);
		this.setReservedDecimalValue(0);
		this.setRouterIDDecimalValue(routerID);
		this.setInterfaceIDDecimalValue(interfaceID);
	}

	public static void main(String[] args) {
		EROUnnumberedInterface e = new EROUnnumberedInterface(true, 1073741825, 1073741825);
		String eBinary = e.getObjectBinaryString();
		EROUnnumberedInterface e2 = new EROUnnumberedInterface(eBinary);

		System.out.println(e.binaryInformation());
		System.out.println(e2.binaryInformation());

		System.out.println(e.toString());
		System.out.println(e2.toString());

	}

	/**
	 * Object
	 */
	public String getObjectBinaryString() {
		String binaryString = lFlag + type + length + reserved + routerID + interfaceID;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);
		String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
		String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
		String routerIDBinaryString = binaryString.substring(routerIDStartBit, routerIDEndBit + 1);
		String interfaceIDBinaryString = binaryString.substring(interfaceIDStartBit, interfaceIDEndBit + 1);

		this.setLFlagBinaryString(lFlagBinaryString);
		this.setTypeBinaryString(typeBinaryString);
		this.setLengthBinaryString(lengthBinaryString);
		this.setReservedBinaryString(reservedBinaryString);
		this.setRouterIDBinaryString(routerIDBinaryString);
		this.setInterfaceIDBinaryString(interfaceIDBinaryString);
	}

	public int getByteLength() {
		int objectLength = lFlag.length() + type.length() + length.length() + reserved.length() + routerID.length() + interfaceID.length();
		int objectFrameByteLength = objectLength / 8;

		return objectFrameByteLength;
	}

	 /**
	  * reserved
	  */
	 public int getReservedDecimalValue() {
		 int decimalValue = (int) PCEPComputationFactory.getDecimalValue(reserved);
		 return decimalValue;
	 }

	 public String getReservedBinaryString() {
		 return this.reserved;
	 }

	 public void setReservedDecimalValue(int decimalValue) {
		 int binaryLength = reservedLength;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 this.reserved = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	 }

	 public void setReservedBinaryString(String binaryString) {
		 // String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		 this.reserved = binaryString;
	 }



	
	/**
	 * RouterID
	 */

	public String getRouterIDBinaryString() {
		return routerID;
	}

	public String getRouterIDDecimalValue(){
		String address = PCEPComputationFactory.convertBinaryAddressToAddress(routerID);
		return address;
	}

	public void setRouterIDBinaryString(String binaryString) {
		// String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, routerIDLength);
		this.routerID = binaryString;
	}

	public void setRouterIDDecimalValue(int decimalValue) {
		int binaryLength = routerIDLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength-1);

		this.routerID = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	/**
	 * InterfaceID
	 */

	public String getInterfaceIDBinaryString() {
		return this.interfaceID;
	}

	public int getInterfaceIDDecimalValue(){
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(interfaceID);
		return decimalValue;
	}

	public void setInterfaceIDBinaryString(String binaryString) {
		// String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, interfaceIDLength);
		this.interfaceID = binaryString;
	}

	public void setInterfaceIDDecimalValue(int decimalValue) {
		int binaryLength = interfaceIDLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength-1);

		this.interfaceID = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	/**
	 * OUTPUT
	 */
	public String toString() {
		String lFlagInfo = "lFlag=" + this.getLFlagDecimalValue();
		String typeInfo = ", type=" + this.getTypeDecimalValue();
		String lengthInfo = ", length=" + this.getLengthDecimalValue();
		String reservedInfo = ", Reserved=" + this.getReservedDecimalValue();
		String routerIDInfo = ", routerID =" + this.getRouterIDBinaryString();
		String interfaceIDInfo = ", interfaceID=" + this.getInterfaceIDBinaryString();

		String objectInfo = NAME + ":" + lFlagInfo + typeInfo + lengthInfo + reservedInfo + routerIDInfo + interfaceIDInfo + ">";

		return objectInfo;
	}

	public String binaryInformation() {
		String lFlagBinaryInfo = getLFlagBinaryString();
		String typeBinaryInfo = "'" + getTypeBinaryString();
		String lengthBinaryInfo = "'" + getLengthBinaryString();
		String reservedInfo = "'" + getReservedBinaryString();
		String routerIDBinaryInfo = "'" + getRouterIDBinaryString();
		String interfaceIDInfo = "'" + this.getInterfaceIDBinaryString();

		String objectInfo = "[" + lFlagBinaryInfo + typeBinaryInfo + lengthBinaryInfo + reservedInfo  + routerIDBinaryInfo + interfaceIDInfo + "]";

		return objectInfo;
	}


}
