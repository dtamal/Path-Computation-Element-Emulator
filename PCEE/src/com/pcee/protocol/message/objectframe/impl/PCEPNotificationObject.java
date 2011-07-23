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
 * |   Reserved    |     Flags     |      NT       |     NV        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 *  //                      Optional TLVs                          //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPNotificationObject implements PCEPObjectFrame {

	private final String NAME = "Notification";

	private String reserved;
	private String notificationType;
	private String notificationValue;
	private String flags;

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;

	private int reservedStartBit = PCEPConstantValues.NOTIFICATION_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.NOTIFICATION_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.NOTIFICATION_OBJECT_RESERVED_LENGTH;

	private int notificationTypeStartBit = PCEPConstantValues.NOTIFICATION_OBJECT_NOTIFICATION_TYPE_START_BIT;
	private int notificationTypeEndBit = PCEPConstantValues.NOTIFICATION_OBJECT_NOTIFICATION_TYPE_END_BIT;
	private int notificationTypeLength = PCEPConstantValues.NOTIFICATION_OBJECT_NOTIFICATION_TYPE_LENGTH;

	private int notificationValueStartBit = PCEPConstantValues.NOTIFICATION_OBJECT_NOTIFICATION_VALUE_START_BIT;
	private int notificationValueEndBit = PCEPConstantValues.NOTIFICATION_OBJECT_NOTIFICATION_VALUE_END_BIT;
	private int notificationValueLength = PCEPConstantValues.NOTIFICATION_OBJECT_NOTIFICATION_VALUE_LENGTH;

	private int flagsStartBit = PCEPConstantValues.NOTIFICATION_OBJECT_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.NOTIFICATION_OBJECT_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.NOTIFICATION_OBJECT_FLAGS_LENGTH;

	public PCEPNotificationObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPNotificationObject(PCEPCommonObjectHeader objectHeader, int notificationType, int notificationValue) {
		this.setObjectHeader(objectHeader);
		this.setReservedBinaryString(PCEPComputationFactory.generateZeroString(reservedLength));
		this.setNotificationTypeDecimalValue(notificationType);
		this.setNotificationValueDecimalValue(notificationValue);
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
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
		String binaryString = reserved + flags + notificationType + notificationValue;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String notificationTypeBinaryString = binaryString.substring(notificationTypeStartBit, notificationTypeEndBit + 1);
		String notificationValueBinaryString = binaryString.substring(notificationValueStartBit, notificationValueEndBit + 1);

		this.setReservedBinaryString(reservedBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setNotificationTypeBinaryString(notificationTypeBinaryString);
		this.setNotificationValueBinaryString(notificationValueBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = reserved.length() + flags.length() + notificationType.length() + notificationValue.length();
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
	 * notificationType
	 */
	public int getNotificationTypeDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(notificationType);
		return decimalValue;
	}

	public String getNotificationTypeBinaryString() {
		return this.notificationType;
	}

	public void setNotificationTypeDecimalValue(int decimalValue) {
		int binaryLength = notificationTypeLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.notificationType = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setNotificationTypeBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, notificationTypeLength);
		this.notificationType = checkedBinaryString;
	}

	public void setNotificationTypeBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(notificationType, startingBit, binaryString, notificationTypeLength);
		this.notificationType = checkedBinaryString;
	}

	/**
	 * notificationValue
	 */
	public int getNotificationValueDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(notificationValue);
		return decimalValue;
	}

	public String getNotificationValueBinaryString() {
		return this.notificationValue;
	}

	public void setNotificationValueDecimalValue(int decimalValue) {
		int binaryLength = notificationValueLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.notificationValue = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setNotificationValueBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, notificationValueLength);
		this.notificationValue = checkedBinaryString;
	}

	public void setNotificationValueBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(notificationValue, startingBit, binaryString, notificationValueLength);
		this.notificationValue = checkedBinaryString;
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
		String reservedInfo = "Reserved=" + this.getReservedBinaryString();
		String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
		String notificationTypeInfo = ",NotificationType=" + this.getNotificationTypeDecimalValue();
		String notificationValueInfo = ",NotificationValue=" + this.getNotificationValueDecimalValue();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "<Notification:" + reservedInfo + flagsInfo + notificationTypeInfo + notificationValueInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String reservedBinaryInfo = getReservedBinaryString();
		String flagsInfo = "'" + this.getFlagsBinaryString();
		String notificationTypeBinaryInfo = "'" + getNotificationTypeBinaryString();
		String notificationValueBinaryInfo = "'" + getNotificationValueBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + reservedBinaryInfo + flagsInfo + notificationTypeBinaryInfo + notificationValueBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
