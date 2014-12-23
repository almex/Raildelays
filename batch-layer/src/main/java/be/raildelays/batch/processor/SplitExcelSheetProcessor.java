package be.raildelays.batch.processor;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;

/**
* Created by Almex on 21/12/2014.
*/
public class SplitExcelSheetProcessor implements ItemProcessor<ExcelRow, ExcelRow> {

    private Date thresholdDate;

    @Override
    public ExcelRow process(ExcelRow item) throws Exception {

        if (item.getDate().before(thresholdDate)) {
            return null;
        } else {
            return item;
        }
    }

    public void setThresholdDate(Date thresholdDate) {
        this.thresholdDate = thresholdDate;
    }
}
