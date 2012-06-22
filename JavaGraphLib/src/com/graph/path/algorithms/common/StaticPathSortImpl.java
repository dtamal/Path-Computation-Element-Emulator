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

package com.graph.path.algorithms.common;

import java.util.ArrayList;

import com.graph.path.PathElement;

public class StaticPathSortImpl {


	/**Sort paths by ascending order of weight*/
	public static ArrayList<PathElement> sortPathsByWeight(ArrayList<PathElement> paths){
		int flag=0;
		int count=0;
		if (paths.size()==0)
			return paths;
		while(flag==0){
			flag=1;
			for (int i=paths.size()-1;i>count;i--)
				if (paths.get(i).getPathParams().getPathWeight()<paths.get(i-1).getPathParams().getPathWeight()){
					//swap elements i and i-1
					PathElement temp= paths.remove(i-1);
					paths.add(i, temp);
					flag=0;
				}
			count++;
		}
		return paths;
	}


	/**Sort paths by ascending order of available capacity. In case of tie give preference to shortest weight path*/
	public static ArrayList<PathElement> sortPathsByBandwidth(ArrayList<PathElement> paths){
		int flag=0;
		int count=0;
		if (paths.size()==0)
			return paths;
		while(flag==0){
			flag=1;
			for (int i=paths.size()-1;i>count;i--)
				if (paths.get(i).getPathParams().getAvailableCapacity()>paths.get(i-1).getPathParams().getAvailableCapacity()){
					//swap elements i and i-1
					PathElement temp= paths.remove(i-1);
					paths.add(i, temp);
					flag=0;
				}
				else if ((paths.get(i).getPathParams().getAvailableCapacity()==paths.get(i-1).getPathParams().getAvailableCapacity())&&(paths.get(i).getPathParams().getPathWeight()<paths.get(i-1).getPathParams().getPathWeight()))
				{
					//swap elements i and i-1
					PathElement temp= paths.remove(i-1);
					paths.add(i, temp);
					flag=0;					
				}
			count++;
		}
		return paths;
	}


	/**Sort paths by ascending order of delay*/
	public static ArrayList<PathElement> sortPathsByDelay(ArrayList<PathElement> paths){
		int flag=0;
		int count=0;
		if (paths.size()==0)
			return paths;

		int i;
		while(flag==0){
			flag=1;
			for (i=paths.size()-1;i>count;i--){
				if (paths.get(i).getPathParams().getPathDelay()<paths.get(i-1).getPathParams().getPathDelay()){
					//swap elements i and i-1
					PathElement temp= paths.remove(i-1);
					paths.add(i, temp);
					flag=0;
				}
			}
			count++;
		}

		return paths;
	}

}
