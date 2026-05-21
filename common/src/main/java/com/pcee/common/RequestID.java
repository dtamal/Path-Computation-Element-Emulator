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

package com.pcee.common;

/**
 * Implemented as Singleton. Range: 1-4294967295 (32 Bit)
 * 
 * @author mdr
 */
public class RequestID {

	static private RequestID _instance;
	private long idCounter;

	private RequestID() {
		initCounter();
	}

	static public RequestID getInstance() {
		if (_instance == null)
			_instance = new RequestID();
		return _instance;
	}

	private void initCounter() {
		idCounter = 0;
	}

	synchronized public long getID() {
		idCounter = (idCounter + 1) % 4294967296L;
		// Zero Value not allowed in the standard!
		if (idCounter == 0) {
			idCounter++;
		}
		return idCounter;
	}

}
