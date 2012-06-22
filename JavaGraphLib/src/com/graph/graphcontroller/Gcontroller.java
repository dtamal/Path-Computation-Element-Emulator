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

package com.graph.graphcontroller;

import java.util.ArrayList;
import java.util.Set;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;

public interface Gcontroller {

    /** Function to add a vertex into the graph */
    public void addVertex(VertexElement vertex);

    /** Function to add an edge into the graph */
    public void addEdge(EdgeElement edge);

    /** Function to return the set of Vertex IDs in a Graph */
    public Set<String> getVertexIDSet();

    /** Function to return the set of Vertices in a Graph */
    public Set<VertexElement> getVertexSet();

    /** Function to return the set of Edge IDs in a Graph */
    public Set<String> getEdgeIDSet();

    /** Function to return the set of Edges in a Graph */
    public Set<EdgeElement> getEdgeSet();

    /** Function to get an arbitrary Edge between 2 vertices */
    public EdgeElement getConnectingEdge(String vertexID1, String vertexID2);

    public EdgeElement getConnectingEdge(VertexElement vertexID1, VertexElement vertexID2);

    /** Function to get all Edges between 2 vertices */
    public ArrayList<EdgeElement> allConnectingEdges(String vertexID1, String vertexID2);

    public ArrayList<EdgeElement> allConnectingEdges(VertexElement vertexID1, VertexElement vertexID2);

    /** Boolean function to check if an edge exists between 2 vertices */
    public boolean aConnectingEdge(String vertexID1, String vertexID2);

    public boolean aConnectingEdge(VertexElement vertexID1, VertexElement vertexID2);

    /** Function to check if a vertex exists in the map */
    public boolean vertexExists(String vertexID);

    public boolean vertexExists(VertexElement vertex);

    /** Function to get the vertexElement based on VertexID */
    public VertexElement getVertex(String vertexID);

    /** Function to get the EdgeElement based on edgeID */
    public EdgeElement getEdge(String edgeID);

    /** Function to create a copy of the Graph Controller */
    public Gcontroller createCopy();

}
