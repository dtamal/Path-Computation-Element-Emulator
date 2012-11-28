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
 * |                  Reserved                     | Endpoint Type |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    														   |
 * |                       Source Point                            |
 * |															   |					
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       		                                   |
 * |					 Destination Point						   |
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPGeneralizedEndPointsTNAObject implements PCEPObjectFrame {

	private final String NAME = "Generalized-End-Points-TNA";

	private String reserved;
	private String endPointType;
	private String sourcePoint;
	private String destinationPoint;
	private PCEPCommonObjectHeader objectHeader;

	private int reservedStartBit = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_RESERVED_LENGTH;

	private int endPointTypeStartBit = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_END_POINT_TYPE_START_BIT;
	private int endPointTypeEndBit = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_END_POINT_TYPE_END_BIT;
	private int endPointTypeLength = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_END_POINT_TYPE_LENGTH;

	private int sourcePointStartBit = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_SOURCE_POINT_START_BIT;
	private int sourcePointEndBit = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_SOURCE_POINT_END_BIT;
	private int sourcePointLength = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_SOURCE_POINT_LENGTH;

	private int destinationPointStartBit = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_DESTINATION_POINT_START_BIT;
	private int destinationPointEndBit = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_DESTINATION_POINT_END_BIT;
	private int destinationPointLength = PCEPConstantValues.GENERALIZED_END_POINTS_TNA_OBJECT_DESTINATION_POINT_LENGTH;

	public PCEPGeneralizedEndPointsTNAObject(PCEPCommonObjectHeader objectHeader, String objectString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(objectString);
		this.updateHeaderLength();
	}

	public PCEPGeneralizedEndPointsTNAObject(PCEPCommonObjectHeader objectHeader, int reserved, int endPointType, PCEPTNASourceObject sourcePoint, PCEPTNADestinationObject destinationPoint) {
		this.setObjectHeader(objectHeader);
		this.setReservedBinaryString(Integer.toBinaryString(reserved));
		this.setEndPointTypeBinaryString(Integer.toBinaryString(endPointType));
		this.setSourcePointBinaryString(sourcePoint.getObjectBinaryString());
		this.setDestinationPointBinaryString(destinationPoint.getObjectBinaryString());
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
		String binaryString = reserved + endPointType + sourcePoint + destinationPoint;
		return binaryString;
	}

	public int getObjectFrameByteLength() {
		int objectLength = reserved.length() + endPointType.length() + sourcePoint.length() + destinationPoint.length();
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
	public String getReservedBinaryString() {
		return this.reserved;
	}

	public void setReservedBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		this.reserved = checkedBinaryString;
	}

	/**
	 * end point type
	 */
	public String getEndPointTypeBinaryString() {
		return this.endPointType;
	}

	public void setEndPointTypeBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, endPointTypeLength);
		this.endPointType = checkedBinaryString;
	}

	/**
	 * source point
	 */
	public String getSourcePointBinaryString() {
		return this.sourcePoint;
	}

	public void setSourcePointBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, sourcePointLength);
		this.sourcePoint = checkedBinaryString;
	}

	/**
	 * destination point
	 */
	public String getDestinationPointBinaryString() {
		return this.destinationPoint;
	}

	public void setDestinationPointBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, destinationPointLength);
		this.destinationPoint = checkedBinaryString;
	}

	public String toString() {
		String headerInfo = this.getObjectHeader().binaryInformation();

		String objectInfo = "PCEPGeneralizedEndPointsTNAObject : Reserved-" + Integer.valueOf(reserved, 2) + ", EndPointType-" + Integer.valueOf(endPointType, 2) + ", SourcePoint-" + sourcePoint + ", DestinationPoint-" + destinationPoint;
		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + reserved + " ' " + endPointType + " ' " + sourcePoint + " ' " + destinationPoint + "]";
		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

	@Override
	public void setObjectBinaryString(String objectString) {
		String reservedBinaryString = objectString.substring(reservedStartBit, reservedEndBit + 1);
		String endPointTypeBinaryString = objectString.substring(endPointTypeStartBit, endPointTypeEndBit + 1);
		String sourcePointBinaryString = objectString.substring(sourcePointStartBit, sourcePointEndBit + 1);
		String destinationPointBinaryString = objectString.substring(destinationPointStartBit, destinationPointEndBit + 1);

		this.setReservedBinaryString(reservedBinaryString);
		this.setEndPointTypeBinaryString(endPointTypeBinaryString);
		this.setSourcePointBinaryString(sourcePointBinaryString);
		this.setDestinationPointBinaryString(destinationPointBinaryString);
	}

}
