package com.pcee.protocol.message.objectframe.impl.erosubobjects;

import com.pcee.protocol.message.PCEPComputationFactory;

public class GeneralizedLabelEROSubobject extends LabelEROSubobject {
	
	/*
	 * 

    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |Grid | C.S.  |    Identifier   |              n                |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */
	
	protected String grid;
	protected String channelSpacing;
	protected String identifier;
	protected String n;
	
	
	private int gridStartBit = 32;
	private int gridEndBit = 34;
	private int gridLength = 3;

	private int channelSpacingStartBit = 35;
	private int channelSpacingEndBit = 38;
	private int channelSpacingLength = 4;
	
	private int identifierStartBit = 39;
	private int identifierEndBit = 47;
	private int identifierLength = 9;

	
	private int nStartBit = 48;
	private int nEndBit = 63;
	private int nLength = 16;

	
	
	public GeneralizedLabelEROSubobject(String binaryString) {
		NAME = "GeneralizedLabelEROSubobject";
		this.setObjectBinaryString(binaryString);
	}
	
	 /**
	  * grid
	  */
	 public int getGridDecimalValue() {
		 int gridValue = (int) PCEPComputationFactory.getDecimalValue(grid);
		 return gridValue;
	 }

	 public String getGridBinaryString() {
		 return this.grid;
	 }

	 public void setGridDecimalValue(int decimalValue) {
		 int binaryLength = gridLength;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 String tmp = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
		 this.grid = tmp;
	 }

	 public void setGridBinaryString(String binaryString) {
		 this.grid= binaryString;
	 }
	 

	 /**
	  * channelSpacing
	  */
	 public int getChannelSpacingDecimalValue() {
		 int channelSpacingValue = (int) PCEPComputationFactory.getDecimalValue(channelSpacing);
		 return channelSpacingValue;
	 }

	 public String getChannelSpacingBinaryString() {
		 return this.channelSpacing;
	 }

	 public void setChannelSpacingDecimalValue(int decimalValue) {
		 int binaryLength = channelSpacingLength;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 String tmp = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
		 this.channelSpacing = tmp;
	 }

	 public void setChannelSpacingBinaryString(String binaryString) {
		 this.channelSpacing= binaryString;
	 }

	 
	
	 /**
	  * identifier
	  */
	 public int getIdentifierDecimalValue() {
		 int identifierValue = (int) PCEPComputationFactory.getDecimalValue(identifier);
		 return identifierValue;
	 }

	 public String getIdentifierBinaryString() {
		 return this.identifier;
	 }

	 public void setIdentifierDecimalValue(int decimalValue) {
		 int binaryLength = identifierLength;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 String tmp = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
		 this.identifier = tmp;
	 }

	 public void setIdentifierBinaryString(String binaryString) {
		 this.identifier= binaryString;
	 }


	 /**
	  * identifier
	  */
	 public int getNDecimalValue() {
		 int nValue = (int) PCEPComputationFactory.getDecimalValue(n);
		 return nValue;
	 }

	 public String getNBinaryString() {
		 return this.n;
	 }

	 public void setNDecimalValue(int decimalValue) {
		 int binaryLength = nLength;
		 int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		 String tmp = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
		 this.n = tmp;
	 }

	 public void setNBinaryString(String binaryString) {
		 this.n= binaryString;
	 }
	 
	 
	
	@Override
	public String getObjectBinaryString() {
		String binaryString =  getLabelObjectHeaderBinaryString() + grid + channelSpacing + identifier + n;
		return binaryString;
	}

	@Override
	public void setObjectBinaryString(String binaryString) {
		setLabelObjectHeaderBinaryString(binaryString);
		String gridBinaryString = binaryString.substring(gridStartBit, gridEndBit + 1);
		String channelSpacingBinaryString = binaryString.substring(channelSpacingStartBit, channelSpacingEndBit + 1);
		String identifierBinaryString = binaryString.substring(identifierStartBit, identifierEndBit + 1);
		String nBinaryString = binaryString.substring(nStartBit, nEndBit + 1);
		
		this.setGridBinaryString(gridBinaryString);
		this.setChannelSpacingBinaryString(channelSpacingBinaryString);
		this.setIdentifierBinaryString(identifierBinaryString);
		this.setNBinaryString(nBinaryString);
	}

	@Override
	public int getByteLength() {
		return getLabelObjectHeaderByteLength() + (grid.length() + channelSpacing.length() + identifier.length()+ n.length()) /8; 
	}

	@Override
	public String toString() {
		String gridInfo = ", Grid =" + this.getGridDecimalValue();
		String channelSpacingInfo = ", Channel Spacing =" + this.getChannelSpacingDecimalValue();
		String identifierInfo = ", Identifier =" + this.getIdentifierDecimalValue();
		String nInfo = ", n =" + this.getNDecimalValue();

		String objectInfo = NAME + ":" + headerString() + gridInfo + channelSpacingInfo + identifierInfo + nInfo + ">";

		return objectInfo;
	}

	public String binaryInformation() {
		String gridBinaryInfo = "'" + getGridBinaryString();
		String channelSpacingBinaryInfo = "'" + getChannelSpacingBinaryString();
		String identifierBinaryInfo = "'" + getIdentifierBinaryString();
		String nInfo = "'" + getNBinaryString();

		String objectInfo = "[" + gridBinaryInfo  + channelSpacingBinaryInfo + identifierBinaryInfo + nInfo + "]";

		return objectInfo;
	}


}
