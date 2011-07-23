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

package com.pcee.logger;

import com.pcee.common.Time;
import com.pcee.logger.logObjectImpl.ConsoleLogObject;

public class Logger {

	public static LogObject logObject = new ConsoleLogObject(); //Default Logging Functionality
	
	public static void setLogObject (LogObject object){
		Logger.logObject = object;
	}
	
	public static void logWarning(String msg) {
		// System.out.println(msg);
	}

	public static void logConnectionEstablishment(String msg) {
		// System.out.println(msg);
	}

	public static void logError(String msg) {
		// System.out.println(msg);
	}

	public static void logGUINotifications(String msg) {
		String timeStamp = Time.timeStamp();
//		System.out.println(timeStamp + " " + msg);
		logObject.logMsg(timeStamp + " " + msg);
	}

	public static synchronized void logSystemEvents(String msg) {
		String timeStamp = Time.timeStamp();
//		System.out.println(timeStamp + " " + msg);
		logObject.logMsg(timeStamp + " " + msg);
	}

	public static void debugger(String msg) {
		String timeStamp = Time.timeStamp();
//		System.out.println(timeStamp + " " + msg);
		logObject.logMsg(timeStamp + " " + msg);
	}

}