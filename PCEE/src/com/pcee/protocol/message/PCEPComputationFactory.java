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

package com.pcee.protocol.message;

import java.util.StringTokenizer;

import com.pcee.logger.Logger;

/**
 * Super class for all header and objects
 * 
 * @author Marek Drogon
 */
public class PCEPComputationFactory {

	public static byte[] rawMessageToByteArray(String rawMessage) {

		int byteArrayLength = rawMessage.length() / 8;
		byte[] byteArray = new byte[byteArrayLength];
		String rawMessageString = rawMessage;

		for (int i = 0; i < byteArrayLength; i++) {
			String bits = rawMessageString.substring(0, 8);
			rawMessageString = rawMessageString.substring(8);

			byteArray[i] = binaryStringToByteConverter(bits);
		}

		return byteArray;

	}

	public static String byteArrayToRawMessage(byte[] byteArray) {

		String rawMessageString = new String();

		for (int i = 0; i < byteArray.length; i++) {
			rawMessageString = rawMessageString
					+ byteToBinaryStringConverter(byteArray[i]);
		}

		return rawMessageString;
	}

	public static byte binaryStringToByteConverter(String rawString) {

		char[] bitArray = rawString.toCharArray();
		int x = 0x00;

		for (int i = 0; i < 8; i++) {
			if (bitArray[i] == '1') {
				x = x << 1;
				x = x + 0x0001;
			} else
				x = x << 1;
		}

		return (byte) x;
	}

	public static String byteToBinaryStringConverter(byte bits) {

		char[] bitArray = new char[8];

		int y = (int) bits;

		for (int i = 0; i < 8; i++) {
			if ((y & 0x0001) == 0x0001) {
				bitArray[7 - i] = '1';
			} else
				bitArray[7 - i] = '0';
			y = y >>> 1;
		}

		return new String(bitArray);
	}

	public static byte binaryStringToByteConverter2(String rawString) {

		char[] bitArray = rawString.toCharArray();
		int x = 0;

		for (int i = 0; i < 7; i++) {
			if (bitArray[i] == '1')
				x += 1 << i;
		}

		if (bitArray[7] == '1')
			x = x - 128;

		return (byte) x;
	}

	public static String byteToBinaryStringConverter2(byte bits) {

		char[] bitArray = { '0', '0', '0', '0', '0', '0', '0', '0' };
		int x = 0;
		int i = 0;

		if (bits < 0) {
			bitArray[7] = '1';
			x = 128 + bits;
		} else {
			bitArray[7] = '0';
			x = bits;
		}

		while (x != 0) {

			if (x % 2 == 1)
				bitArray[i] = '1';
			else
				bitArray[i] = '0';

			x /= 2;
			i++;
		}

		return new String(bitArray);
	}

	public static long getDecimalValue(String headerMember) {
		long x = 0;
		for (int i = 0; i < headerMember.length(); i++) {
			x += Integer.parseInt(headerMember.substring(headerMember.length()
					- 1 - i, headerMember.length() - i))
					* Math.pow(2, i);
		}

		return x;
	}
	
	public static void main(String[] args){
		System.out.println("value of 01010101 : " + getDecimalValue("01010101"));
		System.out.println("value of 01010101 using Integer.valueOf : " + Integer.valueOf("01010101", 2));
	}

	public static String getBinaryString(String headerMember,
			int headerMemberBitIndex) {
		return Character.toString(headerMember.charAt(headerMemberBitIndex));
	}

	public static String setDecimalValue(long decimalValue,
			long headerMemberMaxValue, int headerMemberLength) {
		long checkedDecimalValue = checkInputDecimalValue(decimalValue,
				headerMemberMaxValue);

		String binaryString = Long.toString(checkedDecimalValue, 2);

		return checkInputBinaryString(binaryString, headerMemberLength);
	}

	public static String setBinaryString(String binaryString,
			int headerMemberLength) {
		return checkInputBinaryString(binaryString, headerMemberLength);
	}

