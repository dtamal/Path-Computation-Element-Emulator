package com.pcee.client.launcher;

import com.global.GlobalCfg;
import com.pcee.architecture.ModuleEnum;
import com.pcee.client.ClientTest;
import com.pcee.client.TopologyUpdateLauncher;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPITResourceObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

/**
 * Make vertex searching request according to it resource specification
 * 
 * @author Yuesheng Zhong
 * 
 */
public class ITResourceLauncher {

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

		ClientTest.lm.getClientModule().sendMessage(message, ModuleEnum.CLIENT_MODULE);

		PCEPMessage responseMessage = null;
		try {
			responseMessage = ClientTest.messageQueue.take();
			TopologyUpdateLauncher.timeStampsReceivedMilli.add(System.currentTimeMillis());
			TopologyUpdateLauncher.timeStampsReceivedNano.add(System.nanoTime());
		} catch (InterruptedException e) {
			return null;
		}

		return PCEPResponseFrameFactory.getITResourceResponseFrame(responseMessage);
	}
}
