package com.pcee.client.event.impl;

import com.pcee.client.connectionsource.Source;
import com.pcee.client.event.Event;
import com.pcee.client.resv.ResvElement;

public class ReleaseConnection extends Event {

	private ResvElement element;

	private Source source;

	public ReleaseConnection(ResvElement element, Source source) {
		this.element = element;
		this.setTime(element.getEndTime());
		priority = 1;
		this.source = source;
	}

	@Override
	public void execute() {
		// for it resource scenario
		element.releaseConnectionAndITResource();
		// for multipath scenario
		// element.releaseConnection();
		source.logGraphState();
	}

}
