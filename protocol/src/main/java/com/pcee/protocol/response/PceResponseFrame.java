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
package com.pcee.protocol.response;

import com.pcee.protocol.message.PceMessageFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.impl.*;
import com.pcee.protocol.message.objectframe.impl.PceBandwidthObject;
import java.util.LinkedList;

public class PceResponseFrame implements PceMessageFrame {

  public final int MESSAGE_TYPE = 4;

  PceRequestParametersObject RP;

  PceNoPathObject noPath;
  PceLabelSwitchedPathAttributesObject LSPA;
  LinkedList<PceMetricObject> metricList;
  PceIncludeRouteObject IRO;

  // responseFrame.getattributelist().insertlasp();

  LinkedList<PceBandwidthObject> bwList;
  LinkedList<PceExplicitRouteObject> EROList;

  public PceResponseFrame(PceRequestParametersObject RP) {
    this.RP = RP;
  }

  // VARIOUS CONVENIENT METHODS

  public int getRequestID() {
    return RP.getRequestIDNumberDecimalValue();
  }

  public String getTraversedVertexes() {
    if (containsExplicitRouteObjectList()) {
      String traversedVertexes = new String();

      for (PceExplicitRouteObject ERO : EROList) {
        String path = "[" + ERO.printPath() + "]";
        traversedVertexes += path;
      }

      return traversedVertexes;
    } else {
      return "NO PATH";
    }
  }

  // INSERT METHODS

  public void insertNoPathObject(PceNoPathObject noPath) {
    this.noPath = noPath;
  }

  public void insertLabelSwitchedPathAttributesObject(PceLabelSwitchedPathAttributesObject LSPA) {
    this.LSPA = LSPA;
  }

  public void insertBandwidthObject(PceBandwidthObject bandwidth) {
    if (containsBandwidthObjectList()) {
      this.bwList.add(bandwidth);
    } else {
      bwList = new LinkedList<PceBandwidthObject>();
      bwList.add(bandwidth);
    }
  }

  public void insertBandwidthObjectList(LinkedList<PceBandwidthObject> bwList) {
    this.bwList = bwList;
  }

  public void insertMetricObject(PceMetricObject metricObject) {
    if (containsMetricObjectList()) {
      extractMetricObjectList().add(metricObject);
    } else {
      metricList = new LinkedList<PceMetricObject>();
      metricList.add(metricObject);
    }
  }

  public void insertMetricObjectList(LinkedList<PceMetricObject> metricList) {
    this.metricList = metricList;
  }

  public void insertIncludeRouteObject(PceIncludeRouteObject IRO) {
    this.IRO = IRO;
  }

  public void insertExplicitRouteObject(PceExplicitRouteObject ERO) {
    if (containsExplicitRouteObjectList()) {
      extractExplicitRouteObjectList().add(ERO);
    } else {
      EROList = new LinkedList<PceExplicitRouteObject>();
      EROList.add(ERO);
    }
  }

  public void insertExplicitRouteObjectList(LinkedList<PceExplicitRouteObject> EROList) {
    this.EROList = EROList;
  }

  // EXTRACT METHODS

  public PceNoPathObject extractNoPathObject() {
    if (containsNoPathObject()) {
      return noPath;
    }
    return null;
  }

  public PceLabelSwitchedPathAttributesObject extractLabelSwitchedPathAttributesObject() {
    if (containsLabelSwitchedPathAttributesObject()) {
      return LSPA;
    }
    return null;
  }

  public LinkedList<PceBandwidthObject> extractBandwidthObjectList() {
    if (containsBandwidthObjectList()) { // TODO FIX This bug !!
      return bwList;
    }
    return null;
  }

  public LinkedList<PceMetricObject> extractMetricObjectList() {
    if (containsMetricObjectList()) {
      return metricList;
    }
    return null;
  }

  public PceIncludeRouteObject extractIncludeRouteObject() {
    if (containsIncludeRouteObject()) {
      return IRO;
    }
    return null;
  }

