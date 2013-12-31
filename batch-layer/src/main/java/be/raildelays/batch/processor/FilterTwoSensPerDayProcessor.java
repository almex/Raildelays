package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter items to get only two. One for departure and the other one for arrival.
 * The only remaining items are those which have the maximum delay for a given sens.
 *
 * @author Almex
 */
public class FilterTwoSensPerDayProcessor implements
        ItemProcessor<List<BatchExcelRow>, List<BatchExcelRow>>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FilterTwoSensPerDayProcessor.class);

    private String stationA;

    private String stationB;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(stationA, "Station A name is mandatory");
        Validate.notNull(stationB, "Station B name is mandatory");

        LOGGER.info("Processing for stationA={} and stationB={}...", stationA,
                stationB);
    }

    @Override
    public List<BatchExcelRow> process(final List<BatchExcelRow> items) throws Exception {
        List<BatchExcelRow> result = null;

        BatchExcelRow fromAtoB = extractMaxDelay(items, Sens.DEPARTURE);
        BatchExcelRow fromBtoA = extractMaxDelay(items, Sens.ARRIVAL);

        LOGGER.debug("From A to B : {}", fromAtoB);
        LOGGER.debug("From B to A : {}", fromBtoA);

        if (fromAtoB != null || fromBtoA != null) {
            result = new ArrayList<>();

            if (fromAtoB != null) {
                result.add(fromAtoB);
            }

            if (fromBtoA != null) {
                result.add(fromBtoA);
            }
        }


        return result;
    }

    private BatchExcelRow extractMaxDelay(List<BatchExcelRow> items, Sens sens) {
        BatchExcelRow result = null;
        long maxDelay = -1;

        for (BatchExcelRow excelRow : items) {
            if (excelRow.getSens().equals(sens)
                    && excelRow.getDelay() > maxDelay) {
                maxDelay = excelRow.getDelay();
                result = excelRow;
            }
        }

        LOGGER.trace("sens={} maxDelay={}", sens, maxDelay);

        return result;
    }

    public void setStationA(String stationA) {
        this.stationA = stationA;
    }

    public void setStationB(String stationB) {
        this.stationB = stationB;
    }

}
