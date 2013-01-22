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

import java.util.StringTokenizer;
import com.pcee.protocol.message.PCEPComputationFactory;
/**
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |L|    Type     |     Length    | IPv4 address (4 bytes)        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | IPv4 address (continued)      | Prefix Length |      Resvd    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * <pre>
 */
public class PCEPAddress extends EROSubobjects {
	
	private String IPv4Address;
	private String prefixLength;
	private String reserved;

	private int IPv4AddressStartBit = 16;
	private int IPv4AddressEndBit = 47;
	private int IPv4AddressLength = 32;

	private int prefixLengthStartBit = 48;
	private int prefixLengthEndBit = 55;
	private int prefixLengthLength = 8;

	private int reservedStartBit = 56;
	private int reservedEndBit = 63;
	private int reservedLength = 8;

	private int port; // Not serialized, kind of an attachment

	// Only used in PCEPEndPointsObject for 32 bit input
	/**
	 * @param binaryString
	 */
	public PCEPAddress(String binaryString) {
		NAME="PCEPAddress";
		if (binaryString.length() == 32) {
			this.setLFlagDecimalValue(0);
			this.setTypeDecimalValue(EROSubobjects.PCEPIPv4AddressType);
			this.setLengthDecimalValue(8);
			this.setIPv4AddressBinaryString(binaryString, true);
			this.setPrefixLengthDecimalValue(32);
			this.setReservedDecimalValue(0);
		} else {

			this.deserialize(binaryString);
		}
		this.port = 4189;
	}

	/**
	 * @param address
	 * @param binaryRepresentation
	 */
	public PCEPAddress(String address, boolean binaryRepresentation) {
		NAME="PCEPAddress";
		if (address.length() == 32) {
			System.out.println("WTF");
		}

		if (binaryRepresentation) {
			deserialize(address);
		} else {

			this.setLFlagDecimalValue(0);
			this.setTypeDecimalValue(EROSubobjects.PCEPIPv4AddressType);
			this.setLengthDecimalValue(8);

			this.setIPv4AddressBinaryString(address, binaryRepresentation);

			this.setPrefixLengthDecimalValue(32);
			this.setReservedDecimalValue(0);
			this.port = 4189;
		}
	}

	/**
	 * @param address
	 * @param port
	 */
	public PCEPAddress(String address, int port) {
		NAME="PCEPAddress";
		this.setLFlagDecimalValue(0);
		this.setTypeDecimalValue(EROSubobjects.PCEPIPv4AddressType);
		this.setLengthDecimalValue(8);
		this.setIPv4AddressBinaryString(address, false);
		this.setPrefixLengthDecimalValue(32);
		this.setReservedDecimalValue(0);
		this.port = port;
	}

	public static void main(String[] args) {
		PCEPAddress a = new PCEPAddress("192.168.1.2", false);
		System.out.println(a);
		System.out.println(a.binaryInformation());
		System.out.println(a.serialize());
		System.out.println(a.getByteLength());
		System.out.println("====");
		PCEPAddress b = new PCEPAddress(a.serialize());
		System.out.println(b);
		System.out.println(b.binaryInformation());
		System.out.println(b.serialize());
	}

	/**
	 * Object
	 */
	public String serialize() {
		String binaryString = lFlag + type + length + IPv4Address + prefixLength + reserved;
		return binaryString;
	}

