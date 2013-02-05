package be.raildelays.domain.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class ServedStopDTO implements Serializable, Cloneable {

	private static final long serialVersionUID = 3019492480070457922L;

	@NotNull
	private final String stationName;

	@NotNull
	private final Date arrivalTime;

	@NotNull
	private final Date departureTime;

	private final long arrivalDelay;

	private final long departureDelay;

	private final boolean canceled;

	public ServedStopDTO(final String stationName, final Date departure,
			final long departureDelay, final Date arrival,
			final long arrivalDelay, final boolean canceled) {
		this.stationName = stationName;
		this.canceled = canceled;
		this.departureTime = departure;
		this.departureDelay = departureDelay;
		this.arrivalTime = arrival;
		this.arrivalDelay = arrivalDelay;
	}

	@Override
	public ServedStopDTO clone() {
		try {
			return (ServedStopDTO) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("The parent class is not cloneable", e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
		} else {
			if (obj instanceof ServedStopDTO) {
				ServedStopDTO stop = (ServedStopDTO) obj;

				result = new EqualsBuilder() //
						.append(stationName, stop.getStationName())
						.isEquals();
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(11, 3) //
				.append(stationName)
				.toHashCode();
	}

	public String getStationName() {
		return stationName;
	}

	public Date getArrivalTime() {
		return (Date) (arrivalTime != null ? arrivalTime.clone() : null);
	}

	public Date getDepartureTime() {
		return (Date) (departureTime != null ? departureTime.clone() : null);
	}

	public long getArrivalDelay() {
		return arrivalDelay;
	}

	public long getDepartureDelay() {
		return departureDelay;
	}

	public boolean isCanceled() {
		return canceled;
	}
}
