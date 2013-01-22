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

package com.pcee.protocol.notification;

import java.util.LinkedList;

import com.pcee.protocol.message.PCEPMessageFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PCEPNotificationObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;

public class PCEPNotificationFrame implements PCEPMessageFrame {
	
	public final int MESSAGE_TYPE = 5;

	LinkedList<PCEPNotificationObject> notificationList;
	LinkedList<PCEPRequestParametersObject> rpList;

	public PCEPNotificationFrame(LinkedList<PCEPNotificationObject> notificationList) {
		this.notificationList = notificationList;
	}

	public void insertRequestParametersObjectList(LinkedList<PCEPRequestParametersObject> rpList) {
		this.rpList = rpList;
	}

	public boolean containsRequestParametersObjectList() {
		if (rpList == null) {
			return false;
		}
		return true;
	}

	public LinkedList<PCEPObjectFrame> getObjectFrameLinkedList() {

		LinkedList<PCEPObjectFrame> requestObjects = new LinkedList<PCEPObjectFrame>();

		if (rpList != null) {
			for (int i = 0; i < rpList.size(); i++) {
				requestObjects.add(rpList.get(i));
			}
		}

		for (int i = 0; i < notificationList.size(); i++) {
			requestObjects.add(notificationList.get(i));
		}

		return requestObjects;
	}

	public int getByteLength() {
		int length = 0;

		if (rpList != null) {
			for (int i = 0; i < rpList.size(); i++) {
				length += rpList.get(0).getObjectFrameByteLength();
			}
		}

		for (int i = 0; i < notificationList.size(); i++) {
			length += notificationList.get(0).getObjectFrameByteLength();
		}

		return length;
	}

	public String getBinaryString() {

		StringBuffer objectsString = new StringBuffer();

		if (rpList != null) {
			for (int i = 0; i < rpList.size(); i++) {
				objectsString.append(rpList.get(0).getObjectFrameBinaryString());
			}
		}

		for (int i = 0; i < notificationList.size(); i++) {
			objectsString.append(notificationList.get(0).getObjectFrameBinaryString());
		}

		return objectsString.toString();
	}
	
	public int getMessageType() {
		return MESSAGE_TYPE;
	}

}
