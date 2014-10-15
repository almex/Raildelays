package be.raildelays.batch.bean;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;
import java.util.Locale;

/**
 * @author Almex
 */
public class StationBasedExcelRowComparator implements Comparator<ExcelRow> {

    private Language language;

    public StationBasedExcelRowComparator(Language language) {
        this.language = language;
    }

    @Override
    public int compare(ExcelRow lho, ExcelRow rho) {
        int result;

        if (lho == rho) {
            result = 0;
        } else if (lho == null) {
            result = (rho == null) ? 0 : -1;
        } else if (rho == null) {
            result = 1;
        } else {
            result = new CompareToBuilder()
                    .append(lho.getDate(), rho.getDate())
                    .append(getStationName(lho.getDepartureStation(), language), getStationName(rho.getDepartureStation(), language))
                    .append(getStationName(lho.getArrivalStation(), language), getStationName(rho.getArrivalStation(), language))
                    .toComparison();
        }

        return result;
    }


    private static String getStationName(Station station, Language lang) {
        String result = null;

        if (station != null) {
            String stationName = station.getName(lang);

            if (StringUtils.isNotBlank(stationName)) {
                result = StringUtils.stripAccents(stationName.toUpperCase(Locale.UK));
            }
        }

        return result;
    }
}
