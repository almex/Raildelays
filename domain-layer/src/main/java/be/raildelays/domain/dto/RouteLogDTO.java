package be.raildelays.domain.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public final class RouteLogDTO implements Serializable {

	private static final long serialVersionUID = -546508375202547836L;

	@NotNull
	private final String trainId;

	@NotNull
	private final Date date;

	@NotNull
	@Size(min = 1)
	private final List<ServedStopDTO> stops;

	public RouteLogDTO(final String trainId, final Date date) {
		this.trainId = trainId;
		this.date = date;
		stops = new ArrayList<>();
	}

	public void addStop(final ServedStopDTO stop) {
		this.stops.add(stop.clone());
	}

	public String getTrainId() {
		return trainId;
	}

	public Date getDate() {
		return (Date) (date != null ? date.clone() : null);
	}

	public List<ServedStopDTO> getStops() {
		return stops != null ? Collections.unmodifiableList(stops) : null;
	}

    @Override
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        return new StringBuilder("RouteLogDTO: ") //
                .append("{ ") //
                .append("trainId: " + trainId) //
                .append(", ") //
                .append("date: ")
                .append(date != null ? df.format(date) : "N/A") //
                .append("} ").toString();
    }
}
