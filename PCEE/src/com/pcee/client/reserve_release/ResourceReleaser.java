package com.pcee.client.reserve_release;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TimerTask;

import com.pcee.client.TopologyUpdateLauncher;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

/**
 * @author Yuesheng Zhong
 *
 */
public class ResourceReleaser extends TimerTask {

	private LinkedList<PCEPExplicitRouteObject> objectList;
	private LinkedList<PCEPBandwidthObject> bwList;

	/**
	 * @param objectList
	 * @param bwList
	 */
	public ResourceReleaser(LinkedList<PCEPExplicitRouteObject> objectList, LinkedList<PCEPBandwidthObject> bwList) {
		this.objectList = objectList;
		this.bwList = bwList;
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Socket socket = new Socket(TopologyUpdateLauncher.address, TopologyUpdateLauncher.port);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			for (int i = 0; i < objectList.size(); i++) {
				ArrayList<EROSubobjects> subobjects = ((PCEPGenericExplicitRouteObjectImpl) objectList.get(i)).getTraversedVertexList();
				double bw = bwList.get(i).getBandwidthFloatValue();
				for (int j = 0; j < subobjects.size() - 1; j++) {
					String ingress = ((PCEPAddress) (subobjects.get(j))).getIPv4Address(false);
					String egress = ((PCEPAddress) (subobjects.get(j + 1))).getIPv4Address(false);
					bufferedWriter.write(prepareReleaseString(ingress, egress, Double.toString(bw)));
					bufferedWriter.flush();
				}
			}
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param source
	 * @param destination
	 * @param bw
	 * @return
	 */
	private String prepareReleaseString(String source, String destination, String bw) {
		return "RELEASE:" + source + ":" + destination + ":" + bw;
	}

}
