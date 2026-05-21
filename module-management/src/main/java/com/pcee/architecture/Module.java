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
package com.pcee.architecture;

import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

// TODO make module implementations AutoCloseable to clean up at shutdown.
public interface Module {

  void receiveMessage(PCEPMessage message, ModuleEnum sourceLayer);

  void sendMessage(PCEPMessage message, ModuleEnum targetLayer);

  void stop(boolean graceful);

  void start();

  void closeConnection(PCEPAddress address);

  void registerConnection(
      PCEPAddress address, boolean connected, boolean connectionInitialized, boolean forceClient);
}
