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
 * | Ver |   Flags |   Keepalive   |  DeadTimer    |      SID      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                       Optional TLVs                         //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PCEPOpenObject implements PCEPObjectFrame {

	private final String NAME = "Open";

	private String version;
	private String keepAlive;
	private String deadTimer;
	private String sessionID;
	private String flags;

	private PCEPCommonObjectHeader objectHeader;
//	private LinkedList<PCEPTLVObject> tlvList;

	private int versionStartBit = PCEPConstantValues.OPEN_OBJECT_VERSION_START_BIT;
	private int versionEndBit = PCEPConstantValues.OPEN_OBJECT_VERSION_END_BIT;
	private int versionLength = PCEPConstantValues.OPEN_OBJECT_VERSION_LENGTH;

	private int keepAliveStartBit = PCEPConstantValues.OPEN_OBJECT_KEEPALIVE_START_BIT;
	private int keepAliveEndBit = PCEPConstantValues.OPEN_OBJECT_KEEPALIVE_END_BIT;
	private int keepAliveLength = PCEPConstantValues.OPEN_OBJECT_KEEPALIVE_LENGTH;

	private int deadTimerStartBit = PCEPConstantValues.OPEN_OBJECT_DEADTIMER_START_BIT;
	private int deadTimerEndBit = PCEPConstantValues.OPEN_OBJECT_DEADTIMER_END_BIT;
	private int deadTimerLength = PCEPConstantValues.OPEN_OBJECT_DEADTIMER_LENGTH;

	private int sessionIDStartBit = PCEPConstantValues.OPEN_OBJECT_SESSIONID_START_BIT;
	private int sessionIDEndBit = PCEPConstantValues.OPEN_OBJECT_SESSIONID_END_BIT;
	private int sessionIDLength = PCEPConstantValues.OPEN_OBJECT_SESSIONID_LENGTH;

	private int flagsStartBit = PCEPConstantValues.OPEN_OBJECT_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.OPEN_OBJECT_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.OPEN_OBJECT_FLAGS_LENGTH;

	public PCEPOpenObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPOpenObject(PCEPCommonObjectHeader objectHeader, int version, int keepAlive, int deadTimer, int sessionID) {
		this.setObjectHeader(objectHeader);
		this.setVersionDecimalValue(version);
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setKeepAliveDecimalValue(keepAlive);
		this.setDeadTimerDecimalValue(deadTimer);
		this.setSessionIDDecimalValue(sessionID);
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
		String binaryString = version + flags + keepAlive + deadTimer + sessionID;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String versionBinaryString = binaryString.substring(versionStartBit, versionEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String keepAliveBinaryString = binaryString.substring(keepAliveStartBit, keepAliveEndBit + 1);
		String deadTimerBinaryString = binaryString.substring(deadTimerStartBit, deadTimerEndBit + 1);
		String sessionIDBinaryString = binaryString.substring(sessionIDStartBit, sessionIDEndBit + 1);

		this.setVersionBinaryString(versionBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setKeepAliveBinaryString(keepAliveBinaryString);
		this.setDeadTimerBinaryString(deadTimerBinaryString);
		this.setSessionIDBinaryString(sessionIDBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = version.length() + flags.length() + keepAlive.length() + deadTimer.length() + sessionID.length();
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
	 * keepAlive
	 */
	public int getKeepAliveDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(keepAlive);
		return decimalValue;
	}

	public String getKeepAliveBinaryString() {
		return this.keepAlive;
	}

	public void setKeepAliveDecimalValue(int decimalValue) {
		int binaryLength = keepAliveLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.keepAlive = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setKeepAliveBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, keepAliveLength);
		this.keepAlive = checkedBinaryString;
	}

	public void setKeepAliveBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(keepAlive, startingBit, binaryString, keepAliveLength);
		this.keepAlive = checkedBinaryString;
	}

	/**
	 * deadTimer
	 */
	public int getDeadTimerDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(deadTimer);
		return decimalValue;
	}

	public String getDeadTimerBinaryString() {
		return this.deadTimer;
	}

	public void setDeadTimerDecimalValue(int decimalValue) {
		int binaryLength = deadTimerLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.deadTimer = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setDeadTimerBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, deadTimerLength);
		this.deadTimer = checkedBinaryString;
	}

	public void setDeadTimerBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(deadTimer, startingBit, binaryString, deadTimerLength);
		this.deadTimer = checkedBinaryString;
	}

	/**
	 * sessionID
	 */
	public int getSessionIDDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(sessionID);
		return decimalValue;
	}

	public String getSessionIDBinaryString() {
		return this.sessionID;
	}

	public void setSessionIDDecimalValue(int decimalValue) {
		int binaryLength = sessionIDLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.sessionID = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setSessionIDBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, sessionIDLength);
		this.sessionID = checkedBinaryString;
	}

	public void setSessionIDBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(sessionID, startingBit, binaryString, sessionIDLength);
		this.sessionID = checkedBinaryString;
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
		String keepAliveInfo = ",KeepAlive=" + this.getKeepAliveDecimalValue();
		String deadTimerInfo = ",DeadTimer=" + this.getDeadTimerDecimalValue();
		String sessionIDInfo = ",SessionID=" + this.getSessionIDDecimalValue();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = NAME + ":" + versionInfo + flagsInfo + keepAliveInfo + deadTimerInfo + sessionIDInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String versionBinaryInfo = getVersionBinaryString();
		String flagsInfo = "'" + this.getFlagsBinaryString();
		String keepAliveBinaryInfo = "'" + getKeepAliveBinaryString();
		String deadTimerBinaryInfo = "'" + getDeadTimerBinaryString();
		String sessionIDBinaryInfo = "'" + getSessionIDBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + versionBinaryInfo + flagsInfo + keepAliveBinaryInfo + deadTimerBinaryInfo + sessionIDBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
