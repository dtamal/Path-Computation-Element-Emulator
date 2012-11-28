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

package com.pcee.protocol.response;

import java.util.LinkedList;

import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.PCEPITResourceObject;
import com.pcee.protocol.message.objectframe.impl.PCEPIncludeRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPLabelSwitchedPathAttributesObject;
import com.pcee.protocol.message.objectframe.impl.PCEPMetricObject;
import com.pcee.protocol.message.objectframe.impl.PCEPNoPathObject;
import com.pcee.protocol.message.objectframe.impl.PCEPNoVertexObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;

public class PCEPResponseFrameFactory {

	public static PCEPResponseFrame generatePathComputationRequestFrame(PCEPRequestParametersObject RP) {

		PCEPResponseFrame responseFrame = new PCEPResponseFrame(RP);

		return responseFrame;
	}

	public static PCEPResponseFrame getITResourceResponseFrame(PCEPMessage message) {
		PCEPRequestParametersObject RP = null;
		LinkedList<PCEPExplicitRouteObject> EROList = null;
		PCEPITResourceObject itResourceObject = null;
		PCEPNoVertexObject noVertexObject = null;
		LinkedList<PCEPObjectFrame> objectList = message.getObjectsList();

		for (int i = 0; i < objectList.size(); i++) {
			PCEPObjectFrame objectFrame = objectList.get(i);
			PCEPCommonObjectHeader objectFrameHeader = objectFrame.getObjectHeader();

			switch (objectFrameHeader.getClassDecimalValue()) {
			case 2:
				RP = (PCEPRequestParametersObject) objectFrame;
				break;
			case 7:
				PCEPExplicitRouteObject ERO = (PCEPGenericExplicitRouteObjectImpl) objectFrame;
				if (EROList != null)
					EROList.add(ERO);
				else {
					EROList = new LinkedList<PCEPExplicitRouteObject>();
					EROList.add(ERO);
				}
				break;
			case 12:
				noVertexObject = (PCEPNoVertexObject) objectFrame;
				break;
			case 16:
				itResourceObject = (PCEPITResourceObject) objectFrame;
			}
		}

		PCEPResponseFrame responseFrame = new PCEPResponseFrame(RP);

		if (EROList != null) {
		    System.out.println("EROList != null in PCEPResponseFrameFactory");
			responseFrame.insertExplicitRouteObjectList(EROList);
		}

		if (noVertexObject != null) {
		    System.out.println("noVertexObject != null in PCEPResponseFrameFactory");
			responseFrame.insertNoVertexObject(noVertexObject);
		}

		if (itResourceObject != null) {
			responseFrame.insertITResourceObject(itResourceObject);
		}

		return responseFrame;
	}

	public static PCEPResponseFrame getPathComputationResponseFrame(PCEPMessage message) {

		PCEPRequestParametersObject RP = null;
		PCEPNoPathObject noPath = null;
		PCEPLabelSwitchedPathAttributesObject LSPA = null;
		LinkedList<PCEPBandwidthObject> bwList = null;
		LinkedList<PCEPMetricObject> metricList = null;
		PCEPIncludeRouteObject IRO = null;
		LinkedList<PCEPExplicitRouteObject> EROList = null;

		LinkedList<PCEPObjectFrame> objectList = message.getObjectsList();

		for (int i = 0; i < objectList.size(); i++) {

			PCEPObjectFrame objectFrame = objectList.get(i);
			PCEPCommonObjectHeader objectFrameHeader = objectFrame.getObjectHeader();

			switch (objectFrameHeader.getClassDecimalValue()) {

			case 2: {
				RP = (PCEPRequestParametersObject) objectFrame;
				break;
			}

			case 3: {
				noPath = (PCEPNoPathObject) objectFrame;
				break;
			}

			case 9: {
				LSPA = (PCEPLabelSwitchedPathAttributesObject) objectFrame;
				break;
			}

			case 5: {
				PCEPBandwidthObject bw = (PCEPBandwidthObject) objectFrame;
				if (bwList == null)
					bwList = new LinkedList<PCEPBandwidthObject>();
				bwList.add(bw);
				break;
			}

			case 6: {
				PCEPMetricObject metricObject = (PCEPMetricObject) objectFrame;

				if (metricList != null) {
					metricList.add(metricObject);
				} else {
					metricList = new LinkedList<PCEPMetricObject>();
					metricList.add(metricObject);
				}

				break;
			}

			case 10: {
				IRO = (PCEPIncludeRouteObject) objectFrame;
				break;
			}

			case 7: {
				PCEPExplicitRouteObject ERO = (PCEPGenericExplicitRouteObjectImpl) objectFrame;

				if (EROList != null) {
					EROList.add(ERO);
				} else {
					EROList = new LinkedList<PCEPExplicitRouteObject>();
					EROList.add(ERO);
				}

				break;
			}

			default: {
				break;
			}
			}
		}

		PCEPResponseFrame responseFrame = new PCEPResponseFrame(RP);

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

	public static void log(String logString) {
		System.out.println("PCEPResponseFrameFactory:::" + logString);
	}
}
