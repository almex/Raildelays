package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Direction implements Serializable {

	private static final long serialVersionUID = -546508375202547836L;
	
	private Train train;
	private List<Step> steps = new ArrayList<>();
	private Station from;
	private Station to;
	private String libelle;
	
	public Direction (Train train) {
		this.setTrain(train);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("title="+libelle+"\n");
		
		for(Step step : steps) {
			if(step.getDelay() > 15 || step.isCanceled()) {
				builder.append("station="+step.getStation()+"\n");
				if(step.isCanceled()) {
					builder.append("Canceled!"+"\n");
				} else {
					builder.append("timestamp="+step.getTimestamp()+"\n")
						   .append("delay="+step.getDelay()+"\n");
				}
			}
		}
		
		return builder.toString();
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

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

}
