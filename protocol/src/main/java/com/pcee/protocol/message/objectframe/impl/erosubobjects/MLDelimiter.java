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

package com.pcee.protocol.message.objectframe.impl.erosubobjects;


import com.pcee.protocol.message.PCEPComputationFactory;

/**
 * <pre>
 *    0                   1                   2                   3
 *    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |L|    Type     |     Length    |    Reserved (MUST be zero)    |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |switching cap  |  encoding     |            Reserved           |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |               Optional TLV (Not Implemented)                  |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * <pre>
 */

public class MLDelimiter extends EROSubobjects {

	private int reservedStartBit = 16;
	private int reservedEndBit = 31;
	private int reservedLength = 16;

	
	private String reserved;
	private String reserved1;
	private String swCap;
	private String encoding;


	private int swCapStartBit = 32;
	private int swCapEndBit = 39;
	private int swCapLength = 8;

	private int encodingStartBit = 40;
	private int encodingEndBit = 47;
	private int encodingLength = 8;

	
	private int reserved1StartBit = 48;
	private int reserved1EndBit = 63;
	private int reserved1Length = 16;

	

	public MLDelimiter(String binaryString) {
		NAME= "MLDelimiter";
		this.setObjectBinaryString(binaryString);
	}

	public MLDelimiter() {
		NAME= "MLDelimiter";
		this.setLFlag(false);
		this.setTypeDecimalValue(EROSubobjects.PCEPMLDelimiterType);
		this.setLengthDecimalValue(8);
		this.setReservedDecimalValue(0);
		this.setReserved1DecimalValue(0);
		this.setSwCapDecimalValue(1);
		this.setEncodingDecimalValue(1);
	}

	public static void main(String[] args) {
		MLDelimiter e = new MLDelimiter();
		String eBinary = e.getObjectBinaryString();
		MLDelimiter e2 = new MLDelimiter(eBinary);

		System.out.println(e.binaryInformation());
		System.out.println(e2.binaryInformation());

		System.out.println(e.toString());
		System.out.println(e2.toString());

	}

	/**
	 * Object
	 */
	public String getObjectBinaryString() {
		String binaryString = lFlag + type + length + reserved + swCap + encoding + reserved1;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);
		String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
		String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
		String switchingCapBinaryString = binaryString.substring(swCapStartBit, swCapEndBit + 1);
		String encodingBinaryString = binaryString.substring(encodingStartBit, encodingEndBit + 1);
		String reserved1BinaryString = binaryString.substring(reserved1StartBit, reserved1EndBit + 1);

		this.setLFlagBinaryString(lFlagBinaryString);
		this.setTypeBinaryString(typeBinaryString);
		this.setLengthBinaryString(lengthBinaryString);
		this.setReservedBinaryString(reservedBinaryString);
		this.setSwCapBinaryString(switchingCapBinaryString);
		this.setEncodingBinaryString(encodingBinaryString);
		this.setReserved1BinaryString(reserved1BinaryString);
		
	}

	public int getByteLength() {
		int objectLength = lFlag.length() + type.length() + length.length() + reserved.length() + swCap.length() + encoding.length() + reserved1.length();
		int objectFrameByteLength = objectLength / 8;

		return objectFrameByteLength;
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
		 // String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		 this.reserved = binaryString;
	 }



	
	/**
	 * swCap
	 */

	public String getSwCapBinaryString() {
		return swCap;
	}

	public int getSwCapDecimalValue(){
		int temp = (int) PCEPComputationFactory.getDecimalValue(swCap);
		return temp;
	}

	public void setSwCapBinaryString(String binaryString) {
		// String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, routerIDLength);
		this.swCap = binaryString;
	}

	public void setSwCapDecimalValue(int decimalValue) {
		int binaryLength = swCapLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength-1);

		this.swCap = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}


	/**
	 * encoding
	 */

	public String getEncodingBinaryString() {
		return encoding;
	}

	public int getEncodingDecimalValue(){
		int temp = (int) PCEPComputationFactory.getDecimalValue(encoding);
		return temp;
	}

	public void setEncodingBinaryString(String binaryString) {
		// String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, routerIDLength);
		this.encoding = binaryString;
	}

	public void setEncodingDecimalValue(int decimalValue) {
		int binaryLength = encodingLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength-1);

		this.encoding = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	
	
	
	 /**
	  * reserved1
	  */
	 public int getReserved1DecimalValue() {
		 int decimalValue = (int) PCEPComputationFactory.getDecimalValue(reserved1);
		 return decimalValue;
	 }

	 public String getReserved1BinaryString() {
		 return this.reserved1;
	 }

	 public void setReserved1DecimalValue(int decimalValue) {
		 int binaryLength = reserved1Length;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 this.reserved1 = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	 }

	 public void setReserved1BinaryString(String binaryString) {
		 // String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		 this.reserved1 = binaryString;
	 }

	
	
	
	/**
	 * OUTPUT
	 */
	public String toString() {
		String lFlagInfo = "lFlag=" + this.getLFlagDecimalValue();
		String typeInfo = ", type=" + this.getTypeDecimalValue();
		String lengthInfo = ", length=" + this.getLengthDecimalValue();
		String reservedInfo = ", Reserved=" + this.getReservedDecimalValue();
		String swCapInfo = ", Switching Capacity=" + this.getSwCapDecimalValue();
		String encodingInfo = ", Encoding=" + this.getEncodingDecimalValue();
		String reserved1Info = ", Reserved1=" + this.getReserved1DecimalValue();
		
		String objectInfo = NAME + ":" + lFlagInfo + typeInfo + lengthInfo + reservedInfo + swCapInfo + encodingInfo + reserved1Info + ">";
		return objectInfo;
	}

	public String binaryInformation() {
		String lFlagBinaryInfo = getLFlagBinaryString();
		String typeBinaryInfo = "'" + getTypeBinaryString();
		String lengthBinaryInfo = "'" + getLengthBinaryString();
		String reservedInfo = "'" + getReservedBinaryString();
		String swCapInfo = "'" + getSwCapBinaryString();
		String encodingInfo = "'" + getEncodingBinaryString();
		String reserved1Info = "'" + getReserved1BinaryString();

		String objectInfo = "[" + lFlagBinaryInfo + typeBinaryInfo + lengthBinaryInfo + reservedInfo  + swCapInfo + encodingInfo + reserved1Info + "]";

		return objectInfo;
	}


}
