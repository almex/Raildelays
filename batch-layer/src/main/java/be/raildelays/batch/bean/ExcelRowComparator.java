package be.raildelays.batch.bean;

import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

/**
 * @author Almex
 */
public class ExcelRowComparator implements Comparator<ExcelRow> {

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
                    .append(lho.getArrivalStation(), rho.getArrivalStation())
                    .append(lho.getDepartureStation(), rho.getDepartureStation())
                    .append(lho.getLinkStation(), rho.getLinkStation())
                    .append(lho.getExpectedDepartureTime(), rho.getExpectedDepartureTime())
                    .append(lho.getExpectedArrivalTime(), rho.getExpectedArrivalTime())
                    .append(lho.getExpectedTrain1(), rho.getExpectedTrain1())
                    .append(lho.getExpectedTrain2(), rho.getExpectedTrain2())
                    .append(lho.getEffectiveDepartureTime(), rho.getEffectiveDepartureTime())
                    .append(lho.getEffectiveArrivalTime(), rho.getEffectiveArrivalTime())
                    .append(lho.getEffectiveTrain1(), rho.getEffectiveTrain1())
                    .append(lho.getEffectiveTrain2(), rho.getEffectiveTrain2())
                    .append(lho.getDelay(), rho.getDelay())
                    .toComparison();
        }

        return result;
    }
}
