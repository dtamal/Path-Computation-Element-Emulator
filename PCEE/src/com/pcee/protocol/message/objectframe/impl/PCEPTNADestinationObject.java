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
 * |              Type            |            Length              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Addr Length   |                Reserved                       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Destination IP Address                     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PCEPTNADestinationObject implements PCEPObjectFrame {

	private final String NAME = "TNA-Source";

	private String type;
	private String length;
	private String addrLength;
	private String reserved;
	private String destinationIP;
	private PCEPCommonObjectHeader objectHeader;

	private int typeStartBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_TYPE_START_BIT;
	private int typeEndBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_TYPE_END_BIT;
	private int typeLength = PCEPConstantValues.TNA_DESTINATION_OBJECT_TYPE_LENGTH;

	private int lengthStartBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_LENGTH_START_BIT;
	private int lengthEndBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_LENGTH_END_BIT;
	private int lengthLength = PCEPConstantValues.TNA_DESTINATION_OBJECT_LENGTH_LENGTH;

	private int addrLengthStartBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_ADDRESS_LENGTH_START_BIT;
	private int addrLengthEndBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_ADDRESS_LENGTH_END_BIT;
	private int addrLengthLength = PCEPConstantValues.TNA_DESTINATION_OBJECT_ADDRESS_LENGTH_LENGTH;

	private int reservedStartBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.TNA_DESTINATION_OBJECT_RESERVED_LENGTH;

	private int destinationIPStartBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_DEST_IP_START_BIT;
	private int destinationIPEndBit = PCEPConstantValues.TNA_DESTINATION_OBJECT_DEST_IP_END_BIT;
	private int destinationIPLength = PCEPConstantValues.TNA_DESTINATION_OBJECT_DEST_IP_LENGTH;

	public PCEPTNADestinationObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPTNADestinationObject(PCEPCommonObjectHeader objectHeader, int type, int length, int addrLength, int reserved, PCEPAddress destinationIP) {
		this.setObjectHeader(objectHeader);
		this.setTypeBinaryString(Integer.toBinaryString(type));
		this.setLengthBinaryString(Integer.toBinaryString(length));
		this.setAddrLengthBinaryString(Integer.toBinaryString(addrLength));
		this.setReservedBinaryString(Integer.toBinaryString(reserved));
		this.setDestinationIPBinaryString(destinationIP.getIPv4BinaryAddress());
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
		String binaryString = type + length + addrLength + reserved + destinationIP;
		return binaryString;
	}

	public int getObjectFrameByteLength() {
		int objectLength = type.length() + length.length() + addrLength.length() + reserved.length() + destinationIP.length();
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
	 * length
	 */
	public String getLengthBinaryString() {
		return this.length;
	}

	public void setLengthBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, lengthLength);
		this.length = checkedBinaryString;
	}

	/**
	 * type
	 */
	public String getTypeBinaryString() {
		return this.type;
	}

	public void setTypeBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, typeLength);
		this.type = checkedBinaryString;
	}

	public void setTypeBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(type, startingBit, binaryString, typeLength);
		this.type = checkedBinaryString;
	}

	/**
	 * 
	 * Address Length
	 */
	public String getAddrLengthBinaryString() {
		return this.addrLength;
	}

	public void setAddrLengthBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, addrLengthLength);
		this.addrLength = checkedBinaryString;
	}

	/**
	 * reserved
	 */
	public String getReservedBinaryString() {
		return this.reserved;
	}

	public void setReservedBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		this.reserved = checkedBinaryString;
	}

	/**
	 * destination IP address
	 */
	public String getDestinationIPBinaryString() {
		return this.destinationIP;
	}

	public void setDestinationIPBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, destinationIPLength);
		this.destinationIP = checkedBinaryString;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

	@Override
	public String binaryInformation() {

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + this.getTypeBinaryString() + " ' " + this.getLengthBinaryString() + " ' " + this.getAddrLengthBinaryString() + " ' " + this.getReservedBinaryString() + " ' " + this.getDestinationIPBinaryString() + "]";

		return headerInfo + objectInfo;
	}

	public String toString() {

		PCEPAddress destinationAddress = new PCEPAddress(this.getDestinationIPBinaryString());

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "PCEPTNASourceObject : TYPE-" + Integer.valueOf(this.type, 2) + ", LENGTH-" + Integer.valueOf(this.length, 2) + ", AddressLength-" + Integer.valueOf(this.addrLength, 2) + ", Reserved-" + Integer.valueOf(this.reserved, 2) + ", Source Adresse-" + destinationAddress.getIPv4Address(false);
		return headerInfo + objectInfo;
	}

	@Override
	public void setObjectBinaryString(String objectString) {
		String typeBinaryString = objectString.substring(typeStartBit, typeEndBit + 1);
		String lengthBinaryString = objectString.substring(lengthStartBit, lengthEndBit + 1);
		String addrLengthBinaryString = objectString.substring(addrLengthStartBit, addrLengthEndBit + 1);
		String reservedBinaryString = objectString.substring(reservedStartBit, reservedEndBit + 1);
		String destinationIPBinaryString = objectString.substring(destinationIPStartBit, destinationIPEndBit + 1);

		this.setTypeBinaryString(typeBinaryString);
		this.setLengthBinaryString(lengthBinaryString);
		this.setAddrLengthBinaryString(addrLengthBinaryString);
		this.setReservedBinaryString(reservedBinaryString);
		this.setDestinationIPBinaryString(destinationIPBinaryString);
	}

}
