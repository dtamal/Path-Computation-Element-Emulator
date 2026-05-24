/**
 * This file is part of Path Computation Element Emulator (PCEE).
 *
 * <p>PCEE is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>PCEE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with PCEE. If not, see
 * http://www.gnu.org/licenses/.
 */
package com.pcee.protocol.notification;

import com.pcee.protocol.message.objectframe.impl.PceNotificationObject;
import java.util.LinkedList;

public class PceNotificationFrameFactory {

  public static PceNotificationFrame generateNotificationFrame(PceNotificationObject notifyObject) {

    LinkedList<PceNotificationObject> list = new LinkedList<PceNotificationObject>();

    list.add(notifyObject);

    return new PceNotificationFrame(list);
  }
}
