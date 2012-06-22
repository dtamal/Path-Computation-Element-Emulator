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

import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;

/**
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Reserved    |                   Flags                 |S|N|L|
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                     Request-ID-number #1                      |
 * //                                                             //
 * |                     Request-ID-number #M                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PCEPSynchronizationVectorObject implements PCEPObjectFrame {

	private final String NAME = "Synchronization Vector";

	private String reserved;
	private String flags;

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;
	private LinkedList<String> requestIDNumbers;

	private int reservedStartBit = PCEPConstantValues.SVEC_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.SVEC_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.SVEC_OBJECT_RESERVED_LENGTH;

	private int flagsStartBit = PCEPConstantValues.SVEC_OBJECT_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.SVEC_OBJECT_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.SVEC_OBJECT_FLAGS_LENGTH;

	private int sFlagStartBit = PCEPConstantValues.SVEC_OBJECT_FLAG_S_START_BIT;
	private int sFlagEndBit = PCEPConstantValues.SVEC_OBJECT_FLAG_S_END_BIT;
	private int sFlagLength = PCEPConstantValues.SVEC_OBJECT_FLAG_S_LENGTH;

	private int nFlagStartBit = PCEPConstantValues.SVEC_OBJECT_FLAG_N_START_BIT;
	private int nFlagEndBit = PCEPConstantValues.SVEC_OBJECT_FLAG_N_END_BIT;
	private int nFlagLength = PCEPConstantValues.SVEC_OBJECT_FLAG_N_LENGTH;

	private int lFlagStartBit = PCEPConstantValues.SVEC_OBJECT_FLAG_L_START_BIT;
	private int lFlagEndBit = PCEPConstantValues.SVEC_OBJECT_FLAG_L_END_BIT;
	private int lFlagLength = PCEPConstantValues.SVEC_OBJECT_FLAG_L_LENGTH;

	/**
	 * Testconstructor, not implemented for the requestIDNumbers
	 */
	public PCEPSynchronizationVectorObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPSynchronizationVectorObject(PCEPCommonObjectHeader objectHeader, String sFlag, String nFlag, String lFlag, LinkedList<String> requestIDNumbers) {
		this.setObjectHeader(objectHeader);
		this.setReservedBinaryString(PCEPComputationFactory.generateZeroString(reservedLength));
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setSFlagBinaryString(sFlag);
		this.setNFlagBinaryString(nFlag);
		this.setLFlagBinaryString(lFlag);
		this.requestIDNumbers = requestIDNumbers;
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
		String binaryString = reserved + flags;

		StringBuffer requestIDNumbersStringBuffer = new StringBuffer();

		for (int i = 0; i < requestIDNumbers.size(); i++) {
			String reuqestIDNumberString = requestIDNumbers.get(i);
			requestIDNumbersStringBuffer.append(reuqestIDNumberString);
		}

		return binaryString + requestIDNumbersStringBuffer.toString();
	}

	public void setObjectBinaryString(String binaryString) {
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String sFlagBinaryString = binaryString.substring(sFlagStartBit, sFlagEndBit + 1);
		String nFlagBinaryString = binaryString.substring(nFlagStartBit, nFlagEndBit + 1);
		String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);

		this.setReservedBinaryString(reservedBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setSFlagBinaryString(sFlagBinaryString);
		this.setNFlagBinaryString(nFlagBinaryString);
		this.setLFlagBinaryString(lFlagBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = reserved.length() + flags.length();
		int requestIDNumbersLength = getRequestIDNumbersLength();
		int headerLength = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH;
		int objectFrameByteLength = (objectLength + requestIDNumbersLength + headerLength) / 8;
		return objectFrameByteLength;
	}

	public String getObjectFrameBinaryString() {
		String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
		String objectBinaryString = this.getObjectBinaryString();

		return headerBinaryString + objectBinaryString;
	}

	private int getRequestIDNumbersLength() {
		int length = 0;
		for (int i = 0; i < requestIDNumbers.size(); i++) {
			length += requestIDNumbers.get(i).length();
		}
		return length;
	}

	/**
	 * reserved
	 */
	// public int getReservedDecimalValue() {
	// int decimalValue = (int) getDecimalValue(reserved);
	// return decimalValue;
	// }
	public String getReservedBinaryString() {
		return this.reserved;
	}

	// public void setReservedDecimalValue(int decimalValue) {
	// int binaryLength = reservedLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.reserved = this.setDecimalValue(decimalValue, maxValue,
	// binaryLength);
	// }
	public void setReservedBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		this.reserved = checkedBinaryString;
	}

	public void setReservedBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(reserved, startingBit, binaryString, reservedLength);
		this.reserved = checkedBinaryString;
	}

	/**
	 * flags
	 */
	// public int getFlagsDecimalValue() {
	// int decimalValue = (int) getDecimalValue(flags);
	// return decimalValue;
	// }
	public String getFlagsBinaryString() {
		return this.flags;
	}

	// public void setFlagsDecimalValue(int decimalValue) {
	// int binaryLength = flagsLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.flags = this.setDecimalValue(decimalValue, maxValue, binaryLength);
	// }
	public void setFlagsBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public void setFlagsBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * sFlag
	 */

	public int getSFlagDecimalValue() {
		int relativeStartBit = (sFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + sFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getSFlagBinaryString() {
		String binaryString = flags.substring(0, (sFlagStartBit - flagsStartBit) + sFlagLength);
		return binaryString;
	}

	public void setSFlagBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(this.flags, (sFlagStartBit - flagsStartBit), binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * nFlag
	 */
	public int getNFlagDecimalValue() {
		int relativeStartBit = (nFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + nFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getNFlagBinaryString() {
		int relativeStartBit = (nFlagStartBit - flagsStartBit);
		String binaryString = flags.substring(relativeStartBit, relativeStartBit + nFlagLength);
		return binaryString;
	}

	public void setNFlagBinaryString(String binaryString) {
		int relativeStartBit = (nFlagStartBit - flagsStartBit);
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * lFlag
	 */
	public int getLFlagDecimalValue() {
		int relativeStartBit = (lFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + lFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getLFlagBinaryString() {
		int relativeStartBit = (lFlagStartBit - flagsStartBit);
		String binaryString = flags.substring(relativeStartBit, relativeStartBit + lFlagLength);
		return binaryString;
	}

	public void setLFlagBinaryString(String binaryString) {
		int relativeStartBit = (lFlagStartBit - flagsStartBit);
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public String toString() {
		String reservedInfo = "Reserved=" + this.getReservedBinaryString();
		String flagsInfo = ",Flags=" + this.getFlagsBinaryString();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "<SVEC:" + reservedInfo + flagsInfo;

		StringBuffer requestIDNumbersStringBuffer = new StringBuffer();

		for (int i = 0; i < requestIDNumbers.size(); i++) {
			String reuqestIDNumberString = requestIDNumbers.get(i);
			requestIDNumbersStringBuffer.append(",RequestID=" + reuqestIDNumberString);
		}
		requestIDNumbersStringBuffer.append(">");

		return headerInfo + objectInfo + requestIDNumbersStringBuffer.toString();
	}

	public String binaryInformation() {
		String reservedBinaryInfo = getReservedBinaryString();
		String flagsInfo = "'" + this.getFlagsBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + reservedBinaryInfo + flagsInfo;

		StringBuffer requestIDNumbersStringBuffer = new StringBuffer();

		for (int i = 0; i < requestIDNumbers.size(); i++) {
			String reuqestIDNumberString = requestIDNumbers.get(i);
			requestIDNumbersStringBuffer.append("'" + reuqestIDNumberString);
		}
		requestIDNumbersStringBuffer.append("]");

		return headerInfo + objectInfo + requestIDNumbersStringBuffer.toString();
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
