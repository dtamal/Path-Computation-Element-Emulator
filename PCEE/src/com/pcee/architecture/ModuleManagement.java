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

package com.pcee.architecture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.pcee.architecture.clientmodule.ClientModule;
import com.pcee.architecture.clientmodule.ClientModuleImpl;
import com.pcee.architecture.computationmodule.ComputationModule;
import com.pcee.architecture.computationmodule.ComputationModuleImpl;
import com.pcee.architecture.computationmodule.ted.TopologyInformation;
import com.pcee.architecture.networkmodule.NetworkModule;
import com.pcee.architecture.networkmodule.NetworkModuleImpl;
import com.pcee.architecture.sessionmodule.SessionModule;
import com.pcee.architecture.sessionmodule.SessionModuleImpl;

public class ModuleManagement {

	private NetworkModule networkModule;
	private SessionModule sessionModule;
	private ComputationModule computationModule;
	private ClientModule clientModule;

	boolean running = false;
	boolean isServer = false;

	public ModuleManagement(boolean isServer) {
		if (running == false) {

			this.isServer = isServer;
			networkModule = new NetworkModuleImpl(isServer, this); // FIXME
			if (isServer == false)
				sessionModule = new SessionModuleImpl(this);
			else
				sessionModule = new SessionModuleImpl(this);
			if (isServer == true) {
				computationModule = new ComputationModuleImpl(this);
			} else {
				clientModule = new ClientModuleImpl(this);
			}
			running = true;
		}
	}

	public ModuleManagement(boolean isServer, String configFile) {

		try {
			Properties reader = new Properties();
			reader.load(new FileInputStream(configFile));

			int port = 0, sessionThreads = 0, computationThreads = 0;

			try {
				port = Integer.valueOf(reader.getProperty("port"));
				sessionThreads = Integer.valueOf(reader.getProperty("sessionThreads"));
				computationThreads = Integer.valueOf(reader.getProperty("computationThreads"));
				TopologyInformation.setTopoPath(reader.getProperty("topology"));
				TopologyInformation.setImporter(reader.getProperty("importer"));
			} catch (Exception e) {
				System.out.println("Wrong Configuration Inputs!");
				System.exit(0);
			}
			if (running == false) {

				this.isServer = isServer;
				networkModule = new NetworkModuleImpl(isServer, this, port);
				if (isServer == false)
					sessionModule = new SessionModuleImpl(this, sessionThreads);
				else
					sessionModule = new SessionModuleImpl(this, sessionThreads);
				if (isServer == true) {
					computationModule = new ComputationModuleImpl(this, computationThreads);
				} else {
					clientModule = new ClientModuleImpl(this);
				}
				running = true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		running = false;
		sessionModule.stop();
		networkModule.stop();
		if (isServer == true) {
			computationModule.stop();
		} else {
			clientModule.stop();
		}

	}

	public NetworkModule getNetworkModule() {
		return networkModule;
	}

	public SessionModule getSessionModule() {
		return sessionModule;
	}

	public ComputationModule getComputationModule() {
		return computationModule;
	}

	public ClientModule getClientModule() {
		return clientModule;
	}

	public boolean isServer() {
		return isServer;
	}

}
