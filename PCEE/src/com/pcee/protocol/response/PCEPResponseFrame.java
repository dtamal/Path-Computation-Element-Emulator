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

import com.pcee.protocol.message.PCEPMessageFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPITResourceObject;
import com.pcee.protocol.message.objectframe.impl.PCEPIncludeRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPLabelSwitchedPathAttributesObject;
import com.pcee.protocol.message.objectframe.impl.PCEPMetricObject;
import com.pcee.protocol.message.objectframe.impl.PCEPNoPathObject;
import com.pcee.protocol.message.objectframe.impl.PCEPNoVertexObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;

public class PCEPResponseFrame implements PCEPMessageFrame {

	public final int MESSAGE_TYPE = 4;

	PCEPRequestParametersObject RP;

	PCEPNoPathObject noPath;
	PCEPLabelSwitchedPathAttributesObject LSPA;
	// PCEPBandwidthObject bandwidth;
	LinkedList<PCEPMetricObject> metricList;
	PCEPIncludeRouteObject IRO;
	PCEPITResourceObject itResource;
	LinkedList<PCEPBandwidthObject> bwList;
	LinkedList<PCEPExplicitRouteObject> EROList;
	PCEPNoVertexObject noVertex;

	public PCEPResponseFrame(PCEPRequestParametersObject RP) {
		this.RP = RP;
	}

	// VARIOUS CONVENIENT METHODS

	public int getRequestID() {
		return RP.getRequestIDNumberDecimalValue();
	}

	public String getTraversedVertexes() {
		if (containsExplicitRouteObjectList()) {
			String traversedVertexes = new String();

			for (PCEPExplicitRouteObject ERO : EROList) {
				String path = "[" + ERO.printPath() + "]";
				traversedVertexes += path;
			}

			return traversedVertexes;
		} else {
			return "NO PATH";
		}
	}

	// INSERT METHODS

	public void insertNoPathObject(PCEPNoPathObject noPath) {
		this.noPath = noPath;
	}

	public void insertNoVertexObject(PCEPNoVertexObject noVertex) {
		this.noVertex = noVertex;
	}

	public void insertLabelSwitchedPathAttributesObject(PCEPLabelSwitchedPathAttributesObject LSPA) {
		this.LSPA = LSPA;
	}

	public void insertBandwidthObject(PCEPBandwidthObject bandwidth) {
		if (containsBandwidthObjectList()) {
			this.bwList.add(bandwidth);
		} else {
			bwList = new LinkedList<PCEPBandwidthObject>();
			bwList.add(bandwidth);
		}

	}

	public void insertBandwidthObjectList(LinkedList<PCEPBandwidthObject> bwList) {
		this.bwList = bwList;
	}

	public void insertMetricObject(PCEPMetricObject metricObject) {
		if (containsMetricObjectList()) {
			extractMetricObjectList().add(metricObject);
		} else {
			metricList = new LinkedList<PCEPMetricObject>();
			metricList.add(metricObject);
		}
	}

	public void insertMetricObjectList(LinkedList<PCEPMetricObject> metricList) {
		this.metricList = metricList;
	}

	public void insertITResourceObject(PCEPITResourceObject itResource) {
		log("entering insertITResourceObject...");
		this.itResource = itResource;
		log("this.itResource = " + this.itResource);
	}

	public void insertIncludeRouteObject(PCEPIncludeRouteObject IRO) {
		this.IRO = IRO;
	}

	public void insertExplicitRouteObject(PCEPExplicitRouteObject ERO) {
		if (containsExplicitRouteObjectList()) {
			extractExplicitRouteObjectList().add(ERO);
		} else {
			EROList = new LinkedList<PCEPExplicitRouteObject>();
			EROList.add(ERO);
		}

	}

	public void insertExplicitRouteObjectList(LinkedList<PCEPExplicitRouteObject> EROList) {
		this.EROList = EROList;
	}

	// EXTRACT METHODS

	public PCEPNoPathObject extractNoPathObject() {
		if (containsNoPathObject()) {
			return noPath;
		}
		return null;
	}

	public PCEPNoVertexObject extractNoVertexObject() {
		if (containsNoVertexObject())
			return noVertex;
		return null;
	}

	public PCEPLabelSwitchedPathAttributesObject extractLabelSwitchedPathAttributesObject() {
		if (containsLabelSwitchedPathAttributesObject()) {
			return LSPA;
		}
		return null;
	}

	public LinkedList<PCEPBandwidthObject> extractBandwidthObjectList() {
		if (containsBandwidthObjectList()) { // TODO FIX This bug !!
			return bwList;
		}
		return null;
	}

	public LinkedList<PCEPMetricObject> extractMetricObjectList() {
		if (containsMetricObjectList()) {
			return metricList;
		}
		return null;
	}

	public PCEPITResourceObject extractITResourceObject() {
		if (containsITResourceObject())
			return itResource;
		return null;
	}

	public PCEPIncludeRouteObject extractIncludeRouteObject() {
		if (containsIncludeRouteObject()) {
			return IRO;
		}
		return null;
	}

	public LinkedList<PCEPExplicitRouteObject> extractExplicitRouteObjectList() {
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

	public boolean containsNoVertexObject() {
		if (noVertex == null)
			return false;
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

	public boolean containsITResourceObject() {
		log("entering containsITResourceObject...");
		log("contains itResource ? " + this.itResource);
		if (itResource == null)
			return false;
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
		if (containsNoVertexObject()){
		    length += noVertex.getObjectFrameByteLength();
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

	public LinkedList<PCEPObjectFrame> getObjectFrameLinkedList() {

		log("entering getObjectFrameLinkedList()...");
		LinkedList<PCEPObjectFrame> respondObjects = new LinkedList<PCEPObjectFrame>();

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

		if (containsITResourceObject()) {
			log("adding itResource into respondObjects...");
			respondObjects.add(itResource);
		}

		if (containsNoVertexObject()) {
			respondObjects.add(noVertex);
		}
		return respondObjects;
	}

	public int getMessageType() {
		return MESSAGE_TYPE;
	}

	public void log(String logString) {
		System.out.println("PCEPResponseFrame::: " + logString);
	}

}
