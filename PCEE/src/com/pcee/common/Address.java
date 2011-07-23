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

package com.pcee.common;

import java.util.StringTokenizer;

import com.pcee.protocol.message.PCEPComputationFactory;

public class Address{
	
	private String address;
	private int port;
	
	private String binaryAddress;

//	public void reset(String address, int port){
//		if (address.length() == 32) {
//			this.binaryAddress = address;
//			this.address = convertBinaryAddressToAddress(binaryAddress);
//		} else {
//			this.address = address;
//			this.port = port;
//			this.deserialize(address);
//			this.address = address+":"+port;
//		}		
//	}
	
	public Address(String address) {
		if (address.length() == 32) {
			this.binaryAddress = address;
			this.address = convertBinaryAddressToAddress(binaryAddress);
		} else {
			this.address = address;
			this.port = 0;
			this.deserialize(address);
		}
	}
	
	public Address(String address, int port) {
		if (address.length() == 32) {
			this.binaryAddress = address;
			this.address = convertBinaryAddressToAddress(binaryAddress);
		} else {
			this.address = address;
			this.port = port;
			this.deserialize(address);
		}
	}

	public String getAddress() {
		if(port != 0){
			return address  + ":" + port;
		}
		return address;
	}
	
	public String getAddressWithoutPort(){
		return address;
	}

	public String getBinaryAddress() {
		return binaryAddress;
	}

	/**
	 * Function to populate the address object from a string
	 */
	public void deserialize(String input) {
		StringBuffer addressBuffer = new StringBuffer();
		StringTokenizer addressTokenizer = new StringTokenizer(input, ".");
		int tokenLength = 8;

		while (addressTokenizer.hasMoreTokens()) {

			String currentAddressToken = addressTokenizer.nextToken();

			String nonRevertedbinaryString = Integer.toBinaryString(Integer.valueOf(currentAddressToken).intValue());
			String revertedBinaryString = PCEPComputationFactory.reverseBinaryString(nonRevertedbinaryString);
			String completeBinaryString = PCEPComputationFactory.appendZerosToBinaryString(revertedBinaryString, tokenLength);

			addressBuffer.append(completeBinaryString);
		}

		this.binaryAddress = addressBuffer.toString();

	}

	/**
	 * Function to convert the address object into a string
	 */
	public String serialize() {
		return binaryAddress;
	}

	public int compareTo(Address arg0) {

		if (this.binaryAddress == arg0.serialize()) {
			return 1;
		}

		return 0;
	}

	public int getAddressByteLength() {
		return binaryAddress.length() / 8;
	}

	private String convertBinaryAddressToAddress(String binaryAddress) {

		StringBuffer addressStringBuffer = new StringBuffer();

		while (!binaryAddress.isEmpty()) {
			addressStringBuffer.append(".");

			String token = binaryAddress.substring(0, 8);
			binaryAddress = binaryAddress.substring(8);

			String reversedToken = PCEPComputationFactory.reverseBinaryString(token);
			int intToken = Integer.valueOf(reversedToken, 2).intValue();
			String stringToken = Integer.toString(intToken);

			addressStringBuffer.append(stringToken);
		}

		// removes the first dot!
		String addressString = addressStringBuffer.toString().substring(1);

		return addressString;

	}

	public String binaryInformation() {
		String addressInfo = "[" + binaryAddress + "]";

		return addressInfo;
	}

	public String toString() {
		return getAddress() + "(" + getBinaryAddress() + ")";
	}
	
	public String contentInformation() {
		return "[Address]";
	}

}
