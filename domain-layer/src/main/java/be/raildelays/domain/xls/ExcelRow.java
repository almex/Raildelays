package be.raildelays.domain.xls;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;

/**
 * Describe a row in the delays Excel workbook.
 * 
 * @author Almex
 */
public class ExcelRow {	
	
	private Date date;
	
	private Station arrivalStation;	

	private Station departureStation;

	private Station linkStation;
	
	private Date expectedDepartureHour;
	
	private Date expectedArrivalHour;
	
	private Train expectedTrain1;
	
	private Train expectedTrain2;
	
	private Date effectiveDepartureHour;
	
	private Date effectiveArrivalHour;
	
	private Train effectiveTrain1;
	
	private Train effectiveTrain2;
	
	private long delay;
	
	private Sens sens;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Station getArrivalStation() {
		return arrivalStation;
	}

	public void setArrivalStation(Station arrivalStation) {
		this.arrivalStation = arrivalStation;
	}

	public Station getDepartureStation() {
		return departureStation;
	}

	public void setDepartureStation(Station departureStation) {
		this.departureStation = departureStation;
	}

	public Station getLinkStation() {
		return linkStation;
	}

	public void setLinkStation(Station linkStation) {
		this.linkStation = linkStation;
	}

	public Date getExpectedDepartureHour() {
		return expectedDepartureHour;
	}

	public void setExpectedDepartureHour(Date expectedDepartureHour) {
		this.expectedDepartureHour = expectedDepartureHour;
	}

	public Date getExpectedArrivalHour() {
		return expectedArrivalHour;
	}

	public void setExpectedArrivalHour(Date expectedArrivalHour) {
		this.expectedArrivalHour = expectedArrivalHour;
	}

	public Train getExpectedTrain1() {
		return expectedTrain1;
	}

	public void setExpectedTrain1(Train expectedTrain1) {
		this.expectedTrain1 = expectedTrain1;
	}

	public Train getExpectedTrain2() {
		return expectedTrain2;
	}

	public void setExpectedTrain2(Train expectedTrain2) {
		this.expectedTrain2 = expectedTrain2;
	}

	public Date getEffectiveDepartureHour() {
		return effectiveDepartureHour;
	}

	public void setEffectiveDepartureHour(Date effectiveDepartureHour) {
		this.effectiveDepartureHour = effectiveDepartureHour;
	}

	public Date getEffectiveArrivalHour() {
		return effectiveArrivalHour;
	}

	public void setEffectiveArrivalHour(Date effectiveArrivalHour) {
		this.effectiveArrivalHour = effectiveArrivalHour;
	}

	public Train getEffectiveTrain1() {
		return effectiveTrain1;
	}

	public void setEffectiveTrain1(Train effectiveTrain1) {
		this.effectiveTrain1 = effectiveTrain1;
	}

	public Train getEffectiveTrain2() {
		return effectiveTrain2;
	}

	public void setEffectiveTrain2(Train effectiveTrain2) {
		this.effectiveTrain2 = effectiveTrain2;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public Sens getSens() {
		return sens;
	}

	public void setSens(Sens sens) {
		this.sens = sens;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
		
		builder.append(df.format(date));
		builder.append(" ");
		builder.append(notNullToString(departureStation));
		builder.append(" ");
		builder.append(notNullToString(arrivalStation));
		builder.append(" ");
		builder.append(notNullToString(linkStation));
		builder.append(" ");
		builder.append(tf.format(expectedDepartureHour));
		builder.append(" ");
		builder.append(tf.format(expectedArrivalHour));
		builder.append(" ");
		builder.append(notNullToString(expectedTrain1));
		builder.append(" ");
		builder.append(notNullToString(expectedTrain2));
		builder.append(" ");
		builder.append(tf.format(effectiveDepartureHour));
		builder.append(" ");
		builder.append(tf.format(effectiveArrivalHour));
		builder.append(" ");
		builder.append(notNullToString(effectiveTrain1));
		builder.append(" ");
		builder.append(notNullToString(effectiveTrain2));
		builder.append(" ");
		builder.append(delay);
		builder.append(" ");
		builder.append(sens);
		
		return builder.toString();
	}
	
	private static String notNullToString(Station station) { 
		String result = "";
		
		if (station != null) {
			result = notNullToString(station.getEnglishName());
		}
		
		return result;
	}
	
	private static String notNullToString(Train train) { 
		String result = "";
		
		if (train != null) {
			result = notNullToString(train.getEnglishName());
		}
		
		return result;
	}
	
	private static String notNullToString(Object obj) {
		String result = "";
		
		if (obj != null) {
			result = StringUtils.trimToEmpty(obj.toString());
		}
		
		return result;
	}
	
}