	public static String setBinaryString(String headerMember, int startingBit,
			String binaryString, int headerMemberLength) {

		int checkedStartingBit = checkInputStartingBit(startingBit,
				headerMemberLength);
		int binaryStringLength = binaryString.length();
		int correctBinaryStringLength = headerMemberLength
				- (checkedStartingBit);

		if (binaryStringLength + (startingBit + 1) > headerMemberLength) {
			binaryString = checkInputBinaryString(binaryString,
					correctBinaryStringLength);
		} else {
			binaryString = checkInputBinaryString(binaryString,
					binaryStringLength);
		}

		String headerMemberFrontString = headerMember.substring(0, startingBit);
		String headerMemberChangedBitsString = binaryString;
		String headerMemberRearString = headerMember.substring(startingBit
				+ binaryString.length());

		return headerMemberFrontString + headerMemberChangedBitsString
				+ headerMemberRearString;

	}

	public static String checkInputBinaryString(String binaryString,
			int headerMemberLength) {
		try {
			// TODO: Workaround Hack for String longer than 32 bit.....
			if (binaryString.length() < 32) {
				Integer.valueOf(binaryString, 2);
			}
		} catch (Exception ex) {
			Logger.logWarning("Error at: binaryString malformed! Filled binaryString with zeros!");
			return appendZerosToBinaryString("", headerMemberLength);
		}

		if (binaryString.length() == headerMemberLength) {
			return binaryString;
		} else if (binaryString.length() > headerMemberLength) {
			Logger.logWarning("Error at: binaryString too long, corrected by cutting of bits!");
			return binaryString.substring(0, headerMemberLength);
			//what's the logic behind this code? 
			/*return binaryString.substring(0, binaryString.length()
			*///		- (binaryString.length() - headerMemberLength));
		} else if (binaryString.length() < headerMemberLength) {
			Logger.logWarning("Error at: binaryString too short, corrected by appending zeros!");
			return appendZerosToBinaryString(binaryString, headerMemberLength);
		} else {
			Logger.logWarning("Error at: binaryString malformed! Filled binaryString with zeros!");
			return appendZerosToBinaryString("", headerMemberLength);
		}
	}

	public static int checkInputDecimalValue(int decimalValue,
			int headerMaxValue) {
		if (decimalValue >= 0 && decimalValue <= headerMaxValue) {
			return decimalValue;
		} else {
			Logger.logWarning("Error at: Wrong decimalValue! Set Value to 0");
			return decimalValue = 0;
		}
	}

	public static long checkInputDecimalValue(long decimalValue,
			long headerMaxValue) {
		if (decimalValue >= 0 && decimalValue <= headerMaxValue) {
			return decimalValue;
		} else {
			Logger.logWarning("Error at: Wrong decimalValue! Set Value to 0");
			return decimalValue = 0;
		}
	}

	public static int checkInputStartingBit(int inputStartingBit,
			int headerMemberLength) {
		if (inputStartingBit >= 0 && inputStartingBit < headerMemberLength) {
			return inputStartingBit;
		} else {
			Logger.logWarning("Error at: startingBit out of Bounds! Set to 0!");
			return 0;
		}
	}

	public static String appendZerosToBinaryString(String binaryString,
			int headerMemberLength) {
		String temp = binaryString;

		for (int i = 0; i < (headerMemberLength - binaryString.length()); i++) {
			temp = "0" + temp;
		}

		return temp;
	}

	public static String reverseBinaryString(String binaryString) {
		String reversedBinaryString = new StringBuffer(binaryString).reverse()
				.toString();
		return reversedBinaryString;
	}

	public static String generateZeroString(int length) {
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < length; i++) {
			temp.append("0");
		}
		return temp.toString();
	}

	public static long MaxValueFabrication(int bitLength) {
		long sum = 0;
		for (short i = 0; i < bitLength; i++) {
			sum += 1 << i;
		}
		return sum;
	}

	/**
	 * Transforms x.x.x.x schema in binaryAddress
	 * 
	 * @param x
	 *            .x.x.x schema will be transformed in binaryAddress
	 */
	public static String convertAddressToBinaryAddress(String input) {
		StringBuffer addressBuffer = new StringBuffer();
		StringTokenizer addressTokenizer = new StringTokenizer(input, ".");
		int tokenLength = 8;

		while (addressTokenizer.hasMoreTokens()) {

			String currentAddressToken = addressTokenizer.nextToken();

			String nonRevertedbinaryString = Integer.toBinaryString(Integer
					.valueOf(currentAddressToken).intValue());
			String completeBinaryString = PCEPComputationFactory
					.appendZerosToBinaryString(nonRevertedbinaryString,
							tokenLength);

			addressBuffer.append(completeBinaryString);
		}

		return addressBuffer.toString();

	}

	public static String convertBinaryAddressToAddress(String binaryAddress) {
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
}
