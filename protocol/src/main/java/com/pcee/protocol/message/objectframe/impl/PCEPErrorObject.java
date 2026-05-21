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

/**
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Reserved    |      Flags    |   Error-Type  |  Error-value  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                     Optional TLVs                           //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPErrorObject implements PCEPObjectFrame {

	private final String NAME = "Error";

	private String reserved;
	private String type;
	private String value;
	private String flags;

	private PCEPCommonObjectHeader objectHeader;

	private int reservedStartBit = PCEPConstantValues.ERROR_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.ERROR_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.ERROR_OBJECT_RESERVED_LENGTH;

	private int flagsStartBit = PCEPConstantValues.ERROR_OBJECT_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.ERROR_OBJECT_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.ERROR_OBJECT_FLAGS_LENGTH;

	private int typeStartBit = PCEPConstantValues.ERROR_OBJECT_TYPE_START_BIT;
	private int typeEndBit = PCEPConstantValues.ERROR_OBJECT_TYPE_END_BIT;
	private int typeLength = PCEPConstantValues.ERROR_OBJECT_TYPE_LENGTH;

	private int valueStartBit = PCEPConstantValues.ERROR_OBJECT_VALUE_START_BIT;
	private int valueEndBit = PCEPConstantValues.ERROR_OBJECT_VALUE_END_BIT;
	private int valueLength = PCEPConstantValues.ERROR_OBJECT_VALUE_LENGTH;

	public PCEPErrorObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPErrorObject(PCEPCommonObjectHeader objectHeader, int type, int value) {
		this.setObjectHeader(objectHeader);
		this.setReservedBinaryString(PCEPComputationFactory.generateZeroString(reservedLength));
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setTypeDecimalValue(type);
		this.setValueDecimalValue(value);
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
		String binaryString = reserved + flags + type + value;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
		String valueBinaryString = binaryString.substring(valueStartBit, valueEndBit + 1);

		this.setReservedBinaryString(reservedBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setTypeBinaryString(typeBinaryString);
		this.setValueBinaryString(valueBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = reserved.length() + flags.length() + type.length() + value.length();
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
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		this.reserved = checkedBinaryString;
	}

	public void setReservedBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(reserved, startingBit, binaryString, reservedLength);
		this.reserved = checkedBinaryString;
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
	 * value
	 */
	public int getValueDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(value);
		return decimalValue;
	}

	public String getValueBinaryString() {
		return this.value;
	}

	public void setValueDecimalValue(int decimalValue) {
		int binaryLength = valueLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.value = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setValueBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, valueLength);
		this.value = checkedBinaryString;
	}

	public void setValueBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(value, startingBit, binaryString, valueLength);
		this.value = checkedBinaryString;
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
		String reservedInfo = "Reserved=" + this.getReservedDecimalValue();
		String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
		String typeInfo = ",Type=" + this.getTypeDecimalValue();
		String valueInfo = ",Value=" + this.getValueDecimalValue();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = NAME + ":" + reservedInfo + flagsInfo + typeInfo + valueInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String reservedBinaryInfo = getReservedBinaryString();
		String flagsInfo = "'" + this.getFlagsBinaryString();
		String typeBinaryInfo = "'" + getTypeBinaryString();
		String valueBinaryInfo = "'" + getValueBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + reservedBinaryInfo + flagsInfo + typeBinaryInfo + valueBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
