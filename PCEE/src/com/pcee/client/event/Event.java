package com.pcee.client.event;


public abstract class Event implements Comparable<Event>{

	/**Variable to store event run time*/
	private double time;
	
	/**Variable to set the priority of an event in case of a time tie*/
	protected int priority =0;
	
	/**function to get the time of execution of the event*/
	public double getTime(){
		return time;
	}
	
	/**function to set the time of execution of the event*/
	public void setTime(double time){
		this.time = time;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public abstract void execute();

	public int compareTo(Event o) {
		if (this.time < o.getTime()){
			return 1;
		}
		else if (this.time > o.getTime()){
			return 0;
		}
		else{
			if (this.priority>=o.getPriority())
				return 1;
			else
				return 0;
		}
	}

}
