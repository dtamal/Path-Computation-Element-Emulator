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

package com.pcee.protocol.message.objectframe;

import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.PCEPConstantValues;

/**
 * <pre>
 * <b>Header Layout:</b>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Object-Class  |   OT  |Res|P|I|   Object Length (bytes)       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                        (Object body)                        //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPCommonObjectHeader {

	private final String NAME = "Object Header";

	private String objectClass;
	private String type;
	private String reserved;
	private String length;
	private String flags;

	private int objectClassStartBit = PCEPConstantValues.COMMON_OBJECT_HEADER_CLASS_START_BIT;
	private int objectClassEndBit = PCEPConstantValues.COMMON_OBJECT_HEADER_CLASS_END_BIT;
	private int objectClassLength = PCEPConstantValues.COMMON_OBJECT_HEADER_CLASS_LENGTH;

	private int typeStartBit = PCEPConstantValues.COMMON_OBJECT_HEADER_TYPE_START_BIT;
	private int typeEndBit = PCEPConstantValues.COMMON_OBJECT_HEADER_TYPE_END_BIT;
	private int typeLength = PCEPConstantValues.COMMON_OBJECT_HEADER_TYPE_LENGTH;

	private int reservedStartBit = PCEPConstantValues.COMMON_OBJECT_HEADER_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.COMMON_OBJECT_HEADER_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.COMMON_OBJECT_HEADER_RESERVED_LENGTH;

	private int flagsStartBit = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAGS_LENGTH;

	private int pFlagStartBit = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAG_PROCESSED_START_BIT;
	private int pFlagEndBit = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAG_PROCESSED_END_BIT;
	private int pFlagLength = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAG_PROCESSED_LENGTH;

	private int iFlagStartBit = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAG_IGNORED_START_BIT;
	private int iFlagEndBit = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAG_IGNORED_END_BIT;
	private int iFlagLength = PCEPConstantValues.COMMON_OBJECT_HEADER_FLAG_IGNORED_LENGTH;

	private int lengthStartBit = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH_START_BIT;
	private int lengthEndBit = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH_END_BIT;
	private int lengthLength = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH_LENGTH;

	public PCEPCommonObjectHeader(String binaryString) {
		this.setHeaderBinaryString(binaryString);
	}

	public PCEPCommonObjectHeader(int objectClass, int type, String pFlag, String iFlag) {
		this.setClassDecimalValue(objectClass);
		this.setTypeDecimalValue(type);
		this.setReservedBinaryString(PCEPComputationFactory.generateZeroString(reservedLength));
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setPFlagBinaryString(pFlag);
		this.setIFlagBinaryString(iFlag);
		this.setLengthDecimalValue(0);
	}

	public String getHeaderBinaryString() {
		String binaryString = objectClass + type + reserved + flags + length;
		return binaryString;
	}

	public void setHeaderBinaryString(String binaryString) {
		String objectClassBinaryString = binaryString.substring(objectClassStartBit, objectClassEndBit + 1);
		String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String pFlagBinaryString = binaryString.substring(pFlagStartBit, pFlagEndBit + 1);
		String iFlagBinaryString = binaryString.substring(iFlagStartBit, iFlagEndBit + 1);
		String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);

		this.setClassBinaryString(objectClassBinaryString);
		this.setTypeBinaryString(typeBinaryString);
		this.setReservedBinaryString(reservedBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setPFlagBinaryString(pFlagBinaryString);
		this.setIFlagBinaryString(iFlagBinaryString);
		this.setLengthBinaryString(lengthBinaryString);
	}

	/**
	 * objectClass
	 */
	public int getClassDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(objectClass);
		return decimalValue;
	}

	public String getClassBinaryString() {
		return this.objectClass;
	}

	public void setClassDecimalValue(int decimalValue) {
		int binaryLength = objectClassLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.objectClass = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setClassBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, objectClassLength);
		this.objectClass = checkedBinaryString;
	}

	public void setClassBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(objectClass, startingBit, binaryString, objectClassLength);
		this.objectClass = checkedBinaryString;
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

	/**
	 * pFlag
	 */

	public int getPFlagDecimalValue() {
		int relativeStartBit = (pFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + pFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getPFlagBinaryString() {
		String binaryString = flags.substring(0, (pFlagStartBit - flagsStartBit) + pFlagLength);
		return binaryString;
	}

	public void setPFlagBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(this.flags, (pFlagStartBit - flagsStartBit), binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * iFlag
	 */
	public int getIFlagDecimalValue() {
		int relativeStartBit = (iFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + iFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getIFlagBinaryString() {
		int relativeStartBit = (iFlagStartBit - flagsStartBit);
		String binaryString = flags.substring(relativeStartBit, relativeStartBit + iFlagLength);
		return binaryString;
	}

	public void setIFlagBinaryString(String binaryString) {
		int relativeStartBit = (iFlagStartBit - flagsStartBit);
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public String toString() {
		String objectClassInfo = "Class=" + this.getClassDecimalValue();
		String typeInfo = ",Type=" + this.getTypeDecimalValue();
		String reservedInfo = ",Reserved=" + this.getReservedBinaryString();
		String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
		String lengthInfo = ",Length=" + this.getLengthDecimalValue();

		String headerInfo = "<" + NAME + ":" + objectClassInfo + typeInfo + reservedInfo + flagsInfo + lengthInfo + ">";

		return headerInfo;
	}

	public String binaryInformation() {
		String objectClassBinaryInfo = getClassBinaryString();
		String typeBinaryInfo = "'" + getTypeBinaryString();
		String reservedBinaryInfo = "'" + getReservedBinaryString();
		String flagsInfo = "'" + this.getFlagsBinaryString();
		String lengthBinaryInfo = "'" + getLengthBinaryString();

		String headerInfo = "[" + objectClassBinaryInfo + typeBinaryInfo + reservedBinaryInfo + flagsInfo + lengthBinaryInfo + "]";

		return headerInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}
	
	public void log(String logString){
		System.out.println("PCEPCommonObjectHeader::: " + logString);
	}
}
