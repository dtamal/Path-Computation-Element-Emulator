package com.pcee.client.event.eventhandler;

import java.util.ArrayList;
import com.global.GlobalCfg;
import com.pcee.client.event.Event;

public class EventHandler {
	
	/**ArrayList to store events */
	private static ArrayList<Event> eventList = new ArrayList<Event>();
	
	/**variables for start and end time*/
	private static double startTime=0, endTime;
	
	/**Variable to store the current time*/
	private static double currTime=0;
	
	/**Function to add an event to the event handler*/
	public static void addEvent(Event event){
		if (eventList.size()==0)
			/**if list is empty add event at top*/
			eventList.add(event);
		else
		{
			/**If list is not empty then add at specific position*/
			int i, flag=0;
			for (i=0;i<eventList.size();i++){
				if (event.compareTo(eventList.get(i)) == 1){
					eventList.add(i, event);
					flag=1;
					break;
				}
			}
			/**If flag is 0 then add at the end of the list*/
			if (flag==0)
				eventList.add(event);
		}
	}
	/**function to get the current run time*/
	public static double getTime(){
		return currTime;
	}
	
	/**function to get the end time*/
	public static double getEndTime(){
		return endTime;
	}
	
	/**Function to initialize the event Handler*/
	public static void initEventHandler(){
		eventList.clear();
		startTime=GlobalCfg.startTime;
		currTime = startTime;
		endTime=GlobalCfg.endTime;
	}
	
	/**Function to initialize the event Handler*/
	public static void startEventHandler() {
		Event event;
		while(currTime<endTime){
			event= eventList.get(0);
			System.out.println("About to handle the event at time: " + event.getTime());
			eventList.remove(0);
			currTime = event.getTime();
			event.execute();
			if (eventList.size()==0)
				break;
		}
	}
}
