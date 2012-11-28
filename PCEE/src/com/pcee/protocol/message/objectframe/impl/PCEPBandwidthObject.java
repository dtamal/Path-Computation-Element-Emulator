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
 * |                        Bandwidth                              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

// TODO Change to IEEE Floating point object
public class PCEPBandwidthObject implements PCEPObjectFrame {

	/*
	 * public static void main(String[] args) { PCEPBandwidthObject b = new
	 * PCEPBandwidthObject( new PCEPCommonObjectHeader(6, 1, "1", "0"),
	 * 101.11f);
	 * 
	 * System.out.println(b.getBandwidthFloatValue());
	 * 
	 * String headerBinaryString = b.getObjectHeader().getHeaderBinaryString();
	 * String objectBinaryString = b.getObjectBinaryString();
	 * System.out.println(objectBinaryString);
	 * 
	 * PCEPCommonObjectHeader header = new PCEPCommonObjectHeader(
	 * headerBinaryString);
	 * 
	 * PCEPBandwidthObject b2 = new PCEPBandwidthObject(header,
	 * objectBinaryString); System.out.println(b2.getBandwidthFloatValue()); }
	 */

	private final String NAME = "Bandwidth";

	private String bandwidth;

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;

	private int bandwidthStartBit = PCEPConstantValues.BANDWIDTH_OBJECT_BANDWIDTH_START_BIT;
	private int bandwidthEndBit = PCEPConstantValues.BANDWIDTH_OBJECT_BANDWIDTH_END_BIT;
	private int bandwidthLength = PCEPConstantValues.BANDWIDTH_OBJECT_BANDWIDTH_LENGTH;

	public PCEPBandwidthObject(PCEPCommonObjectHeader objectHeader, String bandwidthBinaryRepresentation) {
		this.setObjectHeader(objectHeader);
		this.setBandwidthBinaryString(bandwidthBinaryRepresentation);
		this.updateHeaderLength();
	}

	public PCEPBandwidthObject(PCEPCommonObjectHeader objectHeader, float bandwidth) {
		this.setObjectHeader(objectHeader);
		this.setBandwidthFloatValue(bandwidth);
		this.updateHeaderLength();
	}

	private void updateHeaderLength() {
		this.getObjectHeader().setLengthDecimalValue(this.getObjectFrameByteLength());
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
		String binaryString = bandwidth;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String bandwidthBinaryString = binaryString.substring(bandwidthStartBit, bandwidthEndBit + 1);

		this.setBandwidthBinaryString(bandwidthBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = bandwidth.length();
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
	 * bandwidth
	 */
	public float getBandwidthFloatValue() {
		return Float.intBitsToFloat(Integer.parseInt(bandwidth, 2));
	}

	public String getBandwidthBinaryString() {
		return this.bandwidth;
	}

	public void setBandwidthFloatValue(float floatValue) {

		int floatBits = Float.floatToIntBits(floatValue);

		String s = Integer.toBinaryString(floatBits);

		if (s.length() > 32) {
			System.out.println("ERROR in PCEPBandwithObject.setBandwidthFloatValue(...)!!! Bandwith value is bigger than 32 bits, exiting now to let you fix it! :P");
			System.exit(0);
		}
		setBandwidthBinaryString(s);
	}

	public void setBandwidthBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, bandwidthLength);
		this.bandwidth = checkedBinaryString;
	}

	public String toString() {
		String bandwidthInfo = "Bandwidth=" + this.getBandwidthFloatValue();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "<Bandwidth:" + bandwidthInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String bandwidthBinaryInfo = getBandwidthBinaryString();
		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + bandwidthBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}
}
