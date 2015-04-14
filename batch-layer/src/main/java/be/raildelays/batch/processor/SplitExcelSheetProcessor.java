package be.raildelays.batch.processor;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;
import java.util.function.Consumer;

/**
 * We filter items based on a threshold date.
 * You have the choice to keep what is before or after the threshold date.
 *
 * @author Almex
 * @since 1.2
*/
public class SplitExcelSheetProcessor implements ItemProcessor<ExcelRow, ExcelRow> {

    private Date thresholdDate;

    private Mode mode;

    public static enum Mode {
        BEFORE, AFTER_OR_EQUALS;
    }

    @Override
    public ExcelRow process(ExcelRow item) throws Exception {
        ExcelRow result = null;

        switch (mode) {
            case BEFORE:
                if (item.getDate().before(thresholdDate)) {
                    result = item;
                }
                break;
            case AFTER_OR_EQUALS:
                if (item.getDate().after(thresholdDate) || item.getDate().equals(thresholdDate)) {
                    result = item;
                }
                break;
        }

        return result;
    }

    public void setThresholdDate(Date thresholdDate) {
        this.thresholdDate = thresholdDate;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
