package com.pcee.protocol.message.objectframe.impl;

import com.pcee.protocol.message.PceComputationFactory;
import com.pcee.protocol.message.objectframe.PceObjectFrame;

public abstract class PceExplicitRouteObject implements PceObjectFrame {

  public abstract String printPath();

  public int type() {
    String objectString = getObjectBinaryString();

    String length = objectString.substring(8, 16);
    int decimalValue = (int) PceComputationFactory.getDecimalValue(length);

    /** 8 means TUBS ERO, 12 Means TID ERO */
    return decimalValue;
  }
}
