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

import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.objectframe.PceCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.impl.*;
import com.pcee.protocol.message.objectframe.impl.PceBandwidthObject;
import java.util.LinkedList;

public class PceRequestFrameFactory {

  public static PceRequestFrame generatePathComputationRequestFrame(
      PceRequestParametersObject RP, PceEndPointsObject endPoints) {

    return new PceRequestFrame(RP, endPoints);
  }

  public static PceRequestFrame generatePathComputationRequestFrame(
      PceRequestParametersObject RP, PceEndPointsObject endPoints, PceBandwidthObject bandwidth) {
    return new PceRequestFrame(RP, endPoints, bandwidth);
  }

  public static PceRequestFrame getPathComputationRequestFrame(PceMessage message) {

    LinkedList<PceSynchronizationVectorObject> SVECList = null;
    PceRequestParametersObject RP = null;
    PceEndPointsObject endPoints = null;
    PceLabelSwitchedPathAttributesObject LSPA = null;
    PceBandwidthObject bandwidth = null;
    LinkedList<PceMetricObject> metricList = null;
    PceReportedRouteObject RRO = null;
    PceIncludeRouteObject IRO = null;
    PceLoadBalancingObject loadBalancing = null;
    //		PCEPGeneralizedEndPointsTNAObject generalizedEndPointTNA = null;
    LinkedList<PceObjectFrame> objectList = message.getObjectsList();

    for (int i = 0; i < objectList.size(); i++) {

      PceObjectFrame objectFrame = objectList.get(i);
      PceCommonObjectHeader objectFrameHeader = objectFrame.getObjectHeader();

      switch (objectFrameHeader.getClassDecimalValue()) {
        case 11:
          {
            PceSynchronizationVectorObject SVEC = (PceSynchronizationVectorObject) objectFrame;

            if (SVECList != null) {
              SVECList.add(SVEC);
            } else {
              SVECList = new LinkedList<PceSynchronizationVectorObject>();
              SVECList.add(SVEC);
            }

            break;
          }

        case 2:
          {
            RP = (PceRequestParametersObject) objectFrame;
            break;
          }
        case 4:
          {
            // generalizedEndPointTNA = (PCEPGeneralizedEndPointsTNAObject) objectFrame;
            endPoints = (PceEndPointsObject) objectFrame;
            break;
          }
        case 9:
          {
            LSPA = (PceLabelSwitchedPathAttributesObject) objectFrame;
            break;
          }

        case 5:
          {
            bandwidth = (PceBandwidthObject) objectFrame;
            break;
          }

        case 6:
          {
            PceMetricObject metricObject = (PceMetricObject) objectFrame;

            if (metricList != null) {
              metricList.add(metricObject);
            } else {
              metricList = new LinkedList<PceMetricObject>();
              metricList.add(metricObject);
            }

            break;
          }

        case 8:
          {
            RRO = (PceReportedRouteObject) objectFrame;
            break;
          }

        case 10:
          {
            IRO = (PceIncludeRouteObject) objectFrame;
            break;
          }

        case 14:
          {
            loadBalancing = (PceLoadBalancingObject) objectFrame;
            break;
          }

        default:
          {
            break;
          }
      }
    }

    PceRequestFrame requestFrame = new PceRequestFrame(RP, endPoints);

    if (endPoints != null) {
      requestFrame = new PceRequestFrame(RP, endPoints);
    }

    if (SVECList != null) {
      requestFrame.insertSynchronizationVectorObjectList(SVECList);
    }
    if (LSPA != null) {
      requestFrame.insertLabelSwitchedPathAttributesObject(LSPA);
    }
    if (bandwidth != null) {
      requestFrame.insertBandwidthObject(bandwidth);
    }
    if (metricList != null) {
      requestFrame.insertMetricObjectList(metricList);
    }
    if (RRO != null) {
      requestFrame.insertReportedRouteObject(RRO);
    }
    if (IRO != null) {
      requestFrame.insertIncludeRouteObject(IRO);
    }
    if (loadBalancing != null) {
      requestFrame.insertLoadBalancingObject(loadBalancing);
    }

    // if(generalizedEndPointTNA!=null){
    // requestFrame = new PceRequestFrame(RP,generalizedEndPointTNA);
    //	}

    return requestFrame;
  }

  public static PceRequestFrame generatePathComputationRequestFrame(
      PceRequestParametersObject RP,
      PceEndPointsObject endPoints,
      PceBandwidthObject bandwidth,
      PceMetricObject metric) {
    return new PceRequestFrame(RP, endPoints, bandwidth, metric);
  }
}
