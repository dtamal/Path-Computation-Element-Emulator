package com.pcee.slf4j.logger;

import com.pcee.gui.ConnectorGUI;

public class GUILogModule implements LogModule {

	@Override
	public void log(LogLevelEnum level, String arg) {
		try {
			ConnectorGUI.updateTextArea(arg);
		} catch (Exception e) {
			// TODO: handle exception
		}	
	}

}
