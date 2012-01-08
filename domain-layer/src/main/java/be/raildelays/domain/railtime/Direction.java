package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Direction implements Serializable {

	private static final long serialVersionUID = -546508375202547836L;
	
	private Train train;
	private List<Step> steps = new ArrayList<>();
	private Integer totalDelay = null;
	private Station from;
	private Station to;
	
	public Direction (Train train) {
		this.setTrain(train);
	}
	
	public Integer getTotalDelay() {
		if (totalDelay == null) {
			totalDelay = 0;
			
			for(Step step : steps) {
				this.totalDelay += step.getDelay();
			}
		} 
		
		return totalDelay;
	}
	
	public Integer getDelayBetween(Station from, Station to) {
		return 0;
	}
	
	public List<Step> getSteps() {
		return steps;
	}
	
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public Train getTrain() {
		return train;
	}

	public void setTrain(Train train) {
		this.train = train;
	}

	public Station getFrom() {
		return from;
	}

	public void setFrom(Station from) {
		this.from = from;
	}

	public Station getTo() {
		return to;
	}

	public void setTo(Station to) {
		this.to = to;
	}

}