  public LinkedList<PceExplicitRouteObject> extractExplicitRouteObjectList() {
    if (containsExplicitRouteObjectList()) { // TODO FIX This bug !!
      return EROList;
    }
    return null;
  }

  // CONTAINS METHODS

  public boolean containsNoPathObject() {
    if (noPath == null) {
      return false;
    }
    return true;
  }

  public boolean containsLabelSwitchedPathAttributesObject() {
    if (LSPA == null) {
      return false;
    }
    return true;
  }

  public boolean containsBandwidthObjectList() {
    if (bwList == null) {
      return false;
    }
    return true;
  }

  public boolean containsMetricObjectList() {
    if (metricList == null) {
      return false;
    }
    return true;
  }

  public boolean containsIncludeRouteObject() {
    if (IRO == null) {
      return false;
    }
    return true;
  }

  public boolean containsExplicitRouteObjectList() {
    if (EROList == null) {
      return false;
    }
    return true;
  }

  // INTERFACE METHODS

  public int getByteLength() {
    int length = 0;

    length += RP.getObjectFrameByteLength();

    if (containsNoPathObject()) {
      length += noPath.getObjectFrameByteLength();
    }
    if (containsLabelSwitchedPathAttributesObject()) {
      length += LSPA.getObjectFrameByteLength();
    }
    if (containsBandwidthObjectList()) {
      for (int i = 0; i < bwList.size(); i++) {
        length += bwList.get(i).getObjectFrameByteLength();
      }
    }
    if (containsMetricObjectList()) {
      for (int i = 0; i < metricList.size(); i++) {
        length += metricList.get(i).getObjectFrameByteLength();
      }
    }
    if (containsIncludeRouteObject()) {
      length += IRO.getObjectFrameByteLength();
    }
    if (containsExplicitRouteObjectList()) {
      for (int i = 0; i < EROList.size(); i++) {
        length += EROList.get(i).getObjectFrameByteLength();
      }
    }
    return length;
  }

  public String getBinaryString() {

    StringBuffer objectsString = new StringBuffer();

    objectsString.append(RP.getObjectFrameBinaryString());

    if (containsNoPathObject()) {
      objectsString.append(noPath.getObjectFrameBinaryString());
    }
    if (containsLabelSwitchedPathAttributesObject()) {
      objectsString.append(LSPA.getObjectFrameBinaryString());
    }
    if (containsBandwidthObjectList()) {
      for (int i = 0; i < bwList.size(); i++) {
        objectsString.append(bwList.get(i).getObjectFrameBinaryString());
      }
    }
    if (containsMetricObjectList()) {
      for (int i = 0; i < metricList.size(); i++) {
        objectsString.append(metricList.get(i).getObjectFrameBinaryString());
      }
    }
    if (containsIncludeRouteObject()) {
      objectsString.append(IRO.getObjectFrameBinaryString());
    }
    if (containsExplicitRouteObjectList()) {
      for (int i = 0; i < EROList.size(); i++) {
        objectsString.append(EROList.get(i).getObjectFrameBinaryString());
      }
    }

    return objectsString.toString();
  }

  public LinkedList<PceObjectFrame> getObjectFrameLinkedList() {

    LinkedList<PceObjectFrame> respondObjects = new LinkedList<PceObjectFrame>();

    respondObjects.add(RP);

    if (containsNoPathObject()) {
      respondObjects.add(noPath);
    }
    if (containsLabelSwitchedPathAttributesObject()) {
      respondObjects.add(LSPA);
    }

    if (containsBandwidthObjectList()) {
      for (int i = 0; i < bwList.size(); i++) {
        respondObjects.add(bwList.get(i));
      }
    }
    if (containsMetricObjectList()) {
      for (int i = 0; i < metricList.size(); i++) {
        respondObjects.add(metricList.get(i));
      }
    }
    if (containsIncludeRouteObject()) {
      respondObjects.add(IRO);
    }
    if (containsExplicitRouteObjectList()) {
      for (int i = 0; i < EROList.size(); i++) {
        respondObjects.add(EROList.get(i));
      }
    }
    return respondObjects;
  }

  public int getMessageType() {
    return MESSAGE_TYPE;
  }
}
