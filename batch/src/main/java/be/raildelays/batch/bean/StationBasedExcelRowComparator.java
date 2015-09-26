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

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Function;

import static java.util.Comparator.*;

/**
 * Compare {@code date} and {@link be.raildelays.domain.entities.Station} name for departure and arrival without taking
 * into account cast and accents. But you must set the {@link be.raildelays.domain.Language} to handle the
 * internationalization.
 *
 * @author Almex
 * @since 1.2
 */
public class StationBasedExcelRowComparator extends AbstractExcelRowComparator<ExcelRow> {

    private Language language;

    public StationBasedExcelRowComparator(Language language) {
        this.language = language;
    }

    @Override
    public int compare(ExcelRow lho, ExcelRow rho) {
        return nullsFirst(compareReferences(
                comparing(ExcelRow::getDate, nullsFirst(naturalOrder()))
                        .thenComparing(getStationName(ExcelRow::getDepartureStation), nullsFirst(naturalOrder()))
                        .thenComparing(getStationName(ExcelRow::getArrivalStation), nullsFirst(naturalOrder()))
        )).compare(lho, rho);
    }

    protected Function<ExcelRow, String> getStationName(Function<ExcelRow, Station> keyExtractor) {
        return excelRow -> {
            Station station = keyExtractor.apply(excelRow);
            String result = null;

            if (station != null) {
                String stationName = station.getName(language);

                if (!"".equals(stationName)) {
                    result = Normalizer
                            .normalize(stationName, Normalizer.Form.NFD)
                            .replaceAll("[^\\p{ASCII}]", "")
                            .toUpperCase(Locale.ENGLISH);
                }
            }

            return result;
        };
    }
}
