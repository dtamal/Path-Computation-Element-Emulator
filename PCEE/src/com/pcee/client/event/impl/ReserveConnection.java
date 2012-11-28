package com.pcee.client.event.impl;

import com.pcee.client.connectionsource.Source;
import com.pcee.client.event.Event;
import com.pcee.client.event.eventhandler.EventHandler;
import com.pcee.client.resv.ResvElement;

public class ReserveConnection extends Event {

	private ResvElement element;

	private Source source;

	public ReserveConnection(ResvElement element, Source source) {
		this.element = element;
		this.setTime(element.getStartTime());
		priority = 1;
		this.source = source;
	}

	@Override
	public void execute() {

		// for with it resource requirement scenario
		boolean temp = element.reserveConnectionWithITResource();
		// for 1+1 protection scenario
		// boolean temp = element.reserveConnection1P1();
		// for multipath scenario
		// boolean temp = element.reserveConnection();
		if (temp == true) {
			EventHandler.addEvent(new ReleaseConnection(element, source));
			source.connectionReserved();
		} else {
			source.connectionBlocked();
		}
		// source.nextConnection(true);
		source.nextRequest(true);
		if (Integer.parseInt(element.getResvID()) >= 1000) {
			source.logGraphState();
		}

	}

}
