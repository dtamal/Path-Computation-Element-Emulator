package com.pcee.protocol.message.objectframe.impl;

import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;

public abstract class PCEPExplicitRouteObject implements PCEPObjectFrame {

	public abstract String printPath();

	public int type() {
		String objectString = getObjectBinaryString();

		String length = objectString.substring(8, 16);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(length);

		/**
		 * 8 means TUBS ERO, 12 Means TID ERO
		 */
		return decimalValue;
	}
}
