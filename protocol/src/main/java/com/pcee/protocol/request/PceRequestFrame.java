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
package com.pcee.protocol.request;

import com.pcee.protocol.message.PceMessageFrame;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.impl.*;
import com.pcee.protocol.message.objectframe.impl.PceBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PceAddress;
import java.util.LinkedList;

/**
 *
 *
 * <pre>
 *   The format of a PCReq message is as follows:
 *
 *   <PCReq Message>::= <Common Header>
 *                      [<svec-list>]
 *                      <request-list>
 *
 *   where:
 *      <svec-list>::=<SVEC>[<svec-list>]
 *      <request-list>::=<request>[<request-list>]
 *      <request>::= <RP>
 *                   <END-POINTS>
 *                   [<LSPA>]
 *                   [<BANDWIDTH>]
 *                   [<metric-list>]
 *                   [<RRO>[<BANDWIDTH>]]
 *                   [<IRO>]
 *                   [<LOAD-BALANCING>]
 *
 *   <metric-list>::=<METRIC>[<metric-list>]
 * </pre>
 */
public class PceRequestFrame implements PceMessageFrame {

  public final int MESSAGE_TYPE = 3;

  LinkedList<PceSynchronizationVectorObject> SVECList;
  PceRequestParametersObject RP;
  PceEndPointsObject endPoints;
  PceLabelSwitchedPathAttributesObject LSPA;
  PceBandwidthObject bandwidth;
  LinkedList<PceMetricObject> metricList;
  PceReportedRouteObject RRO;
  PceIncludeRouteObject IRO;
  PceLoadBalancingObject loadBalancing;
  PceObjectiveFunctionObject of;

  public PceRequestFrame(PceRequestParametersObject RP, PceEndPointsObject endPoints) {
    this.RP = RP;
    this.endPoints = endPoints;
  }

  public PceRequestFrame(
      PceRequestParametersObject RP, PceEndPointsObject endPoints, PceBandwidthObject bandwidth) {
    this.RP = RP;
    this.endPoints = endPoints;
    this.bandwidth = bandwidth;
  }

  // VARIOUS CONVENIENT METHODS

  public PceRequestFrame(
      PceRequestParametersObject RP,
      PceEndPointsObject endPoints,
      PceBandwidthObject bandwidth,
      PceMetricObject metric) {
    this.RP = RP;
    this.endPoints = endPoints;
    this.bandwidth = bandwidth;
    this.metricList = new LinkedList<PceMetricObject>();
    this.insertMetricObject(metric);
  }

  public int getRequestID() {
    return RP.getRequestIDNumberDecimalValue();
  }

  public PceAddress getSourceAddress() {
    return new PceAddress(endPoints.getSourceAddressBinaryString());
  }

  public PceAddress getDestinationAddress() {
    return new PceAddress(endPoints.getDestinationAddressBinaryString());
  }

  // INSERT METHODS

  public void insertSynchronizationVectorObject(PceSynchronizationVectorObject SVECObject) {
    if (containsSynchronizationVectorObjectList()) {
      extractSynchronizationVectorObjectList().add(SVECObject);
    } else {
      SVECList = new LinkedList<PceSynchronizationVectorObject>();
      SVECList.add(SVECObject);
    }
  }

  public void insertEndPointsObject(PceEndPointsObject endPoints) {
    this.endPoints = endPoints;
  }

  public void insertSynchronizationVectorObjectList(
      LinkedList<PceSynchronizationVectorObject> SVECList) {
    this.SVECList = SVECList;
  }

  public void insertLabelSwitchedPathAttributesObject(PceLabelSwitchedPathAttributesObject LSPA) {
    this.LSPA = LSPA;
  }

  public void insertBandwidthObject(PceBandwidthObject bandwidth) {
    this.bandwidth = bandwidth;
  }

