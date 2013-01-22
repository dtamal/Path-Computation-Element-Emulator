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

package com.pcee.server;

import com.globalGraph.TopoGlobal;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ted.TopologyInformation;
import com.pcee.client.GuiLauncher;
import com.pcee.client.launcher.Launcher;

/**
 * Example class to demonstrate the launch of the PCE Server
 * 
 * @author Mohit Chamania
 * 
 */
public class ServerLauncher {

	/** Launch Point of the PCEE Server */
	public static void main(String[] args) {
//	    Launcher.init();
//	    TopoGlobal.init();
		if (args.length == 0) {
			new ModuleManagement(true);
		} else {
			new ModuleManagement(true, args[0]);
		}
	}
}
