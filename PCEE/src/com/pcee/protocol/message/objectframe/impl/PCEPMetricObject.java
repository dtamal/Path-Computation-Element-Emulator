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
 * |          Reserved             |    Flags  |C|B|      Type     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          metric-value                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPMetricObject implements PCEPObjectFrame {

	private final String NAME = "Metric";

	private String reserved;
	private String type;
	private String metricValue;
	private String flags;

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;

	private int reservedStartBit = PCEPConstantValues.METRIC_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.METRIC_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.METRIC_OBJECT_RESERVED_LENGTH;

	private int flagsStartBit = PCEPConstantValues.METRIC_OBJECT_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.METRIC_OBJECT_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.METRIC_OBJECT_FLAGS_LENGTH;

	private int cFlagStartBit = PCEPConstantValues.METRIC_OBJECT_FLAG_C_START_BIT;
	private int cFlagEndBit = PCEPConstantValues.METRIC_OBJECT_FLAG_C_END_BIT;
	private int cFlagLength = PCEPConstantValues.METRIC_OBJECT_FLAG_C_LENGTH;

	private int bFlagStartBit = PCEPConstantValues.METRIC_OBJECT_FLAG_B_START_BIT;
	private int bFlagEndBit = PCEPConstantValues.METRIC_OBJECT_FLAG_B_END_BIT;
	private int bFlagLength = PCEPConstantValues.METRIC_OBJECT_FLAG_B_LENGTH;

	private int typeStartBit = PCEPConstantValues.METRIC_OBJECT_TYPE_START_BIT;
	private int typeEndBit = PCEPConstantValues.METRIC_OBJECT_TYPE_END_BIT;
	private int typeLength = PCEPConstantValues.METRIC_OBJECT_TYPE_LENGTH;

	private int metricValueStartBit = PCEPConstantValues.METRIC_OBJECT_METRIC_VALUE_START_BIT;
	private int metricValueEndBit = PCEPConstantValues.METRIC_OBJECT_METRIC_VALUE_END_BIT;
	private int metricValueLength = PCEPConstantValues.METRIC_OBJECT_METRIC_VALUE_LENGTH;

	public PCEPMetricObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPMetricObject(PCEPCommonObjectHeader objectHeader, String cFlag, String bFlag, int type, String metricValue) {
		this.setObjectHeader(objectHeader);
		this.setReservedBinaryString(PCEPComputationFactory.generateZeroString(reservedLength));
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setCFlagBinaryString(cFlag);
		this.setBFlagBinaryString(bFlag);
		this.setTypeDecimalValue(type);
		this.setMetricValueBinaryString(metricValue);
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
		String binaryString = reserved + flags + type + metricValue;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String cFlagBinaryString = binaryString.substring(cFlagStartBit, cFlagEndBit + 1);
		String bFlagBinaryString = binaryString.substring(bFlagStartBit, bFlagEndBit + 1);
		String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
		String metricValueBinaryString = binaryString.substring(metricValueStartBit, metricValueEndBit + 1);

		this.setReservedBinaryString(reservedBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setCFlagBinaryString(cFlagBinaryString);
		this.setBFlagBinaryString(bFlagBinaryString);
		this.setTypeBinaryString(typeBinaryString);
		this.setMetricValueBinaryString(metricValueBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = reserved.length() + flags.length() + type.length() + metricValue.length();
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
	 * metricValue
	 */
	// public int getMetricValueDecimalValue() {
	// int decimalValue = (int) getDecimalValue(metricValue);
	// return decimalValue;
	// }
	public String getMetricValueBinaryString() {
		return this.metricValue;
	}

	// public void setMetricValueDecimalValue(int decimalValue) {
	// int binaryLength = metricValueLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.metricValue = this.setDecimalValue(decimalValue, maxValue,
	// binaryLength);
	// }
	public void setMetricValueBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, metricValueLength);
		this.metricValue = checkedBinaryString;
	}

	public void setMetricValueBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(metricValue, startingBit, binaryString, metricValueLength);
		this.metricValue = checkedBinaryString;
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
	 * cFlag
	 */

	public int getCFlagDecimalValue() {
		int relativeStartBit = (cFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + cFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getCFlagBinaryString() {
		String binaryString = flags.substring(0, (cFlagStartBit - flagsStartBit) + cFlagLength);
		return binaryString;
	}

	public void setCFlagBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(this.flags, (cFlagStartBit - flagsStartBit), binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * bFlag
	 */
	public int getBFlagDecimalValue() {
		int relativeStartBit = (bFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + bFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getBFlagBinaryString() {
		int relativeStartBit = (bFlagStartBit - flagsStartBit);
		String binaryString = flags.substring(relativeStartBit, relativeStartBit + bFlagLength);
		return binaryString;
	}

	public void setBFlagBinaryString(String binaryString) {
		int relativeStartBit = (bFlagStartBit - flagsStartBit);
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public String toString() {
		String reservedInfo = "Reserved=" + this.getReservedBinaryString();
		String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
		String typeInfo = ",Type=" + this.getTypeDecimalValue();
		String metricValueInfo = ",MetricValue=" + this.getMetricValueBinaryString();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "<Metric:" + reservedInfo + flagsInfo + typeInfo + metricValueInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String reservedBinaryInfo = getReservedBinaryString();
		String flagsInfo = "'" + this.getFlagsBinaryString();
		String typeBinaryInfo = "'" + getTypeBinaryString();
		String metricValueBinaryInfo = "'" + getMetricValueBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + reservedBinaryInfo + flagsInfo + typeBinaryInfo + metricValueBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
