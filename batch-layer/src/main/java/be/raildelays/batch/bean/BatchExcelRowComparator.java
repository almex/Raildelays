package be.raildelays.batch.bean;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

/**
 * @author Almex
 */
public class BatchExcelRowComparator implements Comparator<BatchExcelRow> {

    @Override
    public int compare(BatchExcelRow o1, BatchExcelRow o2) {
        int result = 0;

        if (o1 == null) {
            result = o2 == null ? 0 : 1;
        } else if (o2 == null) {
            result = -1;
        } else {
            result = CompareToBuilder.reflectionCompare(o1, o2);
        }

        return result;
    }
}
