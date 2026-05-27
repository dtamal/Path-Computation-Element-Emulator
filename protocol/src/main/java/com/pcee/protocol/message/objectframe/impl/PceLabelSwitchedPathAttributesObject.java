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

/**
 *
 *
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Exclude-any                             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Include-any                             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Include-all                             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Setup Prio   |  Holding Prio |     Flags   |L|   Reserved    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                     Optional TLVs                           //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceLabelSwitchedPathAttributesObject implements PceObjectFrame {

  private final String NAME = "Label Switched Path Attributes";

  private String excludeAny;
  private String includeAny;
  private String includeAll;
  private String setupPrio;
  private String holdingPrio;
  private String reserved;
  private String flags;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;

  private int excludeAnyStartBit = PceConstantValues.LSPA_OBJECT_EXCLUDE_ANY_START_BIT;
  private int excludeAnyEndBit = PceConstantValues.LSPA_OBJECT_EXCLUDE_ANY_END_BIT;
  private int excludeAnyLength = PceConstantValues.LSPA_OBJECT_EXCLUDE_ANY_LENGTH;

  private int includeAnyStartBit = PceConstantValues.LSPA_OBJECT_INCLUDE_ANY_START_BIT;
  private int includeAnyEndBit = PceConstantValues.LSPA_OBJECT_INCLUDE_ANY_END_BIT;
  private int includeAnyLength = PceConstantValues.LSPA_OBJECT_INCLUDE_ANY_LENGTH;

  private int includeAllStartBit = PceConstantValues.LSPA_OBJECT_INCLUDE_ALL_START_BIT;
  private int includeAllEndBit = PceConstantValues.LSPA_OBJECT_INCLUDE_ALL_END_BIT;
  private int includeAllLength = PceConstantValues.LSPA_OBJECT_INCLUDE_ALL_LENGTH;

  private int setupPrioStartBit = PceConstantValues.LSPA_OBJECT_SETUP_PRIO_START_BIT;
  private int setupPrioEndBit = PceConstantValues.LSPA_OBJECT_SETUP_PRIO_END_BIT;
  private int setupPrioLength = PceConstantValues.LSPA_OBJECT_SETUP_PRIO_LENGTH;

  private int holdingPrioStartBit = PceConstantValues.LSPA_OBJECT_HOLDING_PRIO_START_BIT;
  private int holdingPrioEndBit = PceConstantValues.LSPA_OBJECT_HOLDING_PRIO_END_BIT;
  private int holdingPrioLength = PceConstantValues.LSPA_OBJECT_HOLDING_PRIO_LENGTH;

  private int flagsStartBit = PceConstantValues.LSPA_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.LSPA_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.LSPA_OBJECT_FLAGS_LENGTH;

  private int lFlagStartBit = PceConstantValues.LSPA_OBJECT_FLAG_L_START_BIT;
  private int lFlagEndBit = PceConstantValues.LSPA_OBJECT_FLAG_L_END_BIT;
  private int lFlagLength = PceConstantValues.LSPA_OBJECT_FLAG_L_LENGTH;

  private int reservedStartBit = PceConstantValues.LSPA_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.LSPA_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.LSPA_OBJECT_RESERVED_LENGTH;

  public PceLabelSwitchedPathAttributesObject(
      PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceLabelSwitchedPathAttributesObject(
      PceCommonObjectHeader objectHeader,
      String excludeAny,
      String includeAny,
      String includeAll,
      int setupPrio,
      int holdingPrio,
      String lFlag) {
    this.setObjectHeader(objectHeader);
    this.setExcludeAnyBinaryString(excludeAny);
    this.setIncludeAnyBinaryString(includeAny);
    this.setIncludeAllBinaryString(includeAll);
    this.setSetupPrioDecimalValue(setupPrio);
    this.setHoldingPrioDecimalValue(holdingPrio);
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setLFlagBinaryString(lFlag);
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
    this.updateHeaderLength();
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
    String binaryString =
        excludeAny + includeAny + includeAll + setupPrio + holdingPrio + flags + reserved;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String excludeAnyBinaryString =
        binaryString.substring(excludeAnyStartBit, excludeAnyEndBit + 1);
    String includeAnyBinaryString =
        binaryString.substring(includeAnyStartBit, includeAnyEndBit + 1);
    String includeAllBinaryString =
        binaryString.substring(includeAllStartBit, includeAllEndBit + 1);
    String setupPrioBinaryString = binaryString.substring(setupPrioStartBit, setupPrioEndBit + 1);
    String holdingPrioBinaryString =
        binaryString.substring(holdingPrioStartBit, holdingPrioEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);

    this.setExcludeAnyBinaryString(excludeAnyBinaryString);
    this.setIncludeAnyBinaryString(includeAnyBinaryString);
    this.setIncludeAllBinaryString(includeAllBinaryString);
    this.setSetupPrioBinaryString(setupPrioBinaryString);
    this.setHoldingPrioBinaryString(holdingPrioBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setLFlagBinaryString(lFlagBinaryString);
    this.setReservedBinaryString(reservedBinaryString);
  }

  public int getObjectFrameByteLength() {
    int objectLength =
        excludeAny.length()
            + includeAny.length()
            + includeAll.length()
            + setupPrio.length()
            + holdingPrio.length()
            + flags.length()
            + reserved.length();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + headerLength) / 8;
    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  /** excludeAny */
  // public int getExcludeAnyDecimalValue() {
  // int decimalValue = (int) getDecimalValue(excludeAny);
  // return decimalValue;
  // }
  public String getExcludeAnyBinaryString() {
    return this.excludeAny;
  }

  // public void setExcludeAnyDecimalValue(int decimalValue) {
  // int binaryLength = excludeAnyLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.excludeAny = this.setDecimalValue(decimalValue, maxValue,
  // binaryLength);
  // }
  public void setExcludeAnyBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, excludeAnyLength);
    this.excludeAny = checkedBinaryString;
  }

  public void setExcludeAnyBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            excludeAny, startingBit, binaryString, excludeAnyLength);
    this.excludeAny = checkedBinaryString;
  }

  /** includeAny */
  // public int getIncludeAnyDecimalValue() {
  // int decimalValue = (int) getDecimalValue(includeAny);
  // return decimalValue;
  // }
  public String getIncludeAnyBinaryString() {
    return this.includeAny;
  }

  // public void setIncludeAnyDecimalValue(int decimalValue) {
  // int binaryLength = includeAnyLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.includeAny = this.setDecimalValue(decimalValue, maxValue,
  // binaryLength);
  // }
  public void setIncludeAnyBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, includeAnyLength);
    this.includeAny = checkedBinaryString;
  }

  public void setIncludeAnyBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            includeAny, startingBit, binaryString, includeAnyLength);
    this.includeAny = checkedBinaryString;
  }

  /** includeAll */
  // public int getIncludeAllDecimalValue() {
  // int decimalValue = (int) getDecimalValue(includeAll);
  // return decimalValue;
  // }
  public String getIncludeAllBinaryString() {
    return this.includeAll;
  }

  // public void setIncludeAllDecimalValue(int decimalValue) {
  // int binaryLength = includeAllLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.includeAll = this.setDecimalValue(decimalValue, maxValue,
  // binaryLength);
  // }
  public void setIncludeAllBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, includeAllLength);
    this.includeAll = checkedBinaryString;
  }

  public void setIncludeAllBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            includeAll, startingBit, binaryString, includeAllLength);
    this.includeAll = checkedBinaryString;
  }

  /** setupPrio */
  public int getSetupPrioDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(setupPrio);
    return decimalValue;
  }

  public String getSetupPrioBinaryString() {
    return this.setupPrio;
  }

  public void setSetupPrioDecimalValue(int decimalValue) {
    int binaryLength = setupPrioLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.setupPrio = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setSetupPrioBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, setupPrioLength);
    this.setupPrio = checkedBinaryString;
  }

  public void setSetupPrioBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            setupPrio, startingBit, binaryString, setupPrioLength);
    this.setupPrio = checkedBinaryString;
  }

  /** holdingPrio */
  public int getHoldingPrioDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(holdingPrio);
    return decimalValue;
  }

  public String getHoldingPrioBinaryString() {
    return this.holdingPrio;
  }

  public void setHoldingPrioDecimalValue(int decimalValue) {
    int binaryLength = holdingPrioLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.holdingPrio = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setHoldingPrioBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, holdingPrioLength);
    this.holdingPrio = checkedBinaryString;
  }

  public void setHoldingPrioBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            holdingPrio, startingBit, binaryString, holdingPrioLength);
    this.holdingPrio = checkedBinaryString;
  }

  /** reserved */
  public int getReservedDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(reserved);
    return decimalValue;
  }

  public String getReservedBinaryString() {
    return this.reserved;
  }

  public void setReservedDecimalValue(int decimalValue) {
    int binaryLength = reservedLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.reserved = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setReservedBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, reservedLength);
    this.reserved = checkedBinaryString;
  }

  public void setReservedBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(reserved, startingBit, binaryString, reservedLength);
    this.reserved = checkedBinaryString;
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

  /** lFlag */
  public int getLFlagDecimalValue() {
    int relativeStartBit = (lFlagStartBit - flagsStartBit);
    String flagString = flags.substring(relativeStartBit, relativeStartBit + lFlagLength);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flagString);
    return decimalValue;
  }

  public String getLFlagBinaryString() {
    String binaryString = flags.substring(0, (lFlagStartBit - flagsStartBit) + lFlagLength);
    return binaryString;
  }

  public void setLFlagBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            this.flags, (lFlagStartBit - flagsStartBit), binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public String toString() {
    String excludeAnyInfo = "ExcludeAny=" + this.getExcludeAnyBinaryString();
    String includeAnyInfo = ",IncludeAny=" + this.getIncludeAnyBinaryString();
    String includeAllInfo = ",IncludeAll=" + this.getIncludeAllBinaryString();
    String setupPrioInfo = ",SetupPrio=" + this.getSetupPrioDecimalValue();
    String holdingPrioInfo = ",HoldingPrio=" + this.getHoldingPrioDecimalValue();
    String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
    String reservedInfo = ",Reserved=" + this.getReservedBinaryString();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo =
        "<LSPA:"
            + excludeAnyInfo
            + includeAnyInfo
            + includeAllInfo
            + setupPrioInfo
            + holdingPrioInfo
            + flagsInfo
            + reservedInfo
            + ">";

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String excludeAnyBinaryInfo = getExcludeAnyBinaryString();
    String includeAnyBinaryInfo = "'" + getIncludeAnyBinaryString();
    String includeAllBinaryInfo = "'" + getIncludeAllBinaryString();
    String setupPrioBinaryInfo = "'" + getSetupPrioBinaryString();
    String holdingPrioBinaryInfo = "'" + getHoldingPrioBinaryString();
    String flagsInfo = "'" + this.getFlagsBinaryString();
    String reservedBinaryInfo = "'" + getReservedBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo =
        "["
            + excludeAnyBinaryInfo
            + includeAnyBinaryInfo
            + includeAllBinaryInfo
            + setupPrioBinaryInfo
            + holdingPrioBinaryInfo
            + flagsInfo
            + reservedBinaryInfo
            + "]";

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    return "[" + NAME + "]";
  }
}