	/**
	 * @param binaryString
	 */
	public void deserialize(String binaryString) {
		String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);
		String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
		String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);
		String IPv4AddressBinaryString = binaryString.substring(IPv4AddressStartBit, IPv4AddressEndBit + 1);
		String prefixLengthBinaryString = binaryString.substring(prefixLengthStartBit, prefixLengthEndBit + 1);
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);

		this.setLFlagBinaryString(lFlagBinaryString);
		this.setTypeBinaryString(typeBinaryString);
		this.setLengthBinaryString(lengthBinaryString);
		this.setIPv4AddressBinaryString(IPv4AddressBinaryString, true);
		this.setPrefixLengthBinaryString(prefixLengthBinaryString);
		this.setReservedBinaryString(reservedBinaryString);
	}

	/* (non-Javadoc)
	 * @see com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects#getByteLength()
	 */
	public int getByteLength() {
		int objectLength = lFlag.length() + prefixLength.length() + type.length() + length.length() + IPv4Address.length() + reserved.length();
		int objectFrameByteLength = objectLength / 8;

		return objectFrameByteLength;
	}

	/**
	 * IPv4Address
	 */
	public String getIPv4Address(boolean withPort) {

		String address = convertBinaryAddressToAddress(IPv4Address);

		if (withPort) {
			return address + ":" + port;
		}

		return address;
	}

	/**
	 * @return String
	 */
	public String getIPv4Address() {
		String address = convertBinaryAddressToAddress(IPv4Address) + ":" + port;

		return address;
	}

	/**
	 * @return
	 */
	public String getIPv4BinaryAddress() {
		return IPv4Address;
	}

	public void setIPv4AddressBinaryString(String binaryString, boolean binaryRepresentation) {

		if (binaryRepresentation) {
			String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, IPv4AddressLength);
			this.IPv4Address = checkedBinaryString;
		} else {
			String address = convertAddressToBinaryAddress(binaryString);
			String checkedBinaryString = PCEPComputationFactory.setBinaryString(address, IPv4AddressLength); //TODO CHECK
			this.IPv4Address = checkedBinaryString;
		}

	}

	/**
	 * prefixLength
	 */
	public int getPrefixLengthDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(prefixLength);
		return decimalValue;
	}

	/**
	 * @return
	 */
	public String getPrefixLengthBinaryString() {
		return this.prefixLength;
	}

	/**
	 * @param decimalValue
	 */
	public void setPrefixLengthDecimalValue(int decimalValue) {
		int binaryLength = prefixLengthLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.prefixLength = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	/**
	 * @param binaryString
	 */
	public void setPrefixLengthBinaryString(String binaryString) {
		this.prefixLength = binaryString;
	}

	/**
	 * reserved
	 */
	public int getReservedDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(reserved);
		return decimalValue;
	}

	/**
	 * @return
	 */
	public String getReservedBinaryString() {
		return this.reserved;
	}

	/**
	 * @param decimalValue
	 */
	public void setReservedDecimalValue(int decimalValue) {
		int binaryLength = reservedLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.reserved = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	/**
	 * @param binaryString
	 */
	public void setReservedBinaryString(String binaryString) {
		this.reserved = binaryString;
	}

	/**
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * HELPER
	 */

	/**
	 * Transforms x.x.x.x schema in binaryAddress
	 * 
	 * @param x
	 *            .x.x.x schema will be transformed in binaryAddress
	 */
	private String convertAddressToBinaryAddress(String input) {
		StringBuffer addressBuffer = new StringBuffer();
		StringTokenizer addressTokenizer = new StringTokenizer(input, ".");
		int tokenLength = 8;

		while (addressTokenizer.hasMoreTokens()) {

			String currentAddressToken = addressTokenizer.nextToken();

			String nonRevertedbinaryString = Integer.toBinaryString(Integer.valueOf(currentAddressToken).intValue());
			String completeBinaryString = PCEPComputationFactory.appendZerosToBinaryString(nonRevertedbinaryString, tokenLength);

			addressBuffer.append(completeBinaryString);
		}

		return addressBuffer.toString();

	}

	/**
	 * @param binaryAddress
	 * @return
	 */
	private String convertBinaryAddressToAddress(String binaryAddress) {
		StringBuffer addressStringBuffer = new StringBuffer();

		while (!binaryAddress.isEmpty()) {
			addressStringBuffer.append(".");

			String token = binaryAddress.substring(0, 8);
			binaryAddress = binaryAddress.substring(8);

			String reversedToken = token;
			int intToken = Integer.valueOf(reversedToken, 2).intValue();
			String stringToken = Integer.toString(intToken);

			addressStringBuffer.append(stringToken);
		}

		// removes the first dot!
		String addressString = addressStringBuffer.toString().substring(1);

		return addressString;

	}

	/**
	 * OUTPUT
	 */
	public String toString() {
		String lFlagInfo = "lFlag=" + this.getLFlagDecimalValue();
		String typeInfo = ",type=" + this.getTypeDecimalValue();
		String lengthInfo = ",length=" + this.getLengthDecimalValue();
		String IPv4AddressInfo = ",IPv4Address=" + this.getIPv4Address(false);
		String prefixLengthInfo = ",prefixLength=" + this.getPrefixLengthDecimalValue();
		String reservedInfo = " Reserved=" + this.getReservedDecimalValue();

		String objectInfo = NAME + ":" + lFlagInfo + typeInfo + lengthInfo + IPv4AddressInfo + prefixLengthInfo + reservedInfo + ">";

		return objectInfo;
	}

	/* (non-Javadoc)
	 * @see com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects#binaryInformation()
	 */
	public String binaryInformation() {
		String lFlagBinaryInfo = getLFlagBinaryString();
		String typeBinaryInfo = "'" + getTypeBinaryString();
		String lengthBinaryInfo = "'" + getLengthBinaryString();
		String IPv4AddressBinaryInfo = "'" + getIPv4BinaryAddress();
		String prefixLengthInfo = "'" + this.getPrefixLengthBinaryString();
		String reservedInfo = "'" + getReservedBinaryString();

		String objectInfo = "[" + lFlagBinaryInfo + typeBinaryInfo + lengthBinaryInfo + IPv4AddressBinaryInfo + prefixLengthInfo + reservedInfo + "]";

		return objectInfo;
	}



	/* (non-Javadoc)
	 * @see com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects#getObjectBinaryString()
	 */
	public String getObjectBinaryString() {
		return this.serialize();
	}

	/* (non-Javadoc)
	 * @see com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects#setObjectBinaryString(java.lang.String)
	 */
	public void setObjectBinaryString(String binaryString) {
		this.deserialize(binaryString);
	}
}
