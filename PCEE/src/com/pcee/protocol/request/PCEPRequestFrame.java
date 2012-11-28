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

package com.pcee.protocol.request;

import java.util.LinkedList;
import com.pcee.protocol.message.PCEPMessageFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGeneralizedEndPointsTNAObject;
import com.pcee.protocol.message.objectframe.impl.PCEPITResourceObject;
import com.pcee.protocol.message.objectframe.impl.PCEPIncludeRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPLabelSwitchedPathAttributesObject;
import com.pcee.protocol.message.objectframe.impl.PCEPLoadBalancingObject;
import com.pcee.protocol.message.objectframe.impl.PCEPMetricObject;
import com.pcee.protocol.message.objectframe.impl.PCEPReportedRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.PCEPSynchronizationVectorObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

/**
 * <pre>
 *   The format of a PCReq message is as follows:
 * 
 *   &lt;PCReq Message&gt;::= &lt;Common Header&gt;
 *                      [&lt;svec-list&gt;]
 *                      &lt;request-list&gt;
 * 
 *   where:
 *      &lt;svec-list&gt;::=&lt;SVEC&gt;[&lt;svec-list&gt;]
 *      &lt;request-list&gt;::=&lt;request&gt;[&lt;request-list&gt;]
 *      &lt;request&gt;::= &lt;RP&gt;
 *                   &lt;END-POINTS&gt;
 *                   [&lt;LSPA&gt;]
 *                   [&lt;BANDWIDTH&gt;]
 *                   [&lt;metric-list&gt;]
 *                   [&lt;RRO&gt;[&lt;BANDWIDTH&gt;]]
 *                   [&lt;IRO&gt;]
 *                   [&lt;LOAD-BALANCING&gt;]
 * 
 *   &lt;metric-list&gt;::=&lt;METRIC&gt;[&lt;metric-list&gt;]
 * </pre>
 */
public class PCEPRequestFrame implements PCEPMessageFrame {

	public final int MESSAGE_TYPE = 3;

	LinkedList<PCEPSynchronizationVectorObject> SVECList;
	PCEPRequestParametersObject RP;
	PCEPEndPointsObject endPoints;
	PCEPLabelSwitchedPathAttributesObject LSPA;
	PCEPBandwidthObject bandwidth;
	LinkedList<PCEPMetricObject> metricList;
	PCEPReportedRouteObject RRO;
	PCEPIncludeRouteObject IRO;
	PCEPLoadBalancingObject loadBalancing;
	/* added for ITResourceObject */
	PCEPITResourceObject itResource;
	PCEPGeneralizedEndPointsTNAObject generalizedEndPointsTNA;

	public PCEPRequestFrame(PCEPRequestParametersObject RP,
			PCEPEndPointsObject endPoints) {
		this.RP = RP;
		this.endPoints = endPoints;
	}

	/**
	 * added for ITResourceObject
	 */
	public PCEPRequestFrame(PCEPRequestParametersObject RP,
			PCEPITResourceObject itResource) {
		this.RP = RP;
		this.itResource = itResource;
	}

	public PCEPRequestFrame(PCEPRequestParametersObject RP,
			PCEPGeneralizedEndPointsTNAObject generalizedEndPointsTNA) {
		this.RP = RP;
		this.generalizedEndPointsTNA = generalizedEndPointsTNA;
	}

	public PCEPRequestFrame(PCEPRequestParametersObject RP,
			PCEPEndPointsObject endPoints, PCEPBandwidthObject bandwidth) {
		this.RP = RP;
		this.endPoints = endPoints;
		this.bandwidth = bandwidth;
	}

	// VARIOUS CONVENIENT METHODS

	public PCEPRequestFrame(PCEPRequestParametersObject RP,
			PCEPEndPointsObject endPoints, PCEPBandwidthObject bandwidth,
			PCEPMetricObject metric) {
		this.RP = RP;
		this.endPoints = endPoints;
		this.bandwidth = bandwidth;
		this.metricList = new LinkedList<PCEPMetricObject>();
		this.insertMetricObject(metric);
	}

	public int getRequestID() {
		return RP.getRequestIDNumberDecimalValue();
	}

	public PCEPAddress getSourceAddress() {
		// return new PCEPAddress(endPoints.getSourceAddressBinaryString());
		if (endPoints != null)
			return new PCEPAddress(endPoints.getSourceAddressBinaryString());
		return new PCEPAddress(generalizedEndPointsTNA
				.getSourcePointBinaryString());
	}

	public PCEPAddress getDestinationAddress() {
		// return new
		// PCEPAddress(endPoints.getDestinationAddressBinaryString());
		if (endPoints != null)
			return new PCEPAddress(endPoints
					.getDestinationAddressBinaryString());
		return new PCEPAddress(generalizedEndPointsTNA
				.getDestinationPointBinaryString());
	}

	public PCEPGeneralizedEndPointsTNAObject getGeneralizedEndPointsTNAObject() {
		return this.generalizedEndPointsTNA;
	}

	// INSERT METHODS

	public void insertSynchronizationVectorObject(
			PCEPSynchronizationVectorObject SVECObject) {
		if (containsSynchronizationVectorObjectList()) {
			extractSynchronizationVectorObjectList().add(SVECObject);
		} else {
			SVECList = new LinkedList<PCEPSynchronizationVectorObject>();
			SVECList.add(SVECObject);
		}
	}

