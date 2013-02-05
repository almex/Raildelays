package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class Step extends Stop implements Serializable, Comparable<Step> {

	private static final long serialVersionUID = -3386080893909407089L;

	private Long delay;

	private boolean canceled;

	private Integer ordinance;

	public Step(Integer ordinance, String stationName, Date timestamp,
			Long delay, boolean canceled) {
		super(stationName, timestamp);
		this.ordinance = ordinance;
		this.delay = delay;
		this.canceled = canceled;
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("step=[");

		builder.append(getStation());
		builder.append(", ");

		if (this.isCanceled()) {
			builder.append("canceled=true");
		} else {
			SimpleDateFormat formater = new SimpleDateFormat("HH:mm");
			Calendar effectiveTime = Calendar.getInstance();
			effectiveTime.setTime(getTimestamp());
			effectiveTime.add(Calendar.MINUTE, delay.intValue());

			builder.append("scheduledTime=");
			builder.append(formater.format(getTimestamp()));
			builder.append(", ");
			builder.append("effectiveTime=");
			builder.append(formater.format(effectiveTime.getTime()));
		}

		builder.append("]");

		return builder.toString();
	}

	public Integer getOrdinance() {
		return ordinance;
	}

	public void setOrdinance(Integer ordinance) {
		this.ordinance = ordinance;
	}

	@Override
	public int compareTo(Step step) {
		return new CompareToBuilder() //
				.append(this.getOrdinance(), step.getOrdinance()) //
				.toComparison();
	}

}
