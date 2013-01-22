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

package com.pcee.architecture.computationmodule;

import java.util.concurrent.LinkedBlockingQueue;

import com.pcee.architecture.Module;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

/**
 * Abstract Super class to unify multiple Computaton Layer implementations
 * 
 * @author Marek Drogon
 */
public abstract class ComputationModule implements Module{

	//Functions to suppoort worker tasks to receive incoming messages from other PCE peers
	public abstract boolean  isValidRequestToRemotePeer(PCEPAddress address, String requestID);	
	public abstract void registerRequestToRemotePeer(PCEPAddress address, String requestID, LinkedBlockingQueue<PCEPMessage> queue);
	protected abstract void processResponseFromRemotePeer(PCEPMessage message);

}
