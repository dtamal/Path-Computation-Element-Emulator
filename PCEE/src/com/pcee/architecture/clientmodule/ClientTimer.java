package com.pcee.architecture.clientmodule;

import java.util.TimerTask;

import com.pcee.client.ClientTest;

public class ClientTimer extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ClientTest.main(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
