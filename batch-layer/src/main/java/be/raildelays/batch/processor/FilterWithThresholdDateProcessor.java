package be.raildelays.batch.processor;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * We filter items based on a threshold date.
 * You have the choice to keep what is before or after the threshold date.
 *
 * @author Almex
 * @since 1.2
*/
public class FilterWithThresholdDateProcessor implements ItemProcessor<ExcelRow, ExcelRow>, InitializingBean {

    private LocalDate thresholdDate;

    private Mode mode;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(thresholdDate, "The 'thresholdDate' property must be provided");
    }

    public enum Mode {
        BEFORE, AFTER_OR_EQUALS
    }

    @Override
    public ExcelRow process(ExcelRow item) throws Exception {
        ExcelRow result = null;

        if (item.getDate() != null) {
            LocalDate itemDate = item.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            switch (mode) {
                case BEFORE:
                    if (itemDate.isBefore(thresholdDate)) {
                        result = item;
                    }
                    break;
                case AFTER_OR_EQUALS:
                    if (itemDate.isAfter(thresholdDate) || itemDate.isEqual(thresholdDate)) {
                        result = item;
                    }
                    break;
            }
        }

        return result;
    }

    public void setThresholdDate(Date thresholdDate) {
        this.thresholdDate = thresholdDate != null ? thresholdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
