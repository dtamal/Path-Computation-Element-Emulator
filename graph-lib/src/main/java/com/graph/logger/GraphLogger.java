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

package com.graph.logger;

/** Logging Class for the JavaGraphLib
 * 
 * @author Mohit Chamania
 */
public class GraphLogger {

	/**Flag to define mode of logging for error messages*/
	private static boolean logErrorMode=false;
	private static boolean logMsgMode=false;
	private static boolean logStatusMode=false;



	/**
	 * @param logErrorMode the logErrorMode to set
	 */
	public static void setLogErrorMode(boolean logErrorMode) {
		GraphLogger.logErrorMode = logErrorMode;
	}

	/**
	 * @param logMsgMode the logMsgMode to set
	 */
	public static void setLogMsgMode(boolean logMsgMode) {
		GraphLogger.logMsgMode = logMsgMode;
	}

	/**
	 * @param logStatusMode the logStatusMode to set
	 */
	public static void setLogStatusMode(boolean logStatusMode) {
		GraphLogger.logStatusMode = logStatusMode;
	}

	/**Function to log all recorded errors*/
	public static void logError(String errorMsg, String classIdentifier){
		if (logErrorMode)
			System.out.println("[JavGraphLib-" + classIdentifier + "][Error] "+ errorMsg);
	}

	/**Function to log all recorded messages*/
	public static void logMsg(String msg, String classIdentifier){
		if (logMsgMode)
			System.out.println("[JavGraphLib-" + classIdentifier + "][Msg] "+ msg);
	}


	/**Function to log all recorded messages*/
	public static void logStatus(String msg, String classIdentifier){
		if (logStatusMode)
			System.out.println("[JavGraphLib-" + classIdentifier + "][Status] "+ msg);
	}


}
