package com.pcee.protocol.message.objectframe.impl.erosubobjects;

import com.pcee.protocol.message.PCEPComputationFactory;

public abstract class LabelEROSubobject extends EROSubobjects{

/*	
	0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |L|    Type     |     Length    |U|   Reserved  |   C-Type      |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                             Label                             |
   |                              ...                              |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
*/
	
	protected String uFlag;
	protected String reserved;
	protected String cType;
	
	protected int uFlagStartBit = 16;
	protected int uFlagEndBit = 16;
	protected int uFlagLength = 1;

	protected int reservedStartBit = 17;
	protected int reservedEndBit = 23;
	protected int reservedLength = 7;

	protected static int cTypeStartBit = 24;
	protected static int cTypeEndBit = 31;
	protected static int cTypeLength = 8;
	
	
	
	 /**
	  * cType
	  */
	 public int getCTypeDecimalValue() {
		 int cTypeValue = (int) PCEPComputationFactory.getDecimalValue(cType);
		 return cTypeValue;
	 }

	 public String getCTypeBinaryString() {
		 return this.cType;
	 }

	 public void setCTypeDecimalValue(int decimalValue) {
		 int binaryLength = cTypeLength;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 String tmp = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
		 this.cType = tmp;
	 }

	 public void setCTypeBinaryString(String binaryString) {
		 this.cType= binaryString;
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
	  * UFlag
	  */
	 public int getUFlagDecimalValue() {
		 int decimalValue = (int) PCEPComputationFactory.getDecimalValue(uFlag);
		 return decimalValue;
	 }

	 public String getUFlagBinaryString() {
		 return this.uFlag;
	 }

	 public void setUFlagDecimalValue(int decimalValue) {
		 int binaryLength = uFlagLength;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 this.uFlag = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	 }

	 public void setUFlag(boolean val) {
		 if (val) {
			 uFlag = "1";
		 } else {
			 uFlag = "0";
		 }
	 }

	 public void setUFlagBinaryString(String binaryString) {
		 // String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, lFlagLength);
		 this.uFlag = binaryString;
	 }


	 
	 /**Generalized header composition for the Label Subobjects
	  * 
	  */
	 
	 public String getLabelObjectHeaderBinaryString() {
		 String binaryString = lFlag + type + length + uFlag + reserved + cType;
		 return binaryString;
	 }

	 public void setLabelObjectHeaderBinaryString(String binaryString) {
			String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);
			String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
			String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);
			String uFlagBinaryString = binaryString.substring(uFlagStartBit, uFlagEndBit + 1);
			String cTypeBinaryString = binaryString.substring(cTypeStartBit, cTypeEndBit + 1);
			String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
			
			this.setLFlagBinaryString(lFlagBinaryString);
			this.setTypeBinaryString(typeBinaryString);
			this.setLengthBinaryString(lengthBinaryString);
			this.setReservedBinaryString(reservedBinaryString);
			this.setCTypeBinaryString(cTypeBinaryString);
			this.setUFlagBinaryString(uFlagBinaryString);
	 }

	 public int getLabelObjectHeaderByteLength() {
			int objectLength = lFlag.length() + type.length() + length.length() + reserved.length() + uFlag.length() + cType.length();
			int objectFrameByteLength = objectLength / 8;
			return objectFrameByteLength;
	 }
	 
	 
	 public String headerString() {
			String lFlagInfo = "lFlag=" + this.getLFlagDecimalValue();
			String typeInfo = ", type=" + this.getTypeDecimalValue();
			String lengthInfo = ", length=" + this.getLengthDecimalValue();
			String uFlagInfo = ", uFlag=" + this.getUFlagDecimalValue();
			String cTypeInfo = ", cType=" + this.getCTypeDecimalValue();
			String reservedInfo = ", Reserved=" + this.getReservedDecimalValue();
			
			return  lFlagInfo + typeInfo + lengthInfo + uFlagInfo + reservedInfo + cTypeInfo;
	 }
	 
	 public String headerBinaryInformation(){
			String lFlagBinaryInfo = getLFlagBinaryString();
			String typeBinaryInfo = "'" + getTypeBinaryString();
			String lengthBinaryInfo = "'" + getLengthBinaryString();
			String uFlagInfo = "'" + getUFlagBinaryString();
			String reservedInfo = "'" + getReservedBinaryString();
			String cTypeInfo = "'" + getCTypeBinaryString();
			return lFlagBinaryInfo + typeBinaryInfo + lengthBinaryInfo + uFlagInfo + reservedInfo + cTypeInfo;
	 }

	 
	 public static LabelEROSubobject getObjectFromBinaryString(String binaryString){
		 //check the cType binary String to get the actual subobject
		String cTypeBinaryString = binaryString.substring(cTypeStartBit, cTypeEndBit + 1);
		int cTypeValue = (int) PCEPComputationFactory.getDecimalValue(cTypeBinaryString);
		
		if (cTypeValue==2) {
			return new GeneralizedLabelEROSubobject(binaryString);
		}
		return null;
	 }
	 
}
