package be.raildelays.domain.railtime;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class Stop implements Serializable {

    private static final long serialVersionUID = 3019492480070457922L;

    @NotNull
    protected Station station;

    @NotNull
    protected Date timestamp;

    public Stop(String stationName, Date timestamp) {
        this.station = new Station(stationName);
        this.timestamp = timestamp;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
