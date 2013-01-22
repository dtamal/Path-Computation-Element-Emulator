/**
 *  This file is part of Path Computation Element Emulator (PCEE).
 *
 *  PCEE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PCEE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PCEE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcee.protocol.message.objectframe;

import java.util.ArrayList;
import java.util.LinkedList;
import com.pcee.common.RequestID;
import com.pcee.common.SessionID;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.objectframe.impl.*;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

public class PCEPObjectFrameFactory {

	public static PCEPOpenObject generatePCEPOpenObject(String pFlag, String iFlag, int keepAlive, int deadTimer) {

		int sessionID = SessionID.getInstance().getID();

		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(1, 1, pFlag, iFlag);
		PCEPOpenObject object = new PCEPOpenObject(objectHeader, 1, keepAlive, deadTimer, sessionID);

		return object;
	}

	public static PCEPRequestParametersObject generatePCEPRequestParametersObject(String pFlag, String iFlag, String oFlag, String bFlag, String rFlag, String priFlag) {

		String requestIDNumber = Long.toString(RequestID.getInstance().getID());

		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(2, 1, pFlag, iFlag);
		PCEPRequestParametersObject object = new PCEPRequestParametersObject(objectHeader, oFlag, bFlag, rFlag, priFlag, requestIDNumber);

		return object;
	}

	public static PCEPRequestParametersObject generatePCEPRequestParametersObject(String pFlag, String iFlag, String oFlag, String bFlag, String rFlag, String priFlag, String requestID) {

		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(2, 1, pFlag, iFlag);
		PCEPRequestParametersObject object = new PCEPRequestParametersObject(objectHeader, oFlag, bFlag, rFlag, priFlag, requestID);

		return object;
	}

	public static PCEPNoPathObject generatePCEPNoPathObject(String pFlag, String iFlag, int natureOfIssue, String constraintsFlag) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(3, 1, pFlag, iFlag);
		PCEPNoPathObject object = new PCEPNoPathObject(objectHeader, natureOfIssue, constraintsFlag);

		return object;
	}

	public static PCEPEndPointsObject generatePCEPEndPointsObject(String pFlag, String iFlag, PCEPAddress sourceAddress, PCEPAddress destinationAddress) {
		// TODO IPv6
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(4, 1, pFlag, iFlag);
		PCEPEndPointsObject object = new PCEPEndPointsObject(objectHeader, sourceAddress, destinationAddress);

		return object;
	}

	public static PCEPBandwidthObject generatePCEPBandwidthObject(String pFlag, String iFlag, float bandwidth) {
		// TODO Implement Type 2
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(5, 1, pFlag, iFlag);
		PCEPBandwidthObject object = new PCEPBandwidthObject(objectHeader, bandwidth);

		return object;
	}

	public static PCEPMetricObject generatePCEPMetricObject(String pFlag, String iFlag, String cFlag, String bFlag, int type, float metricValue) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(6, 1, pFlag, iFlag);
		PCEPMetricObject object = new PCEPMetricObject(objectHeader, cFlag, bFlag, type, metricValue);

		return object;
	}

	public static PCEPExplicitRouteObject generatePCEPExplicitRouteObject(String pFlag, String iFlag, ArrayList<EROSubobjects> subObjects) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(7, 1, pFlag, iFlag);
		PCEPExplicitRouteObject object = new PCEPGenericExplicitRouteObjectImpl(objectHeader, subObjects);

		return object;
	}

	public static PCEPReportedRouteObject generatePCEPReportedRouteObject(String pFlag, String iFlag, int type, int length, String routerID, String interfaceID) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(8, 1, pFlag, iFlag);
		PCEPReportedRouteObject object = new PCEPReportedRouteObject(objectHeader, type, length, routerID, interfaceID);

		return object;
	}

	public static PCEPLabelSwitchedPathAttributesObject generatePCEPLabelSwitchedPathAttributesObject(String pFlag, String iFlag, String excludeAny, String includeAny, String includeAll, int setupPrio, int holdingPrio, String lFlag) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(9, 1, pFlag, iFlag);
		PCEPLabelSwitchedPathAttributesObject object = new PCEPLabelSwitchedPathAttributesObject(objectHeader, excludeAny, includeAny, includeAll, setupPrio, holdingPrio, lFlag);

		return object;
	}

	public static PCEPIncludeRouteObject generatePCEPIncludeRouteObject(String pFlag, String iFlag, LinkedList<PCEPObjectFrame> subObjects) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(10, 1, pFlag, iFlag);
		PCEPIncludeRouteObject object = new PCEPIncludeRouteObject(objectHeader, subObjects);

		return object;
	}

	public static PCEPSynchronizationVectorObject generatePCEPSynchronizationVectorObject(String pFlag, String iFlag, String sFlag, String nFlag, String lFlag, LinkedList<String> requestIDNumbers) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(11, 1, pFlag, iFlag);
		PCEPSynchronizationVectorObject object = new PCEPSynchronizationVectorObject(objectHeader, sFlag, nFlag, lFlag, requestIDNumbers);

		return object;
	}

	public static PCEPNoVertexObject generatePCEPNoVertexObject(String pFlag, String iFlag, int natureOfIssue, String constraintsFlag) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(12, 1, pFlag, iFlag);
		PCEPNoVertexObject object = new PCEPNoVertexObject(objectHeader, natureOfIssue, constraintsFlag);

		return object;
	}

	public static PCEPErrorObject generatePCEPErrorObject(String pFlag, String iFlag, int type, int value) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(13, 1, pFlag, iFlag);
		PCEPErrorObject object = new PCEPErrorObject(objectHeader, type, value);

		return object;
	}

	public static PCEPLoadBalancingObject generatePCEPLoadBalancingObject(String pFlag, String iFlag, int maxLSP, String minBandwidth) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(14, 1, pFlag, iFlag);
		PCEPLoadBalancingObject object = new PCEPLoadBalancingObject(objectHeader, maxLSP, minBandwidth);

		return object;
	}

	public static PCEPCloseObject generatePCEPCloseObject(String pFlag, String iFlag, int reason) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(15, 1, pFlag, iFlag);
		PCEPCloseObject object = new PCEPCloseObject(objectHeader, reason);

		return object;
	}

	public static PCEPTNASourceObject generatePCEPTNASourceObject(String pFlag, String iFlag, int type, int length, int addrLength, int reserved, PCEPAddress sourceIP) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(17, 1, pFlag, iFlag);
		PCEPTNASourceObject object = new PCEPTNASourceObject(objectHeader, type, length, addrLength, reserved, sourceIP);

		return object;
	}

	public static PCEPTNADestinationObject generatePCEPTNADestinationObjct(String pFlag, String iFlag, int type, int length, int addrLength, int reserved, PCEPAddress destinationIP) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(18, 1, pFlag, iFlag);
		PCEPTNADestinationObject object = new PCEPTNADestinationObject(objectHeader, type, length, addrLength, reserved, destinationIP);

		return object;
	}

	public static PCEPGeneralizedEndPointsTNAObject generatePCEPGeneralizedEndPointsTNAObject(String pFlag, String iFlag, int reserved, int endPointType, PCEPTNASourceObject sourcePoint, PCEPTNADestinationObject destinationPoint) {
		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(4, 5, pFlag, iFlag);
		PCEPGeneralizedEndPointsTNAObject object = new PCEPGeneralizedEndPointsTNAObject(objectHeader, reserved, endPointType, sourcePoint, destinationPoint);

		return object;
	}

	public static LinkedList<PCEPObjectFrame> PCEPObjectFabrication(String objectsRawString) {
		LinkedList<String> objectStringList = splitObjectsRawString(objectsRawString);

		LinkedList<PCEPObjectFrame> objectArray = new LinkedList<PCEPObjectFrame>();

		for (int i = 0; i < objectStringList.size(); i++) {
			String currentBinaryString = objectStringList.get(i);
			PCEPObjectFrame currentObjectFrame = getPCEPObjectFrame(currentBinaryString);
			objectArray.add(currentObjectFrame);
		}

		return objectArray;
	}

	public static void main(String[] args) {
		String objectString = "00000000100000001000000000000000";
		String length = objectString.substring(8, 16);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(length);
		System.out.println(decimalValue);
	}

	/**
	 * @param objectString
	 * @return
	 */
	public static PCEPObjectFrame getPCEPObjectFrame(String objectString) {
		String objectHeaderString = objectString.substring(0, PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH);
		objectString = objectString.substring(PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH);

		PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(objectHeaderString);

		switch (objectHeader.getClassDecimalValue()) {
		case 1:
			return new PCEPOpenObject(objectHeader, objectString);
		case 2:
			return new PCEPRequestParametersObject(objectHeader, objectString);
		case 3:
			return new PCEPNoPathObject(objectHeader, objectString);
		case 4:
			return new PCEPEndPointsObject(objectHeader, objectString);
		case 5:
			return new PCEPBandwidthObject(objectHeader, objectString);
		case 6:
			return new PCEPMetricObject(objectHeader, objectString);
		case 7:
			return new PCEPGenericExplicitRouteObjectImpl(objectHeader, objectString);
		case 8:
			return new PCEPReportedRouteObject(objectHeader, objectString);
		case 9:
			return new PCEPLabelSwitchedPathAttributesObject(objectHeader, objectString);
		case 10:
			return new PCEPIncludeRouteObject(objectHeader, objectString);
		case 11:
			return new PCEPSynchronizationVectorObject(objectHeader, objectString);
		case 12:
			return new PCEPNoVertexObject(objectHeader, objectString);
		case 13:
			return new PCEPErrorObject(objectHeader, objectString);
		case 14:
			return new PCEPLoadBalancingObject(objectHeader, objectString);
		case 15:
			return new PCEPCloseObject(objectHeader, objectString);
		case 17:
			return new PCEPTNASourceObject(objectHeader, objectString);
		case 18:
			return new PCEPTNADestinationObject(objectHeader, objectString);
		default:
			Logger.logWarning("Error at switch(objectHeader.getClassDecimalValue()). Value = " + objectHeader.getClassDecimalValue());
			return null;
		}
	}

	/**
	 * Splits the rawString by analysing the length Value of the objectHeader!
	 */
	public static LinkedList<String> splitObjectsRawString(String objectsRawString) {
		LinkedList<String> objectStringList = new LinkedList<String>();

		while (objectsRawString.length() > 0) {

			String objectHeaderString = objectsRawString.substring(0, PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH);
			PCEPCommonObjectHeader objectHeader = new PCEPCommonObjectHeader(objectHeaderString);
			int objectHeaderLength = (objectHeader.getLengthDecimalValue()) * 8;

			String objectString = objectsRawString.substring(0, objectHeaderLength);
			objectStringList.add(objectString);

			objectsRawString = objectsRawString.substring(objectHeaderLength);
		}

		return objectStringList;

	}

	public static void log(String logString) {
		System.out.println("PCEPObjectFrameFactory::: " + logString);
	}

}
