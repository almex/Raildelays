/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.domain.xls;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.AbstractI18nEntity;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Describe a row in the delays Excel workbook.
 *
 * @author Almex
 */
@Entity
@Table(name = "EXCEL_ROW", uniqueConstraints = @UniqueConstraint(columnNames = {
        "DATE", "SENS"}))
public class ExcelRow implements Comparable<ExcelRow>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATE")
    @NotNull
    @Past
    private LocalDate date;

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

    @Column(name = "EXPECTED_DEPARTURE_TIME")
    @NotNull
    private LocalTime expectedDepartureTime;

    @Column(name = "EXPECTED_ARRIVAL_TIME")
    @NotNull
    @Past
    private LocalTime expectedArrivalTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EXPEXTED_TRAIN1_ID")
    @NotNull
    private Train expectedTrain1;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EXPEXTED_TRAIN2_ID")
    private Train expectedTrain2;

    @Column(name = "EFFECTIVE_DEPARTURE_TIME")
    @NotNull
    @Past
    private LocalTime effectiveDepartureTime;

    @Column(name = "EFFECTIVE_ARRIVAL_TIME")
    @NotNull
    @Past
    private LocalTime effectiveArrivalTime;

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

    protected static String notNullToString(AbstractI18nEntity entity) {
        String result = "";

        if (entity != null) {
            if (StringUtils.isNotBlank(entity.getEnglishName())) {
                result = entity.getEnglishName();
            } else if (StringUtils.isNotBlank(entity.getFrenchName())) {
                result = entity.getFrenchName();
            } else if (StringUtils.isNotBlank(entity.getDutchName())) {
                result = entity.getDutchName();
            }
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

    @Override
    public int compareTo(ExcelRow excelRow) {
        int result;

        if (excelRow == this) {
            result = 0;
        } else if (excelRow == null) {
            result = 1;
        } else {
            // We give only a chronological order based on expectedTime time
            result = new CompareToBuilder()
                    .append(this.getDate(), excelRow.getDate())
                    .append(this.getExpectedDepartureTime(), excelRow.getExpectedDepartureTime())
                    .append(this.getExpectedArrivalTime(), excelRow.getExpectedArrivalTime())
                    .toComparison();
        }

        return result;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public LocalTime getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    public void setExpectedDepartureTime(LocalTime expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
    }

    public LocalTime getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(LocalTime expectedArrivalTime) {
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

    public LocalTime getEffectiveDepartureTime() {
        return effectiveDepartureTime;
    }

    public void setEffectiveDepartureTime(LocalTime effectiveDepartureTime) {
        this.effectiveDepartureTime = effectiveDepartureTime;
    }

    public LocalTime getEffectiveArrivalTime() {
        return effectiveArrivalTime;
    }

    public void setEffectiveArrivalTime(LocalTime effectiveArrivalTime) {
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

    public Long getDelay() {
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

    public static class Builder {

        private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        protected final LocalDate date;
        protected final Sens sens;
        protected Station arrivalStation;
        protected Station departureStation;
        protected Station linkStation;
        protected LocalTime expectedDepartureTime;
        protected LocalTime expectedArrivalTime;
        protected Train expectedTrain1;
        protected Train expectedTrain2;
        protected LocalTime effectiveDepartureTime;
        protected LocalTime effectiveArrivalTime;
        protected Train effectiveTrain1;
        protected Train effectiveTrain2;
        protected Long delay;

        public Builder(final LocalDate date, final Sens sens) {
            this.date = date;
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
                final LocalTime expectedDepartureTime) {
            this.expectedDepartureTime = expectedDepartureTime;
            return this;
        }

        public Builder expectedArrivalTime(
                final LocalTime expectedArrivalTime) {
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
                final LocalTime effectiveDepartureTime) {
            this.effectiveDepartureTime = effectiveDepartureTime;
            return this;
        }

        public Builder effectiveArrivalTime(
                final LocalTime effectiveArrivalTime) {
            this.effectiveArrivalTime = effectiveArrivalTime;
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
            return build(true);
        }

        public ExcelRow build(boolean validate) {
            ExcelRow result = new ExcelRow(this);

            if (validate) {
                validate(result);
            }

            return result;
        }

        protected void validate(ExcelRow row) {
            Set<ConstraintViolation<ExcelRow>> constraintViolations = validator.validate(row);

            if (!constraintViolations.isEmpty()) {
                StringBuilder builder = new StringBuilder();

                for (ConstraintViolation<? extends ExcelRow> constraintViolation : constraintViolations) {
                    builder.append("\nConstraints violations occurred: ");
                    builder.append(constraintViolation.getPropertyPath());
                    builder.append(' ');
                    builder.append(constraintViolation.getMessage());
                }

                throw new ValidationException(builder.toString());
            }
        }
    }

}
