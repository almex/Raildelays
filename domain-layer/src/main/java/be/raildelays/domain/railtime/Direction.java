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
		StringBuilder builder = new StringBuilder("direction=[");
		
		builder.append("title=");
		builder.append(libelle);
		builder.append(", {");
		
		for(Step step : steps) {
			if(step.getDelay() > 15 || step.isCanceled()) {
				builder.append(step.toString());
				builder.append(", ");
			}
		}
		
		builder.append("}]");
		
		return builder.toString();
	}
	
	public boolean isDelayed() {
		return isDelayed(0L);
	}
	
	public boolean isDelayed(Long delayThreshold) {
		boolean result = false;
		
		for(Step step : steps) {
			if (step.getDelay() >= delayThreshold) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public boolean isDelayedAt(String stationName, Long delayThreshold) {
		boolean result = false;		
		Step step = getStepAt(stationName);
		
		if (step != null && (step.getDelay() >= delayThreshold || step.isCanceled())) {
			result = true;
		}
		
		return result;
	}
	
	public Step getStepAt(String stationName) {
		Step result = null;
		
		for(Step step : steps) {
			if (step.getStation().getName().equals(stationName)) {
				result = step;
				break;
			}
		}
		
		return result;
	}
	
	public String toStringDelayBetween(String stationName1, String stationName2, Long delayThreshold) {
		StringBuilder builder = new StringBuilder();
		Step stepFrom = null;
		Step stepTo = null;
		Step step1 = getStepAt(stationName1);
		Step step2 = getStepAt(stationName2);
		
		int index1 = steps.indexOf(step1);
		int index2 = steps.indexOf(step2);
		
		if (index1 < index2) {
			stepFrom = step1;
			stepTo = step2;
		} else if (index1 > index2) {
			stepFrom = step2;
			stepTo = step1;			
		}
		
		if (stepTo != null && isDelayedAt(stepTo.getStation().getName(), delayThreshold)) {
			if (stepFrom != null && stepFrom.getStation() != null) {
				builder.append(stepFrom.getStation().getName());
			}
			builder.append(" -> ");
			builder.append(stepTo.getStation().getName());
			builder.append(" (");
			builder.append(getTrain().getIdRailtime());
			builder.append(") ");
			builder.append("\n\n");
			builder.append(stepFrom);
			builder.append("\n");
			builder.append(stepTo);
			builder.append("\n");
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
