package be.raildelays.batch.bean;

import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

/**
 * @author Almex
 */
public class StationBasedBatchExcelRowComparator implements Comparator<ExcelRow> {

    @Override
    public int compare(ExcelRow lho, ExcelRow rho) {
        int result;

        if (lho == rho) {
            result = 0;
        } else if (lho == null) {
            result = rho == null ? 0 : 1;
        } else if (rho == null) {
            result = -1;
        } else {
            result = new CompareToBuilder()
                    .append(lho.getDate(), rho.getDate())
                    .append(lho.getDepartureStation(), rho.getDepartureStation())
                    .append(lho.getArrivalStation(), rho.getArrivalStation())
                    .toComparison();
        }

        return result;
    }
}
