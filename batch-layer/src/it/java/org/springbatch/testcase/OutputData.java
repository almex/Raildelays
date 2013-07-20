package org.springbatch.testcase;

import java.io.Serializable;

public class OutputData implements Serializable {

	private static final long serialVersionUID = 1L;


	public enum Event {
		ADD, DELETE, MODIFY
	}
	
	private InputData data;
	
	private Event event;
	
	
	public OutputData(InputData data, Event event) {
		this.data = data;
		this.event = event;
	}

	@Override
	public String toString() {
		return "OutputData [data=" + data + ", event=" + event + "]";
	}


	public Event getEvent() {
		return event;
	}


	public void setEvent(Event event) {
		this.event = event;
	}


	public InputData getData() {
		return data;
	}


	public void setData(DataFromSource1 data) {
		this.data = data;
	}
}
