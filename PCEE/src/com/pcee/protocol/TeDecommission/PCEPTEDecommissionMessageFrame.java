package com.pcee.protocol.TeDecommission;

import java.util.LinkedList;

import com.pcee.protocol.message.PCEPMessageFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;

public class PCEPTEDecommissionMessageFrame implements PCEPMessageFrame{

	public final int MESSAGE_TYPE = 52;

	private PCEPEndPointsObject endPoints;

	public PCEPTEDecommissionMessageFrame(PCEPEndPointsObject endPoints) {
		this.endPoints = endPoints;
	}

	public PCEPEndPointsObject getEndPointsObject() {
		return endPoints;
	}

	public int getByteLength() {
		int length = 0;

		length += endPoints.getObjectFrameByteLength();

		return length;
	}

	public String getBinaryString() {

		StringBuffer objectsString = new StringBuffer();

		objectsString.append(endPoints.getObjectFrameBinaryString());

		return objectsString.toString();
	}

	public LinkedList<PCEPObjectFrame> getObjectFrameLinkedList() {

		LinkedList<PCEPObjectFrame> requestObjects = new LinkedList<PCEPObjectFrame>();
		requestObjects.add(endPoints);

		return requestObjects;
	}

	public int getMessageType() {
		return MESSAGE_TYPE;
	}

}
