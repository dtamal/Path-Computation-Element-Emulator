package com.pcee.launcher;

import com.pcee.architecture.ModuleManagement;
import com.pcee.gui.ConnectorGUI;
import com.pcee.logger.PCEELoggerFactory;

public class Launcher {

  public static void main (String[] args) throws IllegalAccessException {
    if (args.length==0) {
      throw new IllegalAccessException("Expect at least one argument");
    }
    String launchMode = args[0].toLowerCase();
    String topology = null;
    if (args.length==2) {
      topology = args[1];
    }
    switch (launchMode) {
      case "client": {
        PCEELoggerFactory.setOperation(ConnectorGUI::updateTextArea);
        String pceServerAddress = "127.0.0.1";
        //Default source and destination values for path computation requests

        String defaultSourceAddress = "192.169.2.1";
        String defaultDestinationAddress = "192.169.2.14";

        //Initialize the layer management module

        //Start the GUI
        new ConnectorGUI(new ModuleManagement(false), pceServerAddress, defaultSourceAddress, defaultDestinationAddress);
        break;
      }
      case "server":
        if(topology == null){
          new ModuleManagement(true);
        }else{
          new ModuleManagement(true, args[0]);
        }
        break;
      default:
        throw new IllegalAccessException("Expect first argument to be client / server");
    }

  }
}
