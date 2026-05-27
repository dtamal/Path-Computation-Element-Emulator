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
 * |      Type     |     Length    |     Flags     | Reserved (MBZ)|
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                           Router ID                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                     Interface ID (32 bits)                    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class PceReportedRouteObject implements PceObjectFrame {

  private final String NAME = "Reported Route Object";

  private String type;
  private String length;
  private String reserved;
  private String routerID;
  private String interfaceID;
  private String flags;

  private PceBandwidthObject bandwidth;

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;

  private int objectLength = PceConstantValues.REPORTED_ROUTE_OBJECT_LENGTH;

  private int typeStartBit = PceConstantValues.REPORTED_ROUTE_OBJECT_TYPE_START_BIT;
  private int typeEndBit = PceConstantValues.REPORTED_ROUTE_OBJECT_TYPE_END_BIT;
  private int typeLength = PceConstantValues.REPORTED_ROUTE_OBJECT_TYPE_LENGTH;

  private int lengthStartBit = PceConstantValues.REPORTED_ROUTE_OBJECT_LENGTH_START_BIT;
  private int lengthEndBit = PceConstantValues.REPORTED_ROUTE_OBJECT_LENGTH_END_BIT;
  private int lengthLength = PceConstantValues.REPORTED_ROUTE_OBJECT_LENGTH_LENGTH;

  private int flagsStartBit = PceConstantValues.REPORTED_ROUTE_OBJECT_FLAGS_START_BIT;
  private int flagsEndBit = PceConstantValues.REPORTED_ROUTE_OBJECT_FLAGS_END_BIT;
  private int flagsLength = PceConstantValues.REPORTED_ROUTE_OBJECT_FLAGS_LENGTH;

  private int reservedStartBit = PceConstantValues.REPORTED_ROUTE_OBJECT_RESERVED_START_BIT;
  private int reservedEndBit = PceConstantValues.REPORTED_ROUTE_OBJECT_RESERVED_END_BIT;
  private int reservedLength = PceConstantValues.REPORTED_ROUTE_OBJECT_RESERVED_LENGTH;

  private int routerIDStartBit = PceConstantValues.REPORTED_ROUTE_OBJECT_ROUTER_ID_START_BIT;
  private int routerIDEndBit = PceConstantValues.REPORTED_ROUTE_OBJECT_ROUTER_ID_END_BIT;
  private int routerIDLength = PceConstantValues.REPORTED_ROUTE_OBJECT_ROUTER_ID_LENGTH;

  private int interfaceIDStartBit = PceConstantValues.REPORTED_ROUTE_OBJECT_INTERFACE_ID_START_BIT;
  private int interfaceIDEndBit = PceConstantValues.REPORTED_ROUTE_OBJECT_INTERFACE_ID_END_BIT;
  private int interfaceIDLength = PceConstantValues.REPORTED_ROUTE_OBJECT_INTERFACE_ID_LENGTH;

  public PceReportedRouteObject(PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);

    String objectString = binaryString;

    if (binaryString.length() > objectLength) {
      objectString = binaryString.substring(typeStartBit, objectLength);
      String bandwidthString = binaryString.substring(objectLength);

      PceBandwidthObject bandwithObject =
          (PceBandwidthObject) PceObjectFrameFactory.getPCEPObjectFrame(bandwidthString);

      insertPCEPBandwidthObject(bandwithObject);
    }

    this.setObjectBinaryString(objectString);
    this.updateHeaderLength();
  }

  public PceReportedRouteObject(
      PceCommonObjectHeader objectHeader,
      int type,
      int length,
      String routerID,
      String interfaceID) {
    this.setObjectHeader(objectHeader);
    this.setTypeDecimalValue(type);
    this.setLengthDecimalValue(length);
    this.setFlagsBinaryString(PceComputationFactory.generateZeroString(flagsLength));
    this.setReservedBinaryString(PceComputationFactory.generateZeroString(reservedLength));
    this.setRouterIDBinaryString(routerID);
    this.setInterfaceIDBinaryString(interfaceID);
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
    String binaryString = type + length + flags + reserved + routerID + interfaceID;
    return binaryString;
  }

  public void setObjectBinaryString(String binaryString) {
    String typeBinaryString = binaryString.substring(typeStartBit, typeEndBit + 1);
    String lengthBinaryString = binaryString.substring(lengthStartBit, lengthEndBit + 1);
    String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
    String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);
    String routerIDBinaryString = binaryString.substring(routerIDStartBit, routerIDEndBit + 1);
    String interfaceIDBinaryString =
        binaryString.substring(interfaceIDStartBit, interfaceIDEndBit + 1);

    this.setTypeBinaryString(typeBinaryString);
    this.setLengthBinaryString(lengthBinaryString);
    this.setFlagsBinaryString(flagsBinaryString);
    this.setReservedBinaryString(reservedBinaryString);
    this.setRouterIDBinaryString(routerIDBinaryString);
    this.setInterfaceIDBinaryString(interfaceIDBinaryString);
  }

  public int getObjectFrameByteLength() {

    int objectLength =
        type.length()
            + length.length()
            + flags.length()
            + reserved.length()
            + routerID.length()
            + interfaceID.length();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH;
    int objectFrameByteLength = (objectLength + headerLength) / 8;

    if (containsPCEPBandwidthObject() == true) {
      int bandwidthByteLength = this.bandwidth.getObjectFrameByteLength();

      return objectFrameByteLength + bandwidthByteLength;
    }

    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    if (containsPCEPBandwidthObject() == true) {
      String bandwidthBinaryString = this.bandwidth.getObjectFrameBinaryString();

      return headerBinaryString + objectBinaryString + bandwidthBinaryString;
    }

    return headerBinaryString + objectBinaryString;
  }

  /** Bandwidth attachment */
  public void insertPCEPBandwidthObject(PceBandwidthObject bandwidth) {
    this.bandwidth = bandwidth;

    int objectFrameByteLength = this.getObjectFrameByteLength();
    int bandwidthHeaderByteLength = bandwidth.getObjectFrameByteLength();

    this.getObjectHeader().setLengthDecimalValue(objectFrameByteLength + bandwidthHeaderByteLength);
  }

  public boolean containsPCEPBandwidthObject() {
    if (bandwidth != null) {
      return true;
    }
    return false;
  }

  public PceBandwidthObject getPCEPBandwidthObject() {
    if (containsPCEPBandwidthObject() == true) {
      return bandwidth;
    }
    return null;
  }

  /** type */
  public int getTypeDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(type);
    return decimalValue;
  }

  public String getTypeBinaryString() {
    return this.type;
  }

  public void setTypeDecimalValue(int decimalValue) {
    int binaryLength = typeLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.type = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setTypeBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, typeLength);
    this.type = checkedBinaryString;
  }

  public void setTypeBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(type, startingBit, binaryString, typeLength);
    this.type = checkedBinaryString;
  }

  /** length */
  public int getLengthDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(length);
    return decimalValue;
  }

  public String getLengthBinaryString() {
    return this.length;
  }

  public void setLengthDecimalValue(int decimalValue) {
    int binaryLength = lengthLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.length = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setLengthBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, lengthLength);
    this.length = checkedBinaryString;
  }

  public void setLengthBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(length, startingBit, binaryString, lengthLength);
    this.length = checkedBinaryString;
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

  /** routerID */
  // public int getRouterIDDecimalValue() {
  // int decimalValue = (int) getDecimalValue(routerID);
  // return decimalValue;
  // }
  public String getRouterIDBinaryString() {
    return this.routerID;
  }

  // public void setRouterIDDecimalValue(int decimalValue) {
  // int binaryLength = routerIDLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.routerID = this.setDecimalValue(decimalValue, maxValue,
  // binaryLength);
  // }
  public void setRouterIDBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, routerIDLength);
    this.routerID = checkedBinaryString;
  }

  public void setRouterIDBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(routerID, startingBit, binaryString, routerIDLength);
    this.routerID = checkedBinaryString;
  }

  /** interfaceID */
  // public int getInterfaceIDDecimalValue() {
  // int decimalValue = (int) getDecimalValue(interfaceID);
  // return decimalValue;
  // }
  public String getInterfaceIDBinaryString() {
    return this.interfaceID;
  }

  // public void setInterfaceIDDecimalValue(int decimalValue) {
  // int binaryLength = interfaceIDLength;
  // int maxValue = (int)
  // PceConstantValues.MaxValueFabrication(binaryLength);
  //
  // this.interfaceID = this.setDecimalValue(decimalValue, maxValue,
  // binaryLength);
  // }
  public void setInterfaceIDBinaryString(String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(binaryString, interfaceIDLength);
    this.interfaceID = checkedBinaryString;
  }

  public void setInterfaceIDBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(
            interfaceID, startingBit, binaryString, interfaceIDLength);
    this.interfaceID = checkedBinaryString;
  }

  /** flags */
  public int getFlagsDecimalValue() {
    int decimalValue = (int) PceComputationFactory.getDecimalValue(flags);
    return decimalValue;
  }

  public String getFlagsBinaryString() {
    return this.flags;
  }

  public void setFlagsDecimalValue(int decimalValue) {
    int binaryLength = flagsLength;
    int maxValue = (int) PceComputationFactory.MaxValueFabrication(binaryLength);

    this.flags = PceComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
  }

  public void setFlagsBinaryString(String binaryString) {
    String checkedBinaryString = PceComputationFactory.setBinaryString(binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public void setFlagsBinaryString(int startingBit, String binaryString) {
    String checkedBinaryString =
        PceComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
    this.flags = checkedBinaryString;
  }

  public String toString() {
    String typeInfo = "Type=" + this.getTypeDecimalValue();
    String lengthInfo = ",Length=" + this.getLengthDecimalValue();
    String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
    String reservedInfo = ",Reserved=" + this.getReservedBinaryString();
    String routerIDInfo = ",RouterID=" + this.getRouterIDBinaryString();
    String interfaceIDInfo = ",InterfaceID=" + this.getInterfaceIDBinaryString();

    String headerInfo = this.getObjectHeader().toString();
    String objectInfo =
        "<Reportet Route:"
            + typeInfo
            + lengthInfo
            + flagsInfo
            + reservedInfo
            + routerIDInfo
            + interfaceIDInfo
            + ">";

    if (containsPCEPBandwidthObject() == true) {
      String bandwidthInfo = this.bandwidth.toString();

      return headerInfo + objectInfo + bandwidthInfo;
    }

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {
    String typeBinaryInfo = getTypeBinaryString();
    String lengthBinaryInfo = "'" + getLengthBinaryString();
    String flagsInfo = "'" + this.getFlagsBinaryString();
    String reservedBinaryInfo = "'" + getReservedBinaryString();
    String routerIDBinaryInfo = "'" + getRouterIDBinaryString();
    String interfaceIDBinaryInfo = "'" + getInterfaceIDBinaryString();

    String headerInfo = this.getObjectHeader().binaryInformation();
    String objectInfo =
        "["
            + typeBinaryInfo
            + lengthBinaryInfo
            + flagsInfo
            + reservedBinaryInfo
            + routerIDBinaryInfo
            + interfaceIDBinaryInfo;

    if (containsPCEPBandwidthObject() == true) {
      String bandwidthInfo = this.bandwidth.binaryInformation();

      return headerInfo + objectInfo + bandwidthInfo + "]";
    }

    return headerInfo + objectInfo + "]";
  }

  public String contentInformation() {

    if (containsPCEPBandwidthObject() == true) {
      String bandwidthName = this.bandwidth.contentInformation();

      return "[" + NAME + bandwidthName + "]";
    }

    return "[" + NAME + "]";
  }
}
