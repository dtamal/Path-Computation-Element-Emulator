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
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Nature of Issue|C|          Flags              |   Reserved    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                      Optional TLVs                          //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPNoVertexObject implements PCEPObjectFrame {

	private final String NAME = "No-Vertex";

	private String natureOfIssue;
	private String reserved;
	private String flags;

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;

	private int natureOfIssueStartBit = PCEPConstantValues.NO_VERTEX_OBJECT_NATURE_OF_ISSUE_START_BIT;
	private int natureOfIssueEndBit = PCEPConstantValues.NO_VERTEX_OBJECT_NATURE_OF_ISSUE_END_BIT;
	private int natureOfIssueLength = PCEPConstantValues.NO_VERTEX_OBJECT_NATURE_OF_ISSUE_LENGTH;

	private int flagsStartBit = PCEPConstantValues.NO_VERTEX_OBJECT_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.NO_VERTEX_OBJECT_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.NO_VERTEX_OBJECT_FLAGS_LENGTH;

	private int constraintsFlagStartBit = PCEPConstantValues.NO_VERTEX_OBJECT_FLAG_FLAG_CONSTRAINTS_START_BIT;
	private int constraintsFlagEndBit = PCEPConstantValues.NO_VERTEX_OBJECT_FLAG_FLAG_CONSTRAINTS_END_BIT;
	private int constraintsFlagLength = PCEPConstantValues.NO_VERTEX_OBJECT_FLAG_FLAG_CONSTRAINTS_LENGTH;

	private int reservedStartBit = PCEPConstantValues.NO_VERTEX_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.NO_VERTEX_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.NO_VERTEX_OBJECT_RESERVED_LENGTH;

	public PCEPNoVertexObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPNoVertexObject(PCEPCommonObjectHeader objectHeader, int natureOfIssue, String constraintsFlag) {
		this.setObjectHeader(objectHeader);
		this.setNatureOfIssueDecimalValue(natureOfIssue);
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setConstraintsFlagBinaryString(constraintsFlag);
		this.setReservedBinaryString(PCEPComputationFactory.generateZeroString(reservedLength));
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
		String binaryString = natureOfIssue + flags + reserved;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String natureOfIssueBinaryString = binaryString.substring(natureOfIssueStartBit, natureOfIssueEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String constraintsFlagBinaryString = binaryString.substring(constraintsFlagStartBit, constraintsFlagEndBit + 1);
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);

		this.setNatureOfIssueBinaryString(natureOfIssueBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setConstraintsFlagBinaryString(constraintsFlagBinaryString);
		this.setReservedBinaryString(reservedBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = natureOfIssue.length() + flags.length() + reserved.length();
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
	 * natureOfIssue
	 */
	public int getNatureOfIssueDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(natureOfIssue);
		return decimalValue;
	}

	public String getNatureOfIssueBinaryString() {
		return this.natureOfIssue;
	}

	public void setNatureOfIssueDecimalValue(int decimalValue) {
		int binaryLength = natureOfIssueLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.natureOfIssue = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setNatureOfIssueBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, natureOfIssueLength);
		this.natureOfIssue = checkedBinaryString;
	}

	public void setNatureOfIssueBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(natureOfIssue, startingBit, binaryString, natureOfIssueLength);
		this.natureOfIssue = checkedBinaryString;
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
	 * constraintsFlag
	 */

	public int getConstraintsFlagDecimalValue() {
		int relativeStartBit = (constraintsFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + constraintsFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getConstraintsFlagBinaryString() {
		String binaryString = flags.substring(0, (constraintsFlagStartBit - flagsStartBit) + constraintsFlagLength);
		return binaryString;
	}

	public void setConstraintsFlagBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(this.flags, (constraintsFlagStartBit - flagsStartBit), binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public String toString() {
		String natureOfIssueInfo = "NatureOfIssue=" + this.getNatureOfIssueDecimalValue();
		String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
		String reservedInfo = ",Reserved=" + this.getReservedBinaryString();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "<No-Path:" + natureOfIssueInfo + flagsInfo + reservedInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String natureOfIssueBinaryInfo = getNatureOfIssueBinaryString();
		String flagsInfo = "'" + this.getFlagsBinaryString();
		String reservedBinaryInfo = "'" + getReservedBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + natureOfIssueBinaryInfo + flagsInfo + reservedBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
