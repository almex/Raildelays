package be.raildelays.domain.xls;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;

/**
 * Describe a row in the delays Excel workbook.
 * 
 * @author Almex
 */
@Entity
@Table(name = "EXCEL_ROW", uniqueConstraints = @UniqueConstraint(columnNames = {
		"DATE", "SENS" }))
public class ExcelRow {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID")
	private Long id;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE")
	@NotNull
	private Date date;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "ARRIVAL_STATION_ID")
	@NotNull
	private Station arrivalStation;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "DEPARTURE_STATION_ID")
	@NotNull
	private Station departureStation;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "LINK_STATION_ID")
	private Station linkStation;

	@Temporal(TemporalType.TIME)
	@Column(name = "EXPECTED_DEPARTURE_TIME")
	@NotNull
	private Date expectedDepartureTime;

	@Temporal(TemporalType.TIME)
	@Column(name = "EXPECTED_ARRIVAL_TIME")
	@NotNull
	private Date expectedArrivalTime;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "EXPEXTED_TRAIN1_ID")
	@NotNull
	private Train expectedTrain1;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "EXPEXTED_TRAIN2_ID")
	private Train expectedTrain2;

	@Temporal(TemporalType.TIME)
	@Column(name = "EFFECTIVE_DEPARTURE_TIME")
	@NotNull
	private Date effectiveDepartureTime;

	@Temporal(TemporalType.TIME)
	@Column(name = "EFFECTIVE_ARRIVAL_TIME")
	@NotNull
	private Date effectiveArrivalTime;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "EFFECTIVE_TRAIN1_ID")
	@NotNull
	private Train effectiveTrain1;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "EFFECTIVE_TRAIN2_ID")
	private Train effectiveTrain2;

	@Column(name = "DELAY")
	private Long delay;

	@Column(name = "SENS")
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
		return expectedDepartureTime;
	}

	public void setExpectedDepartureHour(Date expectedDepartureHour) {
		this.expectedDepartureTime = expectedDepartureHour;
	}

	public Date getExpectedArrivalHour() {
		return expectedArrivalTime;
	}

	public void setExpectedArrivalHour(Date expectedArrivalHour) {
		this.expectedArrivalTime = expectedArrivalHour;
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
		return effectiveDepartureTime;
	}

	public void setEffectiveDepartureHour(Date effectiveDepartureHour) {
		this.effectiveDepartureTime = effectiveDepartureHour;
	}

	public Date getEffectiveArrivalHour() {
		return effectiveArrivalTime;
	}

	public void setEffectiveArrivalHour(Date effectiveArrivalHour) {
		this.effectiveArrivalTime = effectiveArrivalHour;
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

	public Long getId() {
		return id;
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
		builder.append(tf.format(expectedDepartureTime));
		builder.append(" ");
		builder.append(tf.format(expectedArrivalTime));
		builder.append(" ");
		builder.append(notNullToString(expectedTrain1));
		builder.append(" ");
		builder.append(notNullToString(expectedTrain2));
		builder.append(" ");
		builder.append(tf.format(effectiveDepartureTime));
		builder.append(" ");
		builder.append(tf.format(effectiveArrivalTime));
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
