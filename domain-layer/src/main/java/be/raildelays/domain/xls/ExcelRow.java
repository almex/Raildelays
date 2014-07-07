package be.raildelays.domain.xls;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Describe a row in the delays Excel workbook.
 *
 * @author Almex
 */
@Entity
@Table(name = "EXCEL_ROW", uniqueConstraints = @UniqueConstraint(columnNames = {
        "DATE", "SENS"}))
public class ExcelRow implements Comparable<ExcelRow> {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE")
    @NotNull
    @Past
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
    @Past
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
    @Past
    private Date effectiveDepartureTime;

    @Temporal(TemporalType.TIME)
    @Column(name = "EFFECTIVE_ARRIVAL_TIME")
    @NotNull
    @Past
    private Date effectiveArrivalTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EFFECTIVE_TRAIN1_ID")
    @NotNull
    private Train effectiveTrain1;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EFFECTIVE_TRAIN2_ID")
    private Train effectiveTrain2;

    @Column(name = "DELAY")
    @Min(0)
    private Long delay;

    @Column(name = "SENS")
    private Sens sens;

    @Override
    public int compareTo(ExcelRow excelRow) {
        int result = 0;

        if (excelRow == null) {
            result = -1;
        } else {
            // We give only a chronologic order based on expected time        
            result = new CompareToBuilder()
                    .append(excelRow.getDate(), this.getDate())
                    .append(excelRow.getExpectedDepartureTime(), this.getExpectedDepartureTime())
                    .append(excelRow.getExpectedArrivalTime(), this.getExpectedArrivalTime())
                    .toComparison();
        }

        return result;
    }

    public static class Builder {

        protected final Date date;
        protected Station arrivalStation;
        protected Station departureStation;
        protected Station linkStation;
        protected Date expectedDepartureTime;
        protected Date expectedArrivalTime;
        protected Train expectedTrain1;
        protected Train expectedTrain2;
        protected Date effectiveDepartureTime;
        protected Date effectiveArrivalTime;
        protected Train effectiveTrain1;
        protected Train effectiveTrain2;
        protected Long delay;
        protected final Sens sens;

        public Builder(final Date date, final Sens sens) {
            this.date = (Date) (date != null ? date.clone() : null);
            this.sens = sens;
        }

        public Builder arrivalStation(final Station arrivalStation) {
            this.arrivalStation = arrivalStation;
            return this;
        }

        public Builder departureStation(final Station departureStation) {
            this.departureStation = departureStation;
            return this;
        }

        public Builder linkStation(final Station linkStation) {
            this.linkStation = linkStation;
            return this;
        }

        public Builder expectedDepartureTime(
                final Date expectedDepartureTime) {
            this.expectedDepartureTime = expectedDepartureTime;
            return this;
        }

        public Builder expectedArrivalTime(
                final Date expectedArrivalTime) {
            this.expectedArrivalTime = expectedArrivalTime;
            return this;
        }

        public Builder expectedTrain1(final Train expectedTrain1) {
            this.expectedTrain1 = expectedTrain1;
            return this;
        }

        public Builder expectedTrain2(final Train expectedTrain2) {
            this.expectedTrain2 = expectedTrain2;
            return this;
        }

        public Builder effectiveDepartureTime(
                final Date effectiveDepartureTime) {
            this.effectiveDepartureTime = (Date) (effectiveDepartureTime != null ? effectiveDepartureTime
                    .clone() : null);
            return this;
        }

        public Builder effectiveArrivalTime(
                final Date effectiveArrivalTime) {
            this.effectiveArrivalTime = (Date) (effectiveArrivalTime != null ? effectiveArrivalTime
                    .clone() : null);
            return this;
        }

        public Builder effectiveTrain1(final Train effectiveTrain1) {
            this.effectiveTrain1 = effectiveTrain1;
            return this;
        }

