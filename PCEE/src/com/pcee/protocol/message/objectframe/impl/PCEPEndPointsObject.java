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

import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

/**
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                     Source IPv4 address                       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                  Destination IPv4 address                     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPEndPointsObject implements PCEPObjectFrame {

	private final String NAME = "End-Points";

	private String sourceAddress;
	private String destinationAddress;

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;

	private int sourceAddressStartBit = PCEPConstantValues.END_POINTS_OBJECT_SOURCE_ADDRESS_START_BIT;
	private int sourceAddressEndBit = PCEPConstantValues.END_POINTS_OBJECT_SOURCE_ADDRESS_END_BIT;
	private int sourceAddressLength = PCEPConstantValues.END_POINTS_OBJECT_SOURCE_ADDRESS_LENGTH;

	private int destinationAddressStartBit = PCEPConstantValues.END_POINTS_OBJECT_DESTINATION_ADDRESS_START_BIT;
	private int destinationAddressEndBit = PCEPConstantValues.END_POINTS_OBJECT_DESTINATION_ADDRESS_END_BIT;
	private int destinationAddressLength = PCEPConstantValues.END_POINTS_OBJECT_DESTINATION_ADDRESS_LENGTH;

	public PCEPEndPointsObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPEndPointsObject(PCEPCommonObjectHeader objectHeader, PCEPAddress sourceAddress, PCEPAddress destinationAddress) {
		this.setObjectHeader(objectHeader);
		this.setSourceAddressBinaryString(sourceAddress.getIPv4BinaryAddress());
		this.setDestinationAddressBinaryString(destinationAddress.getIPv4BinaryAddress());
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
		String binaryString = sourceAddress + destinationAddress;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String sourceAddressBinaryString = binaryString.substring(sourceAddressStartBit, sourceAddressEndBit + 1);
		String destinationAddressBinaryString = binaryString.substring(destinationAddressStartBit, destinationAddressEndBit + 1);

		this.setSourceAddressBinaryString(sourceAddressBinaryString);
		this.setDestinationAddressBinaryString(destinationAddressBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = sourceAddress.length() + destinationAddress.length();
		int headerLength = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH;
		int objectFrameByteLength = (objectLength + headerLength) / 8;
		return objectFrameByteLength;
	}

	public String getObjectFrameBinaryString() {
		String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
		String objectBinaryString = this.getObjectBinaryString();

		return headerBinaryString + objectBinaryString;
	}

	/**
	 * sourceAddress
	 */
	// public int getSourceAddressDecimalValue() {
	// int decimalValue = (int) getDecimalValue(sourceAddress);
	// return decimalValue;
	// }
	public String getSourceAddressBinaryString() {
		return this.sourceAddress;
	}

	// public void setSourceAddressDecimalValue(int decimalValue) {
	// int binaryLength = sourceAddressLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.sourceAddress = this.setDecimalValue(decimalValue, maxValue,
	// binaryLength);
	// }
	public void setSourceAddressBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, sourceAddressLength);
		this.sourceAddress = checkedBinaryString;
	}

	/*
	 * public void setSourceAddressBinaryString(int startingBit, String
	 * binaryString) { String checkedBinaryString =
	 * PCEPComputationFactory.setBinaryString(sourceAddress, startingBit,
	 * binaryString, sourceAddressLength); this.sourceAddress =
	 * checkedBinaryString; }
	 */
	/**
	 * destinationAddress
	 */
	// public int getDestinationAddressDecimalValue() {
	// int decimalValue = (int) getDecimalValue(destinationAddress);
	// return decimalValue;
	// }
	public String getDestinationAddressBinaryString() {
		return this.destinationAddress;
	}

	// public void setDestinationAddressDecimalValue(int decimalValue) {
	// int binaryLength = destinationAddressLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.destinationAddress = this.setDecimalValue(decimalValue, maxValue,
	// binaryLength);
	// }
	public void setDestinationAddressBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, destinationAddressLength);
		this.destinationAddress = checkedBinaryString;
	}

	public void setDestinationAddressBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(destinationAddress, startingBit, binaryString, destinationAddressLength);
		this.destinationAddress = checkedBinaryString;
	}

	public String toString() {

		PCEPAddress sourceAddress = new PCEPAddress(this.getSourceAddressBinaryString());
		PCEPAddress destinationAddress = new PCEPAddress(this.getDestinationAddressBinaryString());

		String sourceAddressInfo = "SourceAddress=" + sourceAddress.getIPv4Address(false);
		String destinationAddressInfo = ",DestinationAddress=" + destinationAddress.getIPv4Address(false);

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "<End-Points:" + sourceAddressInfo + destinationAddressInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String sourceAddressBinaryInfo = getSourceAddressBinaryString();
		String destinationAddressBinaryInfo = "'" + getDestinationAddressBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + sourceAddressBinaryInfo + destinationAddressBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
