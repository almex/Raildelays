package be.raildelays.batch.bean;

import java.util.Date;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;

public class BatchExcelRow extends ExcelRow {

	private boolean canceled;

	private BatchExcelRow(Builder builder) {
		super(builder);
		this.canceled = builder.canceled;
	}
	
	public static class Builder extends ExcelRow.Builder {
		
		protected boolean canceled;

		public Builder(Date date, Sens sens) {
			super(date, sens);
		}
		
		public Builder canceled(boolean canceled) {
			this.canceled  = canceled;
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
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}