        public Builder effectiveTrain2(final Train effectiveTrain2) {
            this.effectiveTrain2 = effectiveTrain2;
            return this;
        }

        public Builder delay(final Long delay) {
            this.delay = delay;
            return this;
        }

        public ExcelRow build() {
            return new ExcelRow(this);
        }

    }

    protected ExcelRow(final Builder builder) {
        this.date = builder.date;
        this.arrivalStation = builder.arrivalStation;
        this.departureStation = builder.departureStation;
        this.linkStation = builder.linkStation;
        this.expectedDepartureTime = builder.expectedDepartureTime;
        this.expectedArrivalTime = builder.expectedArrivalTime;
        this.expectedTrain1 = builder.expectedTrain1;
        this.expectedTrain2 = builder.expectedTrain2;
        this.effectiveDepartureTime = builder.effectiveDepartureTime;
        this.effectiveArrivalTime = builder.effectiveArrivalTime;
        this.effectiveTrain1 = builder.effectiveTrain1;
        this.effectiveTrain2 = builder.effectiveTrain2;
        this.delay = builder.delay;
        this.sens = builder.sens;
    }

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

    public Date getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    public void setExpectedDepartureTime(Date expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
    }

    public Date getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(Date expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
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

    public Date getEffectiveDepartureTime() {
        return effectiveDepartureTime;
    }

    public void setEffectiveDepartureTime(Date effectiveDepartureTime) {
        this.effectiveDepartureTime = effectiveDepartureTime;
    }

    public Date getEffectiveArrivalTime() {
        return effectiveArrivalTime;
    }

    public void setEffectiveArrivalTime(Date effectiveArrivalTime) {
        this.effectiveArrivalTime = effectiveArrivalTime;
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
    public int hashCode() {
        return new HashCodeBuilder() //
                .append("date") //
                .append("arrivalStation") //
                .append("departureStation") //
                .append("linkStation") //
                .append("expectedTrain1") //
                .append("expectedTrain2") //
                .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else if (obj instanceof ExcelRow) {
            ExcelRow target = (ExcelRow) obj;

            result = new EqualsBuilder() //
                    .append(this.date, target.date) //
                    .append(this.arrivalStation, target.arrivalStation) //
                    .append(this.departureStation, target.departureStation) //
                    .append(this.linkStation, target.linkStation) //
                    .append(this.expectedTrain1, target.expectedTrain1) //
                    .append(this.expectedTrain2, target.expectedTrain2) //
                    .isEquals();
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");

        builder.append(date != null ? df.format(date) : "N/A");
        builder.append(" ");
        builder.append(notNullToString(departureStation));
        builder.append(" ");
        builder.append(notNullToString(arrivalStation));
        builder.append(" ");
        builder.append(notNullToString(linkStation));
        builder.append(" ");
        builder.append(expectedDepartureTime != null ? tf.format(expectedDepartureTime) : "N/A");
        builder.append(" ");
        builder.append(expectedArrivalTime != null ? tf.format(expectedArrivalTime) : "N/A");
        builder.append(" ");
        builder.append(notNullToString(expectedTrain1));
        builder.append(" ");
        builder.append(notNullToString(expectedTrain2));
        builder.append(" ");
        builder.append(effectiveDepartureTime != null ? tf.format(effectiveDepartureTime) : "N/A");
        builder.append(" ");
        builder.append(effectiveArrivalTime != null ? tf.format(effectiveArrivalTime) : "N/A");
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

    protected static String notNullToString(Station station) {
        String result = "";

        if (station != null) {
            result = notNullToString(station.getEnglishName());
        }

        return result;
    }

    protected static String notNullToString(Train train) {
        String result = "";

        if (train != null) {
            result = notNullToString(train.getEnglishName());
        }

        return result;
    }

    protected static String notNullToString(Object obj) {
        String result = "";

        if (obj != null) {
            result = StringUtils.trimToEmpty(obj.toString());
        }

        return result;
    }

}
