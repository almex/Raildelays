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

package be.raildelays.domain.dto;

import be.raildelays.domain.Language;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class RouteLogDTO implements Serializable {

    private static final long serialVersionUID = -546508375202547836L;

    @NotNull
    private final String trainId;

    @NotNull
    private final Date date;

    @NotNull
    private final Language language;

    @NotNull
    @Size(min = 1)
    private final List<ServedStopDTO> stops;

    public RouteLogDTO(final String trainId, final Date date, final Language language) {
        this.trainId = trainId;
        this.language = language;
        this.date = date;
        stops = new ArrayList<>();
    }

    public void addStop(final ServedStopDTO stop) {
        this.stops.add(stop.clone());
    }

    public String getTrainId() {
        return trainId;
    }

    public Date getDate() {
        return (Date) (date != null ? date.clone() : null);
    }

    public List<ServedStopDTO> getStops() {
        return stops != null ? Collections.unmodifiableList(stops) : new ArrayList<ServedStopDTO>();
    }

    @Override
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        StringBuilder result = new StringBuilder("RouteLogDTO: ") //
                .append("{ ") //
                .append("trainId: " + trainId) //
                .append(", ") //
                .append("date: ") //
                .append(date != null ? df.format(date) : "N/A") //
                .append(", ") //
                .append("language: ") //
                .append(language != null ? language.name() : "N/A") //
                .append(", ") //
                .append("stops: [");

        for (ServedStopDTO stop : getStops()) {
            result.append(stop.toString());
        }

        result.append("]} ");

        return result.toString();
    }

    public Language getLanguage() {
        return language;
    }
}
