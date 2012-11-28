package com.pcee.protocol.message.objectframe.impl.erosubobjects;

import com.pcee.protocol.message.PCEPComputationFactory;

/**
 * <pre>
 *    0                   1                   2                   3
 *    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |L|    Type     |     Length    |    Reserved (MUST be zero)    |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *                            Subobjects                             |
 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *   |                     Interface ID (32 bits)                    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * <pre>
 */

public abstract class EROSubobjects {

	public static final int PCEPIPv4AddressType = 1;
	public static final int PCEPUnnumberedInterfaceType = 4;
	public static final int PCEPMLDelimiterType = 40;
	
	protected String NAME;

	protected String lFlag;
	protected String type;
	protected String length; // in bytes

	protected int lFlagStartBit = 0;
	protected int lFlagEndBit = 0;
	protected int lFlagLength = 1;

	protected int typeStartBit = 1;
	protected int typeEndBit = 7;
	protected int typeLength = 7;

	protected int lengthStartBit = 8;
	protected int lengthEndBit = 15;
	protected int lengthLength = 8;



	public static void main(String[] args) {
		EROUnnumberedInterface e = new EROUnnumberedInterface(true, 1073741825, 1073741825);
		String eBinary = e.getObjectBinaryString();
		EROUnnumberedInterface e2 = new EROUnnumberedInterface(eBinary);

		System.out.println(e.binaryInformation());
		System.out.println(e2.binaryInformation());

		System.out.println(e.toString());
		System.out.println(e2.toString());

	}

	/**
	 * Object
	 */
	 public abstract String getObjectBinaryString();

	 public abstract void setObjectBinaryString(String binaryString) ;  

	 public abstract int getByteLength() ;
	 /**
	  * lFlag
	  */
	 public int getLFlagDecimalValue() {
		 int decimalValue = (int) PCEPComputationFactory.getDecimalValue(lFlag);
		 return decimalValue;
	 }

	 public String getLFlagBinaryString() {
		 return this.lFlag;
	 }

	 public void setLFlagDecimalValue(int decimalValue) {
		 int binaryLength = lFlagLength;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 this.lFlag = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	 }

	 public void setLFlag(boolean isLooseHop) {
		 if (isLooseHop) {
			 lFlag = "1";
		 } else {
			 lFlag = "0";
		 }
	 }

	 public void setLFlagBinaryString(String binaryString) {
		 // String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, lFlagLength);
		 this.lFlag = binaryString;
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
		 // String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, typeLength);
		 this.type = binaryString;
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

		 String tmp = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
		 this.length = tmp;
	 }

	 public void setLengthBinaryString(String binaryString) {
		 // String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, lengthLength);
		 this.length = binaryString;
	 }

	 /**
	  * OUTPUT
	  */
	 public abstract String toString() ;


	 public abstract String binaryInformation() ;

	 public String contentInformation() {
		 return "[" + NAME + "]";
	 }

	 public int compareTo(EROUnnumberedInterface a) {

		 if (a.toString().equals(this.toString())) { // TODO
			 return 1;
		 }

		 return 0;
	 }


}
