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

package com.pcee.protocol.keepalive;

import java.util.LinkedList;

import com.pcee.protocol.message.PCEPMessageFrame;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;

public class PCEPKeepaliveFrame implements PCEPMessageFrame {

	public final int MESSAGE_TYPE = 2;

	public PCEPKeepaliveFrame() {
	}

	public int getByteLength() {
		return 0;
	}

	public String getBinaryString() {
		return "";
	}

	public LinkedList<PCEPObjectFrame> getObjectFrameLinkedList() {
		return new LinkedList<PCEPObjectFrame>();
	}

	public int getMessageType() {
		return MESSAGE_TYPE;
	}
}