  public void insertObjectiveFunctionObject(PceObjectiveFunctionObject of) {
    this.of = of;
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

  public void insertReportedRouteObject(PceReportedRouteObject RRO) {
    this.RRO = RRO;
  }

  public void insertIncludeRouteObject(PceIncludeRouteObject IRO) {
    this.IRO = IRO;
  }

  public void insertLoadBalancingObject(PceLoadBalancingObject loadBalancing) {
    this.loadBalancing = loadBalancing;
  }

  // EXTRACT METHODS

  public LinkedList<PceSynchronizationVectorObject> extractSynchronizationVectorObjectList() {
    if (containsSynchronizationVectorObjectList()) {
      return SVECList;
    }
    return null;
  }

  public PceObjectiveFunctionObject extractObjectiveFunctionObject() {
    if (containsObjectiveFunctionObject()) return of;
    return null;
  }

  public PceEndPointsObject extractEndPointsObject() {
    if (containsEndPointsObject()) return endPoints;
    return null;
  }

  public PceLabelSwitchedPathAttributesObject extractLabelSwitchedPathAttributesObject() {
    if (containsLabelSwitchedPathAttributesObject()) {
      return LSPA;
    }
    return null;
  }

  public PceBandwidthObject extractBandwidthObject() {
    if (containsBandwidthObject()) {
      return bandwidth;
    }
    return null;
  }

  public LinkedList<PceMetricObject> extractMetricObjectList() {
    if (containsMetricObjectList()) {
      return metricList;
    }
    return null;
  }

  public PceReportedRouteObject extractReportedRouteObject() {
    if (containsReportedRouteObject()) {
      return RRO;
    }
    return null;
  }

  public PceIncludeRouteObject extractIncludeRouteObject() {
    if (containsIncludeRouteObject()) {
      return IRO;
    }
    return null;
  }

  public PceLoadBalancingObject extractLoadBalancingObject() {
    if (containsLoadBalancingObject()) {
      return loadBalancing;
    }
    return null;
  }

  // CONTAINS METHODS

  public boolean containsObjectiveFunctionObject() {
    if (of == null) return false;
    return true;
  }

  public boolean containsEndPointsObject() {
    if (endPoints == null) return false;
    return true;
  }

  public boolean containsSynchronizationVectorObjectList() {
    if (SVECList == null) {
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

  public boolean containsBandwidthObject() {
    if (bandwidth == null) {
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

  public boolean containsReportedRouteObject() {
    if (RRO == null) {
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

  public boolean containsLoadBalancingObject() {
    if (loadBalancing == null) {
      return false;
    }
    return true;
  }

  // INTERFACE METHODS

  public int getByteLength() {
    int length = 0;

    if (containsSynchronizationVectorObjectList()) {
      for (int i = 0; i < SVECList.size(); i++) {
        length += SVECList.get(i).getObjectFrameByteLength();
      }
    }

    length += RP.getObjectFrameByteLength();
    if (containsEndPointsObject()) length += endPoints.getObjectFrameByteLength();

    if (containsLabelSwitchedPathAttributesObject()) {
      length += LSPA.getObjectFrameByteLength();
    }
    if (containsBandwidthObject()) {
      length += bandwidth.getObjectFrameByteLength();
    }
    if (containsMetricObjectList()) {
      for (int i = 0; i < metricList.size(); i++) {
        length += metricList.get(i).getObjectFrameByteLength();
      }
    }
    if (containsReportedRouteObject()) {
      length += RRO.getObjectFrameByteLength();
    }
    if (containsIncludeRouteObject()) {
      length += IRO.getObjectFrameByteLength();
    }
    if (containsLoadBalancingObject()) {
      length += loadBalancing.getObjectFrameByteLength();
    }

    if (containsObjectiveFunctionObject()) {
      length += of.getObjectFrameByteLength();
    }

    return length;
  }

  public String getBinaryString() {

    StringBuffer objectsString = new StringBuffer();

    if (containsSynchronizationVectorObjectList()) {
      for (int i = 0; i < SVECList.size(); i++) {
        objectsString.append(SVECList.get(i).getObjectFrameBinaryString());
      }
    }

    objectsString.append(RP.getObjectFrameBinaryString());
    objectsString.append(endPoints.getObjectFrameBinaryString());

    if (containsLabelSwitchedPathAttributesObject()) {
      objectsString.append(LSPA.getObjectFrameBinaryString());
    }
    if (containsBandwidthObject()) {
      objectsString.append(bandwidth.getObjectFrameBinaryString());
    }
    if (containsMetricObjectList()) {
      for (int i = 0; i < metricList.size(); i++) {
        objectsString.append(metricList.get(i).getObjectFrameBinaryString());
      }
    }
    if (containsReportedRouteObject()) {
      objectsString.append(RRO.getObjectFrameBinaryString());
    }
    if (containsIncludeRouteObject()) {
      objectsString.append(IRO.getObjectFrameBinaryString());
    }
    if (containsIncludeRouteObject()) {
      objectsString.append(loadBalancing.getObjectFrameBinaryString());
    }

    if (containsObjectiveFunctionObject()) {
      objectsString.append(of.getObjectFrameBinaryString());
    }

    return objectsString.toString();
  }

  public LinkedList<PceObjectFrame> getObjectFrameLinkedList() {

    LinkedList<PceObjectFrame> requestObjects = new LinkedList<PceObjectFrame>();

    if (containsSynchronizationVectorObjectList()) {
      for (int i = 0; i < SVECList.size(); i++) {
        requestObjects.add(SVECList.get(i));
      }
    }

    requestObjects.add(RP);

    if (containsEndPointsObject()) requestObjects.add(endPoints);

    if (containsLabelSwitchedPathAttributesObject()) {
      requestObjects.add(LSPA);
    }
    if (containsBandwidthObject()) {
      requestObjects.add(bandwidth);
    }
    if (containsMetricObjectList()) {
      for (int i = 0; i < metricList.size(); i++) {
        requestObjects.add(metricList.get(i));
      }
    }
    if (containsReportedRouteObject()) {
      requestObjects.add(RRO);
    }
    if (containsIncludeRouteObject()) {
      requestObjects.add(IRO);
    }
    if (containsLoadBalancingObject()) {
      requestObjects.add(loadBalancing);
    }

    if (containsObjectiveFunctionObject()) {
      requestObjects.add(of);
    }

    return requestObjects;
  }

  public int getMessageType() {
    return MESSAGE_TYPE;
  }
}