	public void insertEndPointsObject(PCEPEndPointsObject endPoints) {
		this.endPoints = endPoints;
	}

	public void insertSynchronizationVectorObjectList(
			LinkedList<PCEPSynchronizationVectorObject> SVECList) {
		this.SVECList = SVECList;
	}

	public void insertLabelSwitchedPathAttributesObject(
			PCEPLabelSwitchedPathAttributesObject LSPA) {
		this.LSPA = LSPA;
	}

	public void insertBandwidthObject(PCEPBandwidthObject bandwidth) {
		this.bandwidth = bandwidth;
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

	public void insertReportedRouteObject(PCEPReportedRouteObject RRO) {
		this.RRO = RRO;
	}

	public void insertIncludeRouteObject(PCEPIncludeRouteObject IRO) {
		this.IRO = IRO;
	}

	public void insertLoadBalancingObject(PCEPLoadBalancingObject loadBalancing) {
		this.loadBalancing = loadBalancing;
	}

	public void insertITResourceObject(PCEPITResourceObject itResource) {
		this.itResource = itResource;
	}

	public void insertGeneralizedEndPointsTNAObject(
			PCEPGeneralizedEndPointsTNAObject generalizedEndPointsTNA) {
		this.generalizedEndPointsTNA = generalizedEndPointsTNA;
	}

	// EXTRACT METHODS

	public LinkedList<PCEPSynchronizationVectorObject> extractSynchronizationVectorObjectList() {
		if (containsSynchronizationVectorObjectList()) {
			return SVECList;
		}
		return null;
	}

	public PCEPEndPointsObject extractEndPointsObject() {
		if (containsEndPointsObject())
			return endPoints;
		return null;
	}

	public PCEPLabelSwitchedPathAttributesObject extractLabelSwitchedPathAttributesObject() {
		if (containsLabelSwitchedPathAttributesObject()) {
			return LSPA;
		}
		return null;
	}

	public PCEPBandwidthObject extractBandwidthObject() {
		if (containsBandwidthObject()) {
			return bandwidth;
		}
		return null;
	}

	public LinkedList<PCEPMetricObject> extractMetricObjectList() {
		if (containsMetricObjectList()) {
			return metricList;
		}
		return null;
	}

	public PCEPReportedRouteObject extractReportedRouteObject() {
		if (containsReportedRouteObject()) {
			return RRO;
		}
		return null;
	}

	public PCEPIncludeRouteObject extractIncludeRouteObject() {
		if (containsIncludeRouteObject()) {
			return IRO;
		}
		return null;
	}

	public PCEPLoadBalancingObject extractLoadBalancingObject() {
		if (containsLoadBalancingObject()) {
			return loadBalancing;
		}
		return null;
	}

	public PCEPITResourceObject extractITResourceObject() {
		if (containsITResourceObject())
			return itResource;
		return null;
	}

	public PCEPGeneralizedEndPointsTNAObject extractGeneralizedEndPointsTNAObject() {
		if (containsGeneralizedEndPointsTNAObject())
			return generalizedEndPointsTNA;
		return null;
	}

	// CONTAINS METHODS

	public boolean containsITResourceObject() {
		if (itResource == null)
			return false;
		return true;
	}

	public boolean containsEndPointsObject() {
		if (endPoints == null)
			return false;
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

	public boolean containsGeneralizedEndPointsTNAObject() {
		if (generalizedEndPointsTNA == null)
			return false;
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
		if (containsEndPointsObject())
			length += endPoints.getObjectFrameByteLength();

		if (containsGeneralizedEndPointsTNAObject())
			length += generalizedEndPointsTNA.getObjectFrameByteLength();

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
		if (containsITResourceObject()){
			length += itResource.getObjectFrameByteLength();
		}

		return length;
	}

	public String getBinaryString() {

		StringBuffer objectsString = new StringBuffer();

		if (containsSynchronizationVectorObjectList()) {
			for (int i = 0; i < SVECList.size(); i++) {
				objectsString.append(SVECList.get(i)
						.getObjectFrameBinaryString());
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
				objectsString.append(metricList.get(i)
						.getObjectFrameBinaryString());
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
		if (containsITResourceObject()){
			objectsString.append(itResource.getObjectFrameBinaryString());
		}

		return objectsString.toString();
	}

	public LinkedList<PCEPObjectFrame> getObjectFrameLinkedList() {

		LinkedList<PCEPObjectFrame> requestObjects = new LinkedList<PCEPObjectFrame>();

		if (containsSynchronizationVectorObjectList()) {
			for (int i = 0; i < SVECList.size(); i++) {
				requestObjects.add(SVECList.get(i));
			}
		}

		requestObjects.add(RP);

		if (containsEndPointsObject())
			requestObjects.add(endPoints);

		if (containsGeneralizedEndPointsTNAObject())
			requestObjects.add(generalizedEndPointsTNA);

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
		if (containsITResourceObject()) {
			requestObjects.add(itResource);
		}

		return requestObjects;
	}

	public int getMessageType() {
		return MESSAGE_TYPE;
	}

	public void log(String logString) {
		System.out.println("PCEPRequestFrame::: " + logString);
	}
}
