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

package com.pcee.client;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.protocol.TeDecommission.PCEPTEDecommissionMessageFrame;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.PCEPMessageFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

/**
 * GUI based implementation of the PCE client
 * 
 * @author Marek Drogon
 * 
 */
public class TEDecommissionClientLauncher {

	public TEDecommissionClientLauncher(String vertexID, String vertexID2) {
		// Address of the PCE server
		String pceServerAddress = "172.16.1.1";
		int port = 4000;

		// Default source and destination values for path computation requests

		// String defaultSourceAddress = "172.20.1.10";
		// String defaultDestinationAddress = "172.20.1.40";

		ModuleManagement lm = new ModuleManagement(false);

		PCEPAddress destAddress = new PCEPAddress(pceServerAddress, port);

		lm.getClientModule().registerConnection(destAddress, false, true,false);

		PCEPEndPointsObject endPoints = PCEPObjectFrameFactory
				.generatePCEPEndPointsObject("0", "0", new PCEPAddress(
						vertexID, false), new PCEPAddress(vertexID2, false));
		PCEPMessageFrame frame = new PCEPTEDecommissionMessageFrame(endPoints);
		PCEPMessage message = PCEPMessageFactory.generateMessage(frame);
		message.setAddress(destAddress);
		lm.getClientModule().sendMessage(message, ModuleEnum.CLIENT_MODULE);

	}

}