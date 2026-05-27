/**
 * This file is part of Path Computation Element Emulator (PCEE).
 *
 * <p>PCEE is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>PCEE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with PCEE. If not, see
 * http://www.gnu.org/licenses/.
 */
package com.pcee.protocol.message.objectframe.impl;

import com.pcee.protocol.message.PceComputationFactory;
import com.pcee.protocol.message.PceConstantValues;
import com.pcee.protocol.message.objectframe.PceCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrameFactory;

/**
 *
 *
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          Flags                    |O|B|R| Pri |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        Request-ID-number                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                      Optional TLVs                          //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceRequestParametersObject implements PceObjectFrame {

  private final String NAME = "Request Parameters";

  private String requestIDNumber;
  private String flags;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;

  private int flagsStartBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAGS_LENGTH;

  private int oFlagStartBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_O_START_BIT;
  private int oFlagEndBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_O_END_BIT;
  private int oFlagLength = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_O_LENGTH;

  private int bFlagStartBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_B_START_BIT;
  private int bFlagEndBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_B_END_BIT;
  private int bFlagLength = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_B_LENGTH;

  private int rFlagStartBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_R_START_BIT;
  private int rFlagEndBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_R_END_BIT;
  private int rFlagLength = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_R_LENGTH;

  private int priFlagStartBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_PRI_START_BIT;
  private int priFlagEndBit = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_PRI_END_BIT;
  private int priFlagLength = PceConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_PRI_LENGTH;

  private int requestIDNumberStartBit =
      PceConstantValues.REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_START_BIT;
  private int requestIDNumberEndBit =
      PceConstantValues.REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_END_BIT;
  private int requestIDNumberLength =
      PceConstantValues.REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_LENGTH;

  public PceRequestParametersObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceRequestParametersObject(
      PceCommonObjectHeader objectHeader,
      String oFlag,
      String bFlag,
      String rFlag,
      String priFlag,
      String requestIDNumber) {
    this.setObjectHeader(objectHeader);
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setOFlagBinaryString(oFlag);
    this.setBFlagBinaryString(bFlag);
    this.setRFlagBinaryString(rFlag);
    this.setPriFlagBinaryString(priFlag);
    this.setRequestIDNumberDecimalValue(Integer.parseInt(requestIDNumber));
    this.updateHeaderLength();
  }

  public static void main(String[] args) {
    PceRequestParametersObject RP =
        PceObjectFrameFactory.generatePCEPRequestParametersObject(
            "1", "0", "1", "0", "0", "1", "32");
    System.out.println(RP.getRequestIDNumberDecimalValue());
  }

  private void updateHeaderLength() {
    int objectFrameByteLength = this.getObjectFrameByteLength();
    this.getObjectHeader().setLengthDecimalValue(objectFrameByteLength);
  }

  /** Object */
  public PceCommonObjectHeader getObjectHeader() {
    return objectHeader;
  }

  public void setObjectHeader(PceCommonObjectHeader objectHeader) {
    this.objectHeader = objectHeader;
  }

  public String getObjectBinaryString() {
    String binaryString = flags + requestIDNumber;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String oFlagBinaryString = binaryString.substring(oFlagStartBit, oFlagEndBit + 1);
    String bFlagBinaryString = binaryString.substring(bFlagStartBit, bFlagEndBit + 1);
    String rFlagBinaryString = binaryString.substring(rFlagStartBit, rFlagEndBit + 1);
    String priFlagBinaryString = binaryString.substring(priFlagStartBit, priFlagEndBit + 1);
    String requestIDNumberBinaryString =
        binaryString.substring(requestIDNumberStartBit, requestIDNumberEndBit + 1);

    this.setFlagsBinaryString(flagsBinaryString);
    this.setOFlagBinaryString(oFlagBinaryString);
    this.setBFlagBinaryString(bFlagBinaryString);
    this.setRFlagBinaryString(rFlagBinaryString);
    this.setPriFlagBinaryString(priFlagBinaryString);
    this.setRequestIDNumberBinaryString(requestIDNumberBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength = flags.length() + requestIDNumber.length();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + headerLength) / 8;
    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  /** requestIDNumber */
  public int getRequestIDNumberDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(requestIDNumber);
    return decimalValue;
  }

  public String getRequestIDNumberBinaryString() {
    return this.requestIDNumber;
  }

  public void setRequestIDNumberDecimalValue(int decimalValue) {
    int binaryLength = requestIDNumberLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength - 1);

    this.requestIDNumber =
        PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setRequestIDNumberBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, requestIDNumberLength);
    this.requestIDNumber = checkedBinaryString;
  }

  public void setRequestIDNumberBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            requestIDNumber, startingBit, binaryString, requestIDNumberLength);
    this.requestIDNumber = checkedBinaryString;
  }

  /** flags */
  // public int getFlagsDecimalValue() {
  // int decimalValue = (int) getDecimalValue(flags);
  // return decimalValue;
  // }
  public String getFlagsBinaryString() {
    return this.flags;
  }

  // public void setFlagsDecimalValue(int decimalValue) {
  // int binaryLength = flagsLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.flags = this.setDecimalValue(decimalValue, maxValue, binaryLength);
  // }
  public void setFlagsBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public void setFlagsBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** oFlag */
  public int getOFlagDecimalValue() {
    int relativeStartBit = (oFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + oFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getOFlagBinaryString() {
    String binaryString = flags.substring(0, (oFlagStartBit - flagsStartBit) + oFlagLength);
    return binaryString;
  }

  public void setOFlagBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            this.flags, (oFlagStartBit - flagsStartBit), binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** bFlag */
  public int getBFlagDecimalValue() {
    int relativeStartBit = (bFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + bFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getBFlagBinaryString() {
    int relativeStartBit = (bFlagStartBit - flagsStartBit);
    String binaryString = flags.substring(relativeStartBit, relativeStartBit + bFlagLength);
    return binaryString;
  }

  public void setBFlagBinaryString(String binaryString) {
    int relativeStartBit = (bFlagStartBit - flagsStartBit);
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** rFlag */
  public int getRFlagDecimalValue() {
    int relativeStartBit = (rFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + rFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getRFlagBinaryString() {
    int relativeStartBit = (rFlagStartBit - flagsStartBit);
    String binaryString = flags.substring(relativeStartBit, relativeStartBit + rFlagLength);
    return binaryString;
  }

  public void setRFlagBinaryString(String binaryString) {
    int relativeStartBit = (rFlagStartBit - flagsStartBit);
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  /** priFlag */
  public int getPriFlagDecimalValue() {
    int relativeStartBit = (priFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + priFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getPriFlagBinaryString() {
    int relativeStartBit = (priFlagStartBit - flagsStartBit);
    String binaryString = flags.substring(relativeStartBit, relativeStartBit + priFlagLength);
    return binaryString;
  }

  public void setPriFlagBinaryString(String binaryString) {
    int relativeStartBit = (priFlagStartBit - flagsStartBit);
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public String toString() {
    String flagsInfo = "Flags=" + this.getFlagsBinaryString();
    String requestIDNumberInfo = ",RequestIDNumber=" + this.getRequestIDNumberBinaryString();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo = NAME + ":" + flagsInfo + requestIDNumberInfo + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String flagsInfo = this.getFlagsBinaryString();
    String requestIDNumberBinaryInfo = "'" + getRequestIDNumberBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo = "[" + flagsInfo + requestIDNumberBinaryInfo + "]";

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
