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

import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.PCEPConstantValues;

/**
 * <pre>
 * <b>Header Layout:</b>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Ver |  Flags  |  Message-Type |       Message-Length          |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPCommonMessageHeader {

	private final String NAME = "Message Header";

	private String version;
	private String type;
	private String length;
	private String flags;

	private int versionStartBit = PCEPConstantValues.COMMON_MESSAGE_HEADER_VERSION_START_BIT;
	private int versionEndBit = PCEPConstantValues.COMMON_MESSAGE_HEADER_VERSION_END_BIT;
	private int versionLength = PCEPConstantValues.COMMON_MESSAGE_HEADER_VERSION_LENGTH;

	private int flagsStartBit = PCEPConstantValues.COMMON_MESSAGE_HEADER_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.COMMON_MESSAGE_HEADER_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.COMMON_MESSAGE_HEADER_FLAGS_LENGTH;

	private int typeStartBit = PCEPConstantValues.COMMON_MESSAGE_HEADER_TYPE_START_BIT;
	private int typeEndBit = PCEPConstantValues.COMMON_MESSAGE_HEADER_TYPE_END_BIT;
	private int typeLength = PCEPConstantValues.COMMON_MESSAGE_HEADER_TYPE_LENGTH;

	private int lengthStartBit = PCEPConstantValues.COMMON_MESSAGE_HEADER_LENGTH_START_BIT;
	private int lengthEndBit = PCEPConstantValues.COMMON_MESSAGE_HEADER_LENGTH_END_BIT;
	private int lengthLength = PCEPConstantValues.COMMON_MESSAGE_HEADER_LENGTH_LENGTH;

	public PCEPCommonMessageHeader(String binaryString) {
		this.setHeaderBinaryString(binaryString);
	}

	public PCEPCommonMessageHeader(int version, int type) {
		this.setVersionDecimalValue(version);
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setTypeDecimalValue(type);
		this.setLengthDecimalValue(0);
	}

	/**
	 * Header
	 */
	public String getHeaderBinaryString() {
		String binaryString = version + flags + type + length;
		return binaryString;
	}

	public void setHeaderBinaryString(String binaryString) {
		String versionBinaryString = binaryString.substring(versionStartBit, versionEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
		String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);

		this.setVersionBinaryString(versionBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setTypeBinaryString(typeBinaryString);
		this.setLengthBinaryString(lengthBinaryString);
	}

	/**
	 * version
	 */
	public int getVersionDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(version);
		return decimalValue;
	}

	public String getVersionBinaryString() {
		return this.version;
	}

	public void setVersionDecimalValue(int decimalValue) {
		int binaryLength = versionLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.version = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setVersionBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, versionLength);
		this.version = checkedBinaryString;
	}

	public void setVersionBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(version, startingBit, binaryString, versionLength);
		this.version = checkedBinaryString;
	}

	/**
	 * type
	 */
	public int getTypeDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(type);
		return decimalValue;
	}

	public String getTypeBinaryString() {
		return this.type;
	}

	public void setTypeDecimalValue(int decimalValue) {
		int binaryLength = typeLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.type = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
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
	 * length
	 */
	public int getLengthDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(length);
		return decimalValue;
	}

	public String getLengthBinaryString() {
		return this.length;
	}

	public void setLengthDecimalValue(int decimalValue) {
		int binaryLength = lengthLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.length = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setLengthBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, lengthLength);
		this.length = checkedBinaryString;
	}

	public void setLengthBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(length, startingBit, binaryString, lengthLength);
		this.length = checkedBinaryString;
	}

	/**
	 * flags
	 */
	public int getFlagsDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flags);
		return decimalValue;
	}

	public String getFlagsBinaryString() {
		return this.flags;
	}

	public void setFlagsDecimalValue(int decimalValue) {
		int binaryLength = flagsLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.flags = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setFlagsBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public void setFlagsBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public String toString() {
		String versionInfo = "Version=" + this.getVersionDecimalValue();
		String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
		String typeInfo = ",Type=" + this.getTypeDecimalValue();
		String lengthInfo = ",Length=" + this.getLengthDecimalValue();

		String headerInfo = NAME + ":" + versionInfo + typeInfo + lengthInfo + flagsInfo + ">";

		return headerInfo;
	}

	public String binaryInformation() {
		String versionBinaryInfo = getVersionBinaryString();
		String flagsInfo = "'" + getFlagsBinaryString();
		String typeBinaryInfo = "'" + getTypeBinaryString();
		String lengthBinaryInfo = "'" + getLengthBinaryString();

		String headerInfo = "[" + versionBinaryInfo + typeBinaryInfo + lengthBinaryInfo + flagsInfo + "]";

		return headerInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
