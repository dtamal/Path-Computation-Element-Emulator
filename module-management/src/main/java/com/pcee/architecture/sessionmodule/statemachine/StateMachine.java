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
package com.pcee.architecture.sessionmodule.statemachine;

import com.pcee.architecture.ModuleEnum;
import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PceAddress;

public abstract class StateMachine {

  public abstract void updateState(boolean connectionEstablished);

  // public abstract void updateState(PceMessage message);
  public abstract void updateState(PceMessage message, ModuleEnum sourceLayer);

  public abstract PceAddress getAddress();

  public abstract void releaseResources();
}
