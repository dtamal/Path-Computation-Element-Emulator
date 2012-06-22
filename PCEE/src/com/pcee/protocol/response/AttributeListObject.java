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

import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPIncludeRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPLabelSwitchedPathAttributesObject;
import com.pcee.protocol.message.objectframe.impl.PCEPMetricObject;

public class AttributeListObject {

	PCEPLabelSwitchedPathAttributesObject LSPA;
	PCEPBandwidthObject bandwidth;
	LinkedList<PCEPMetricObject> metricList;
	PCEPIncludeRouteObject IRO;

	public void insertLabelSwitchedPathAttributesObject(PCEPLabelSwitchedPathAttributesObject LSPA) {
		this.LSPA = LSPA;
	}

	public void insertBandwidthObject(PCEPBandwidthObject bandwidth) {
		this.bandwidth = bandwidth;
	}

	public void insertMetricObjectList(LinkedList<PCEPMetricObject> metricList) {
		this.metricList = metricList;
	}

	public void insertIncludeRouteObject(PCEPIncludeRouteObject IRO) {
		this.IRO = IRO;
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

	public boolean containsIncludeRouteObject() {
		if (IRO == null) {
			return false;
		}
		return true;
	}

	public LinkedList<PCEPObjectFrame> getObjectFrameList() {

		LinkedList<PCEPObjectFrame> objectsLinkedList = new LinkedList<PCEPObjectFrame>();

		if (LSPA != null) {
			objectsLinkedList.add(LSPA);
		}
		if (bandwidth != null) {
			objectsLinkedList.add(bandwidth);
		}
		if (metricList != null) {
			for (int i = 0; i < metricList.size(); i++) {
				objectsLinkedList.add(metricList.get(i));
			}
		}
		if (IRO != null) {
			objectsLinkedList.add(IRO);
		}

		return objectsLinkedList;
	}

	public int getByteLength() {
		int length = 0;

		if (LSPA != null) {
			length += LSPA.getObjectFrameByteLength();
		}
		if (bandwidth != null) {
			length += bandwidth.getObjectFrameByteLength();
		}
		if (metricList != null) {
			for (int i = 0; i < metricList.size(); i++) {
				length += metricList.get(0).getObjectFrameByteLength();
			}
		}
		if (IRO != null) {
			length += IRO.getObjectFrameByteLength();
		}

		return length;
	}

	public String getBinaryString() {

		StringBuffer objectsString = new StringBuffer();

		if (LSPA != null) {
			objectsString.append(LSPA.getObjectFrameBinaryString());
		}
		if (bandwidth != null) {
			objectsString.append(bandwidth.getObjectFrameBinaryString());
		}
		if (metricList != null) {
			for (int i = 0; i < metricList.size(); i++) {
				objectsString.append(metricList.get(0).getObjectFrameBinaryString());
			}
		}
		if (IRO != null) {
			objectsString.append(IRO.getObjectFrameBinaryString());
		}

		return objectsString.toString();
	}

}
