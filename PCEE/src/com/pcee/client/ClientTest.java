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

package com.pcee.client;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.global.GlobalCfg;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PCEPITResourceObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

/**
 * GUI based implementation of the PCE client
 * 
 * @author Marek Drogon
 * @author Yuesheng Zhong
 */
public class ClientTest {

	public static LinkedBlockingQueue<PCEPMessage> messageQueue = new LinkedBlockingQueue<PCEPMessage>();
	public static ModuleManagement lm;

	public static ArrayList<Long> requestEnterTheQueue = new ArrayList<Long>();
	public static ArrayList<Long> requestLeaveTheQueue = new ArrayList<Long>();

	public static ArrayList<Long> enterTheComputation = new ArrayList<Long>();
	public static ArrayList<Long> leaveTheComputation = new ArrayList<Long>();

	public static int singlePath = 0;
	public static int total = 0;

	/** Launch point to initialize the client GUI */
	public static void main(String[] args) throws Exception {

	}

	public static void initClient() {
		// Initialize the layer management module
		lm = new ModuleManagement(false);
		PCEPAddress address = new PCEPAddress(GlobalCfg.pcrAddress, GlobalCfg.pcrPort);
		lm.getClientModule().registerConnection(address, false, true,false);
	}

	public static PCEPResponseFrame getPath(String sourceID, String destID, float bw) {

		// Address of the PCE server
		PCEPAddress address = new PCEPAddress(GlobalCfg.pcrAddress, GlobalCfg.pcrPort);
		PCEPAddress sourceAddress = new PCEPAddress(sourceID, false);
		PCEPAddress destinationAddress = new PCEPAddress(destID, false);

		PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "1", "0", "1", "432");
		PCEPEndPointsObject endPoints = PCEPObjectFrameFactory.generatePCEPEndPointsObject("1", "0", sourceAddress, destinationAddress);
		PCEPBandwidthObject bandwidth = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", bw);
//		PCEPITResourceObject itResource = PCEPObjectFrameFactory.generatePCEPITResourceObject("1", "0", 0, 3, 30, 300);

		PCEPRequestFrame requestFrame = PCEPRequestFrameFactory.generatePathComputationRequestFrame(RP, endPoints);
		requestFrame.insertBandwidthObject(bandwidth);
//		requestFrame.insertITResourceObject(itResource);
		PCEPMessage message = PCEPMessageFactory.generateMessage(requestFrame);
		message.setAddress(address);
		String mString = message.binaryInformation();
		log(mString);
		TopologyUpdateLauncher.requestCount++;
		TopologyUpdateLauncher.timeStampsSentMilli.add(System.currentTimeMillis());
		TopologyUpdateLauncher.timeStampsSentNano.add(System.nanoTime());

		lm.getClientModule().sendMessage(message, ModuleEnum.SESSION_MODULE);

		PCEPMessage response;
		try {
			response = messageQueue.take();
			TopologyUpdateLauncher.timeStampsReceivedMilli.add(System.currentTimeMillis());
			TopologyUpdateLauncher.timeStampsReceivedNano.add(System.nanoTime());
		} catch (InterruptedException e) {
			return null;
		}
		
		System.out.println("Repnose RETURNED!!!!!!!!!!!!!!!!!!");

		return PCEPResponseFrameFactory.getPathComputationResponseFrame(response);
	}
	
	public static PCEPResponseFrame getPath(String sourceID, float bw, int cpu, int ram, int storage) {

		// Address of the PCE server
		PCEPAddress address = new PCEPAddress(GlobalCfg.pcrAddress, GlobalCfg.pcrPort);
		PCEPAddress sourceAddress = new PCEPAddress(sourceID, false);
		PCEPAddress destinationAddress = new PCEPAddress(sourceID, false);

		PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "1", "0", "1", "432");
		PCEPEndPointsObject endPoints = PCEPObjectFrameFactory.generatePCEPEndPointsObject("1", "0", sourceAddress, destinationAddress);
		PCEPBandwidthObject bandwidth = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", bw);
		PCEPITResourceObject itResource = PCEPObjectFrameFactory.generatePCEPITResourceObject("1", "0", 0, cpu, ram, storage);

		PCEPRequestFrame requestFrame = PCEPRequestFrameFactory.generatePathComputationRequestFrame(RP, endPoints);
		requestFrame.insertBandwidthObject(bandwidth);
		requestFrame.insertITResourceObject(itResource);
		PCEPMessage message = PCEPMessageFactory.generateMessage(requestFrame);
		message.setAddress(address);
		String mString = message.binaryInformation();
		log(mString);
		TopologyUpdateLauncher.requestCount++;
		TopologyUpdateLauncher.timeStampsSentMilli.add(System.currentTimeMillis());
		TopologyUpdateLauncher.timeStampsSentNano.add(System.nanoTime());

		lm.getClientModule().sendMessage(message, ModuleEnum.SESSION_MODULE);

		PCEPMessage response;
		try {
			response = messageQueue.take();
			TopologyUpdateLauncher.timeStampsReceivedMilli.add(System.currentTimeMillis());
			TopologyUpdateLauncher.timeStampsReceivedNano.add(System.nanoTime());
		} catch (InterruptedException e) {
			return null;
		}

		return PCEPResponseFrameFactory.getPathComputationResponseFrame(response);
	}

	/**
	 * get vertex from server with required IT Resource
	 */
	public static PCEPResponseFrame getVertex(int cpu, int ram, int storage) {

		PCEPAddress serverAddress = new PCEPAddress(GlobalCfg.pcrAddress, GlobalCfg.pcrPort);
		PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "1", "0", "1", "432");
		PCEPITResourceObject itResource = PCEPObjectFrameFactory.generatePCEPITResourceObject("1", "0", 0, cpu, ram, storage);

		PCEPRequestFrame request = PCEPRequestFrameFactory.generateITResourceRequestFrame(RP, itResource);
		PCEPMessage message = PCEPMessageFactory.generateMessage(request);
		message.setAddress(serverAddress);

		TopologyUpdateLauncher.timeStampsVertexSentMilli.add(System.currentTimeMillis());
		TopologyUpdateLauncher.timeStampsVertexSentNano.add(System.nanoTime());
		
		lm.getClientModule().sendMessage(message, ModuleEnum.CLIENT_MODULE);

		PCEPMessage responseMessage = null;
		try {
			responseMessage = ClientTest.messageQueue.take();
			TopologyUpdateLauncher.timeStampsVertexReceivedMilli.add(System.currentTimeMillis());
			TopologyUpdateLauncher.timeStampsVertexReceivedNano.add(System.nanoTime());
		} catch (InterruptedException e) {
			return null;
		}
		PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.getITResourceResponseFrame(responseMessage);
		System.out.println("responseFrame.extractNoVertexParams() = " + responseFrame.extractNoVertexObject());
		System.out.println("responseFrame.extractExplicitRouteObjectList() = " + responseFrame.extractExplicitRouteObjectList());
		System.out.println("response Message in ClientTest.getVertex()" + responseMessage);
		return PCEPResponseFrameFactory.getITResourceResponseFrame(responseMessage);
	}

	public static void log(String logString) {
		System.out.println("ClientTest::: " + logString);
	}
}