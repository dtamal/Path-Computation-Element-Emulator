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

package com.graph.topology.importers;

import com.graph.graphcontroller.Gcontroller;

public abstract class ImportTopology {
	
	/**Function to define the Graph Implementation to be populated with the graph given in the file*/
	public abstract void importTopology (Gcontroller graph, String filename);

	public abstract void importTopologyFromString (Gcontroller graph, String[] topology);

}
