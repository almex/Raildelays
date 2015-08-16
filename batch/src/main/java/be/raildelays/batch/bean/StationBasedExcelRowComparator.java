/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.batch.bean;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;
import java.util.Locale;

/**
 * Compare {@code date} and {@link be.raildelays.domain.entities.Station} name for departure and arrival without taking
 * into account cast and accents. But you must set the {@link be.raildelays.domain.Language} to handle the
 * internationalization.
 *
 * @author Almex
 * @since 1.2
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
            result = -1;
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
