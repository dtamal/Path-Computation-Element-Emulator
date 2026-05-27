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

import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.objectframe.PceCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PceObjectFrame;
import com.pcee.protocol.message.objectframe.impl.*;
import com.pcee.protocol.message.objectframe.impl.PceBandwidthObject;
import java.util.LinkedList;

public class PceResponseFrameFactory {

  public static PceResponseFrame generatePathComputationResponseFrame(
      PceRequestParametersObject RP) {

    return new PceResponseFrame(RP);
  }

  public static PceResponseFrame getPathComputationResponseFrame(PceMessage message) {

    PceRequestParametersObject RP = null;
    PceNoPathObject noPath = null;
    PceLabelSwitchedPathAttributesObject LSPA = null;
    LinkedList<PceBandwidthObject> bwList = null;
    LinkedList<PceMetricObject> metricList = null;
    PceIncludeRouteObject IRO = null;
    LinkedList<PceExplicitRouteObject> EROList = null;

    LinkedList<PceObjectFrame> objectList = message.getObjectsList();

    for (int i = 0; i < objectList.size(); i++) {

      PceObjectFrame objectFrame = objectList.get(i);
      PceCommonObjectHeader objectFrameHeader = objectFrame.getObjectHeader();

      switch (objectFrameHeader.getClassDecimalValue()) {
        case 2:
          {
            RP = (PceRequestParametersObject) objectFrame;
            break;
          }

        case 3:
          {
            noPath = (PceNoPathObject) objectFrame;
            break;
          }

        case 9:
          {
            LSPA = (PceLabelSwitchedPathAttributesObject) objectFrame;
            break;
          }

        case 5:
          {
            PceBandwidthObject bw = (PceBandwidthObject) objectFrame;
            if (bwList == null) bwList = new LinkedList<PceBandwidthObject>();
            bwList.add(bw);
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

        case 10:
          {
            IRO = (PceIncludeRouteObject) objectFrame;
            break;
          }

        case 7:
          {
            PceExplicitRouteObject ERO = (PceGenericExplicitRouteObjectImpl) objectFrame;

            if (EROList != null) {
              EROList.add(ERO);
            } else {
              EROList = new LinkedList<PceExplicitRouteObject>();
              EROList.add(ERO);
            }

            break;
          }

        default:
          {
            break;
          }
      }
    }

    PceResponseFrame responseFrame = new PceResponseFrame(RP);

    if (noPath != null) {
      responseFrame.insertNoPathObject(noPath);
    }

    if (LSPA != null) {
      responseFrame.insertLabelSwitchedPathAttributesObject(LSPA);
    }

    if (bwList != null) {
      responseFrame.insertBandwidthObjectList(bwList);
    }

    if (metricList != null) {
      responseFrame.insertMetricObjectList(metricList);
    }

    if (IRO != null) {
      responseFrame.insertIncludeRouteObject(IRO);
    }

    if (EROList != null) {
      responseFrame.insertExplicitRouteObjectList(EROList);
    }

    return responseFrame;
  }
}
