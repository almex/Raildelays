package be.raildelays.batch.bean;

import java.util.Date;

import org.eclipse.persistence.internal.indirection.ProtectedValueHolder;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;

public class BatchExcelRow extends ExcelRow {

	private boolean canceled;

	private BatchExcelRow(BatchExcelRowBuilder builder) {
		super(builder);
		this.canceled = builder.canceled;
	}
	
	public static class BatchExcelRowBuilder extends ExcelRowBuilder {
		
		protected boolean canceled;

		public BatchExcelRowBuilder(Date date, Sens sens) {
			super(date, sens);
		}
		
		public void canceled(boolean canceled) {
			this.canceled  = canceled;
		}

		@Override
		public BatchExcelRow build() {			
			return new BatchExcelRow(this);
		}
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}
