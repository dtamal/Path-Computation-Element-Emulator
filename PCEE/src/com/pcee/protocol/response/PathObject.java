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
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObjectWSON;

public class PathObject {

	PCEPExplicitRouteObjectWSON ERO;
	AttributeListObject attributeList;

	public PathObject(PCEPExplicitRouteObjectWSON ERO, AttributeListObject attributeList) {
		this.ERO = ERO;
		this.attributeList = attributeList;
	}

	public LinkedList<PCEPObjectFrame> getObjectFrameList() {

		LinkedList<PCEPObjectFrame> pathObjectsLinkedList = new LinkedList<PCEPObjectFrame>();
		LinkedList<PCEPObjectFrame> attributesLinkedList = attributeList.getObjectFrameList();

		pathObjectsLinkedList.add(ERO);

		for (int i = 0; i < attributesLinkedList.size(); i++) {
			pathObjectsLinkedList.add(attributesLinkedList.get(i));
		}

		return pathObjectsLinkedList;
	}

	public int getByteLength() {
		int length = 0;

		length += ERO.getObjectFrameByteLength();
		length += attributeList.getByteLength();

		return length;
	}

	public String getBinaryString() {

		StringBuffer objectsString = new StringBuffer();

		objectsString.append(ERO.getObjectFrameBinaryString());
		objectsString.append(attributeList.getBinaryString());

		return objectsString.toString();
	}

}
