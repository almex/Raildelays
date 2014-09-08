package be.raildelays.domain.railtime;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    @Override
    public int hashCode() {
        int hash = super.hashCode();

        hash = hash * 7 + (ordinance != null ? ordinance.hashCode() : 0);
        hash = hash * 13 + (delay != null ? delay.hashCode() : 0);
        hash = hash * 3 + (canceled ? 1 : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = super.equals(object);

        if (result && object instanceof Step) {
            Step step = (Step) object;

            result = new EqualsBuilder()
                    .append(this.ordinance, step.ordinance)
                    .append(this.delay, step.delay)
                    .append(this.canceled, step.canceled)
                    .build();
        }

        return result;
    }

}
