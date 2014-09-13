package be.raildelays.batch.bean;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.ItemCountAware;
import org.springframework.batch.item.ItemIndexAware;

import java.util.Date;

public class BatchExcelRow extends ExcelRow implements ItemIndexAware, ItemCountAware {

    private boolean canceled;

    private Long index;

    private BatchExcelRow(Builder builder) {
        super(builder);
        this.canceled = builder.canceled;
        this.index = builder.index;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    @Override
    public void setItemCount(int count) {
        setIndex(new Long(count));
    }

    public static class Builder extends ExcelRow.Builder {

        protected boolean canceled;
        protected Long index;

        public Builder(Date date, Sens sens) {
            super(date, sens);
        }

        public Builder canceled(boolean canceled) {
            this.canceled = canceled;
            return this;
        }

        @Override
        public BatchExcelRow build() {
            return new BatchExcelRow(this);
        }

        @Override
        public Builder arrivalStation(final Station arrivalStation) {
            super.arrivalStation(arrivalStation);
            return this;
        }

        @Override
        public Builder departureStation(final Station departureStation) {
            super.departureStation(departureStation);
            return this;
        }

        @Override
        public Builder linkStation(final Station linkStation) {
            super.linkStation(linkStation);
            return this;
        }

        @Override
        public Builder expectedDepartureTime(
                final Date expectedDepartureTime) {
            super.expectedDepartureTime(expectedDepartureTime);
            return this;
        }

        @Override
        public Builder expectedArrivalTime(
                final Date expectedArrivalTime) {
            super.expectedArrivalTime(expectedArrivalTime);
            return this;
        }

        @Override
        public Builder expectedTrain1(final Train expectedTrain1) {
            super.expectedTrain1(expectedTrain1);
            return this;
        }

        @Override
        public Builder expectedTrain2(final Train expectedTrain2) {
            super.expectedTrain2(expectedTrain2);
            return this;
        }

        @Override
        public Builder effectiveDepartureTime(
                final Date effectiveDepartureTime) {
            super.effectiveDepartureTime(effectiveDepartureTime);
            return this;
        }

        @Override
        public Builder effectiveArrivalTime(
                final Date effectiveArrivalTime) {
            super.effectiveArrivalTime(effectiveArrivalTime);
            return this;
        }

        @Override
        public Builder effectiveTrain1(final Train effectiveTrain1) {
            super.effectiveTrain1(effectiveTrain1);
            return this;
        }

        public Builder effectiveTrain2(final Train effectiveTrain2) {
            super.effectiveTrain2(effectiveTrain2);
            return this;
        }

        @Override
        public Builder delay(final Long delay) {
            super.delay(delay);
            return this;
        }

        public Builder index(final Long index) {
            this.index = index;
            return this;
        }
    }
}
