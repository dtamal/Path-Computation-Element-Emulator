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
import com.pcee.protocol.message.objectframe.impl.erosubobjects.*;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EroSubobjects;
import java.util.ArrayList;

/**
 *
 *
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                        (Subobjects)                          //
 * |                                                               |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

// TODO Generate methods to split and identify objects
public class PceGenericExplicitRouteObjectImpl extends PceExplicitRouteObject {

  private final String NAME = "Explicit Route Object";

  private PceCommonObjectHeader objectHeader;
  // private LinkedList<PceTlvObject> tlvList;
  private ArrayList<EroSubobjects> traversedVertexList = new ArrayList<EroSubobjects>();

  // private String subObjectsString;

  public PceGenericExplicitRouteObjectImpl(
      PceCommonObjectHeader objectHeader, String binaryString) {
    this.setObjectHeader(objectHeader);
    this.setObjectBinaryString(binaryString);
    this.updateHeaderLength();
  }

  public PceGenericExplicitRouteObjectImpl(
      PceCommonObjectHeader objectHeader, ArrayList<EroSubobjects> traversedVertexList) {
    this.setObjectHeader(objectHeader);
    this.setTraversedVertexList(traversedVertexList);
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

    StringBuffer subObjectsStringBuffer = new StringBuffer();

    for (int i = 0; i < traversedVertexList.size(); i++) {
      EroSubobjects address = traversedVertexList.get(i);
      subObjectsStringBuffer.append(address.getObjectBinaryString());
    }

    return subObjectsStringBuffer.toString();
  }

  public void setObjectBinaryString(String binaryString) {

    ArrayList<EroSubobjects> vertexList = new ArrayList<EroSubobjects>();

    while (binaryString.length() > 0) {
      String lengthBinaryString = binaryString.substring(8, 16);
      int length = (int) PceComputationFactory.getDecimalValue(lengthBinaryString);
      length = length * 8;
      String tempString = binaryString.substring(0, length);
      int type = (int) PceComputationFactory.getDecimalValue(tempString.substring(1, 8));
      EroSubobjects temp = null;
      if (type == EroSubobjects.PCEPIPv4AddressType) {
        temp = new PceAddress(tempString, true);
      } else if (type == EroSubobjects.PCEPUnnumberedInterfaceType) {
        temp = new EroUnnumberedInterface(tempString);
      } else if (type == EroSubobjects.PCEPMLDelimiterType) {
        temp = new MlDelimiter(tempString);
      } else if (type == EroSubobjects.PCEPLabelEROSubobjectType) {
        temp = LabelEroSubobject.getObjectFromBinaryString(tempString);
      }

      if (temp == null)
        System.out.println(
            "["
                + NAME
                + "] Problem in parsing the ERO Subobject in the setObjectBinaryString Function");

      vertexList.add(temp);

      binaryString = binaryString.substring(length);
    }

    setTraversedVertexList(vertexList);
  }

  public int getObjectFrameByteLength() {
    int objectsBinaryLength = this.getTraversedVertexListBinaryLength();
    int headerLength = PceConstantValues.COMMON_OBJECT_HEADER_LENGTH / 8;
    int objectFrameByteLength = objectsBinaryLength + headerLength;

    return objectFrameByteLength;
  }

  public String getObjectFrameBinaryString() {
    String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
    String objectBinaryString = this.getObjectBinaryString();

    return headerBinaryString + objectBinaryString;
  }

  public void setTraversedVertexList(ArrayList<EroSubobjects> subObjects) {
    this.traversedVertexList = subObjects;
  }

  public ArrayList<EroSubobjects> getTraversedVertexList() {
    return this.traversedVertexList;
  }

  private int getTraversedVertexListBinaryLength() {
    int length = 0;
    for (int i = 0; i < traversedVertexList.size(); i++) {
      length += ((traversedVertexList.get(i).getByteLength()));
    }
    return length;
  }

  public String printPath() {
    String traversedVertexesList = new String();

    for (EroSubobjects address : traversedVertexList) {
      if (address instanceof PceAddress)
        traversedVertexesList =
            traversedVertexesList + ((PceAddress) address).getIPv4Address(false) + "-";
      else if (address instanceof EroUnnumberedInterface) {
        traversedVertexesList =
            traversedVertexesList
                + "UI-"
                + ((EroUnnumberedInterface) address).getRouterIDDecimalValue()
                + ":"
                + ((EroUnnumberedInterface) address).getInterfaceIDDecimalValue()
                + "-";
      } else if (address instanceof MlDelimiter) {
        traversedVertexesList = traversedVertexesList + "ML-";
      }
    }

    return traversedVertexesList;
  }

  // public String getTraversedVertexes() {
  // String traversedVertexesList = new String();
  //
  // for (int i = 0; i < traversedVertexList.size(); i++) {
  //
  // traversedVertexesList = traversedVertexesList +
  // traversedVertexList.get(i).getAddress() + "-->";
  //
  // if (i != traversedVertexList.size()) {
  // traversedVertexesList = traversedVertexesList + "--";
  // }
  // }
  //
  // return traversedVertexesList;
  // }

  public String toString() {

    String headerInfo = this.getObjectHeader().toString();

    StringBuffer objectInfo = new StringBuffer();

    objectInfo.append("<Include Route Object:");
    for (int i = 0; i < traversedVertexList.size(); i++) {
      objectInfo.append(traversedVertexList.get(i).toString());
    }
    objectInfo.append(">");

    return headerInfo + objectInfo;
  }

  public String binaryInformation() {

    String headerInfo = this.getObjectHeader().binaryInformation();

    StringBuffer objectInfo = new StringBuffer();

    for (int i = 0; i < traversedVertexList.size(); i++) {
      objectInfo.append(traversedVertexList.get(i).binaryInformation());
    }

    return headerInfo + objectInfo;
  }

  public String contentInformation() {
    String EROName = "[" + NAME;
    String subObjectsName = new String();

    for (EroSubobjects address : traversedVertexList) {
      subObjectsName = subObjectsName + address.contentInformation();
    }

    return EROName + subObjectsName + "]";
  }

  public static void main(String[] args) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(7, 1, "1", "0");

    ArrayList<EroSubobjects> temp = new ArrayList<EroSubobjects>();
    temp.add(new PceAddress("192.168.1.2", false));
    temp.add(new PceAddress("192.168.1.3", false));
    temp.add(new MlDelimiter());
    PceGenericExplicitRouteObjectImpl a = new PceGenericExplicitRouteObjectImpl(objectHeader, temp);

    System.out.println(a.getObjectFrameBinaryString());

    String header = a.getObjectFrameBinaryString().substring(0, 32);

    PceCommonObjectHeader newHeader = new PceCommonObjectHeader(header);
    System.out.println(a.getObjectFrameBinaryString().substring(32).length());
    PceGenericExplicitRouteObjectImpl b =
        new PceGenericExplicitRouteObjectImpl(
            newHeader, a.getObjectFrameBinaryString().substring(32));
    System.out.println(b);
  }
}
