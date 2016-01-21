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

package be.raildelays.batch.bean;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.IndexedItem;
import org.springframework.batch.item.ItemCountAware;

import java.time.LocalDate;
import java.time.LocalTime;

public class BatchExcelRow extends ExcelRow<BatchExcelRow> implements IndexedItem, ItemCountAware {

    public static final BatchExcelRow EMPTY = new Builder(null, null).delay(0L).build(false);
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

        public Builder(LocalDate date, Sens sens) {
            super(date, sens);
        }

        public Builder canceled(boolean canceled) {
            this.canceled = canceled;
            return this;
        }

        @Override
        public BatchExcelRow build() {
            return build(true);
        }

        @Override
        public BatchExcelRow build(boolean validate) {
            BatchExcelRow result = new BatchExcelRow(this);

            if (validate) {
                super.validate(result);
            }

            return result;
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
                final LocalTime expectedDepartureTime) {
            super.expectedDepartureTime(expectedDepartureTime);
            return this;
        }

        @Override
        public Builder expectedArrivalTime(
                final LocalTime expectedArrivalTime) {
            super.expectedArrivalTime(expectedArrivalTime);
            return this;
        }

        @Override
        public Builder expectedTrain1(final TrainLine expectedTrainLine1) {
            super.expectedTrain1(expectedTrainLine1);
            return this;
        }

        @Override
        public Builder expectedTrain2(final TrainLine expectedTrainLine2) {
            super.expectedTrain2(expectedTrainLine2);
            return this;
        }

        @Override
        public Builder effectiveDepartureTime(
                final LocalTime effectiveDepartureTime) {
            super.effectiveDepartureTime(effectiveDepartureTime);
            return this;
        }

        @Override
        public Builder effectiveArrivalTime(
                final LocalTime effectiveArrivalTime) {
            super.effectiveArrivalTime(effectiveArrivalTime);
            return this;
        }

        @Override
        public Builder effectiveTrain1(final TrainLine effectiveTrainLine1) {
            super.effectiveTrain1(effectiveTrainLine1);
            return this;
        }

        public Builder effectiveTrain2(final TrainLine effectiveTrainLine2) {
            super.effectiveTrain2(effectiveTrainLine2);
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
