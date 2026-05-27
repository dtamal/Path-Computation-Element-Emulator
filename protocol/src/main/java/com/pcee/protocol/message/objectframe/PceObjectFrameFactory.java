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
package com.pcee.protocol.message.objectframe;

import com.pcee.common.RequestId;
import com.pcee.common.SessionId;
import com.pcee.protocol.message.PceComputationFactory;
import com.pcee.protocol.message.PceConstantValues;
import com.pcee.protocol.message.objectframe.impl.*;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EroSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PceAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PceObjectFrameFactory {

  private static final Logger logger = LoggerFactory.getLogger(PceObjectFrameFactory.class);

  public static PceOpenObject generatePCEPOpenObject(
      String pFlag, String iFlag, int keepAlive, int deadTimer) {

    int sessionID = SessionId.INSTANCE.getId();

    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(1, 1, pFlag, iFlag);
    PceOpenObject object = new PceOpenObject(objectHeader, 1, keepAlive, deadTimer, sessionID);

    return object;
  }

  public static PceRequestParametersObject generatePCEPRequestParametersObject(
      String pFlag, String iFlag, String oFlag, String bFlag, String rFlag, String priFlag) {

    String requestIDNumber = Long.toString(RequestId.INSTANCE.getId());

    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(2, 1, pFlag, iFlag);
    PceRequestParametersObject object =
        new PceRequestParametersObject(objectHeader, oFlag, bFlag, rFlag, priFlag, requestIDNumber);

    return object;
  }

  public static PceRequestParametersObject generatePCEPRequestParametersObject(
      String pFlag,
      String iFlag,
      String oFlag,
      String bFlag,
      String rFlag,
      String priFlag,
      String requestID) {

    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(2, 1, pFlag, iFlag);
    PceRequestParametersObject object =
        new PceRequestParametersObject(objectHeader, oFlag, bFlag, rFlag, priFlag, requestID);

    return object;
  }

  public static PceNoPathObject generatePCEPNoPathObject(
      String pFlag, String iFlag, int natureOfIssue, String constraintsFlag) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(3, 1, pFlag, iFlag);
    PceNoPathObject object = new PceNoPathObject(objectHeader, natureOfIssue, constraintsFlag);

    return object;
  }

  public static PceEndPointsObject generatePCEPEndPointsObject(
      String pFlag, String iFlag, PceAddress sourceAddress, PceAddress destinationAddress) {
    // TODO IPv6
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(4, 1, pFlag, iFlag);
    PceEndPointsObject object =
        new PceEndPointsObject(objectHeader, sourceAddress, destinationAddress);

    return object;
  }

  public static PceBandwidthObject generatePCEPBandwidthObject(
      String pFlag, String iFlag, float bandwidth) {
    // TODO Implement Type 2
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(5, 1, pFlag, iFlag);
    PceBandwidthObject object = new PceBandwidthObject(objectHeader, bandwidth);

    return object;
  }

  public static PceMetricObject generatePCEPMetricObject(
      String pFlag, String iFlag, String cFlag, String bFlag, int type, float metricValue) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(6, 1, pFlag, iFlag);
    PceMetricObject object = new PceMetricObject(objectHeader, cFlag, bFlag, type, metricValue);

    return object;
  }

  public static PceExplicitRouteObject generatePCEPExplicitRouteObject(
      String pFlag, String iFlag, ArrayList<EroSubobjects> subObjects) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(7, 1, pFlag, iFlag);
    PceExplicitRouteObject object = new PceGenericExplicitRouteObjectImpl(objectHeader, subObjects);

    return object;
  }

  public static PceReportedRouteObject generatePCEPReportedRouteObject(
      String pFlag, String iFlag, int type, int length, String routerID, String interfaceID) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(8, 1, pFlag, iFlag);
    PceReportedRouteObject object =
        new PceReportedRouteObject(objectHeader, type, length, routerID, interfaceID);

    return object;
  }

  public static PceLabelSwitchedPathAttributesObject generatePCEPLabelSwitchedPathAttributesObject(
      String pFlag,
      String iFlag,
      String excludeAny,
      String includeAny,
      String includeAll,
      int setupPrio,
      int holdingPrio,
      String lFlag) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(9, 1, pFlag, iFlag);
    PceLabelSwitchedPathAttributesObject object =
        new PceLabelSwitchedPathAttributesObject(
            objectHeader, excludeAny, includeAny, includeAll, setupPrio, holdingPrio, lFlag);

    return object;
  }

  public static PceIncludeRouteObject generatePCEPIncludeRouteObject(
      String pFlag, String iFlag, LinkedList<PceObjectFrame> subObjects) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(10, 1, pFlag, iFlag);
    PceIncludeRouteObject object = new PceIncludeRouteObject(objectHeader, subObjects);

    return object;
  }

  public static PceSynchronizationVectorObject generatePCEPSynchronizationVectorObject(
      String pFlag,
      String iFlag,
      String sFlag,
      String nFlag,
      String lFlag,
      LinkedList<String> requestIDNumbers) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(11, 1, pFlag, iFlag);
    PceSynchronizationVectorObject object =
        new PceSynchronizationVectorObject(objectHeader, sFlag, nFlag, lFlag, requestIDNumbers);

    return object;
  }

  public static PceErrorObject generatePCEPErrorObject(
      String pFlag, String iFlag, int type, int value) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(13, 1, pFlag, iFlag);
    PceErrorObject object = new PceErrorObject(objectHeader, type, value);

    return object;
  }

  public static PceLoadBalancingObject generatePCEPLoadBalancingObject(
      String pFlag, String iFlag, int maxLSP, String minBandwidth) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(14, 1, pFlag, iFlag);
    PceLoadBalancingObject object = new PceLoadBalancingObject(objectHeader, maxLSP, minBandwidth);

    return object;
  }

  public static PceCloseObject generatePCEPCloseObject(String pFlag, String iFlag, int reason) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(15, 1, pFlag, iFlag);
    PceCloseObject object = new PceCloseObject(objectHeader, reason);

    return object;
  }

  public static PceObjectiveFunctionObject generatePCEPObjectiveFunctionObject(
      String pFlag, String iFlag, int ofCode) {
    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(21, 1, pFlag, iFlag);
    PceObjectiveFunctionObject object = new PceObjectiveFunctionObject(objectHeader, ofCode);
    return object;
  }

  public static LinkedList<PceObjectFrame> PCEPObjectFabrication(String objectsRawString) {
    LinkedList<String> objectStringList = splitObjectsRawString(objectsRawString);

    LinkedList<PceObjectFrame> objectArray = new LinkedList<PceObjectFrame>();

    for (int i = 0; i < objectStringList.size(); i++) {
      String currentBinaryString = objectStringList.get(i);
      PceObjectFrame currentObjectFrame = getPCEPObjectFrame(currentBinaryString);
      objectArray.add(currentObjectFrame);
    }

    return objectArray;
  }

  public static void main(String[] args) {
    String objectString = "00000000100000001000000000000000";
    String length = objectString.substring(8, 16);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(length);
    System.out.println(decimalValue);
  }

  /**
   * @param objectString
   * @return
   */
  public static PceObjectFrame getPCEPObjectFrame(String objectString) {
    String objectHeaderString =
        objectString.substring(0, PceConstantValues.COMMON_OBJECT_HEADER_LENGTH);
    objectString = objectString.substring(PceConstantValues.COMMON_OBJECT_HEADER_LENGTH);

    PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(objectHeaderString);

    switch (objectHeader.getClassDecimalValue()) {
      case 1:
        return new PceOpenObject(objectHeader, objectString);
      case 2:
        return new PceRequestParametersObject(objectHeader, objectString);
      case 3:
        return new PceNoPathObject(objectHeader, objectString);
      case 4:
        return new PceEndPointsObject(objectHeader, objectString);
      case 5:
        return new PceBandwidthObject(objectHeader, objectString);
      case 6:
        return new PceMetricObject(objectHeader, objectString);
      case 7:
        return new PceGenericExplicitRouteObjectImpl(objectHeader, objectString);
      case 8:
        return new PceReportedRouteObject(objectHeader, objectString);
      case 9:
        return new PceLabelSwitchedPathAttributesObject(objectHeader, objectString);
      case 10:
        return new PceIncludeRouteObject(objectHeader, objectString);
      case 13:
        return new PceErrorObject(objectHeader, objectString);
      case 14:
        return new PceLoadBalancingObject(objectHeader, objectString);
      case 15:
        return new PceCloseObject(objectHeader, objectString);
      case 21:
        return new PceObjectiveFunctionObject(objectHeader, objectString);
      default:
        logger.error(
            "Error at switch(objectHeader.getClassDecimalValue()). Value = "
                + objectHeader.getClassDecimalValue());
        return null;
    }
  }

  /** Splits the rawString by analysing the length Value of the objectHeader! */
  public static LinkedList<String> splitObjectsRawString(String objectsRawString) {

    LinkedList<String> objectStringList = new LinkedList<String>();

    while (objectsRawString.length() > 0) {
      String objectHeaderString =
          objectsRawString.substring(0, PceConstantValues.COMMON_OBJECT_HEADER_LENGTH);
      PceCommonObjectHeader objectHeader = new PceCommonObjectHeader(objectHeaderString);
      int objectHeaderLength = (objectHeader.getLengthDecimalValue()) * 8;

      String objectString = objectsRawString.substring(0, objectHeaderLength);
      objectStringList.add(objectString);

      objectsRawString = objectsRawString.substring(objectHeaderLength);
    }

    return objectStringList;
  }
}
