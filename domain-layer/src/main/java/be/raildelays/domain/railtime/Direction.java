package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Direction implements Serializable {

	private static final long serialVersionUID = -546508375202547836L;
	
	@NotNull
	private Train train;
	
	@NotNull
	private List<Step> steps = new ArrayList<>();
	
	@NotNull
	private Station from;
	
	@NotNull
	private Station to;
	
	@NotNull
	@Size(min=1, max=256)
	private String libelle;
	
	public Direction (Train train) {
		this.train = train;
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
