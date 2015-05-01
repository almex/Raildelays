package be.raildelays.parser.impl

import be.raildelays.domain.Language
import be.raildelays.domain.entities.LineStop
import be.raildelays.domain.entities.Station
import be.raildelays.domain.entities.TimestampDelay
import be.raildelays.domain.entities.Train
import be.raildelays.httpclient.Stream
import be.raildelays.httpclient.impl.DelaysRequestV2
import be.raildelays.parser.StreamParser
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

/**
 * Stream parser for data coming from HAFAS interface of SNCB/NMBS.
 *
 * @author Almex
 * @since 1.2
 */
class DelaysStreamParserV2 implements StreamParser<LineStop, DelaysRequestV2> {

    def slurper;
    List<List<Map>> json;

    private void init(Reader reader) {
        slurper = new JsonSlurper(type: JsonParserType.CHAR_BUFFER)
        json = slurper.parse(reader)

        assert json instanceof List
    }

    def Logger log = LoggerFactory.getLogger(DelaysStreamParserV2.class)

    @Override
    public LineStop parse(Stream<DelaysRequestV2> stream) {
        LineStop.Builder result = null;

        if (stream.reader != null) {
            init(stream.reader);
            result = map(stream.request);
        }

        return result != null ? result.build() : null;
    }

    private map(request) {
        LineStop.Builder result = null;

        json[0].forEach { object ->
            if (!"".equals(object.csAt) && !"".equals(object.csDt)) {
                LineStop.Builder builder = new LineStop.Builder();

                builder.date(request.day)
                        .train(getTrain(object, request.language))
                        .station(getStation(object, request.language))
                        .departureTime(getDepartureTime(object))
                        .arrivalTime(getArrivalTime(object))
                        .canceledDeparture(isCanceledDeparture(object))
                        .canceledArrival(isCanceledArrival(object));

                if (result == null) {
                    result = builder;
                } else {
                    result.addNext(builder);
                }
            }
        }

        return result;
    }

    private static TimestampDelay getDepartureTime(Map object) {
        final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        TimestampDelay result = null;

        if (object.csDt != null) {
            result = new TimestampDelay(parser.parse(object.csDt), object.dD != null ? object.dD : 0);
        }

        return result;
    }

    private static TimestampDelay getArrivalTime(Map object) {
        final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        TimestampDelay result = null;

        if (object.csAt != null) {
            result = new TimestampDelay(parser.parse(object.csAt), object.dA != null ? object.dA : 0);
        }

        return result;
    }

    private static Station getStation(Map object, Language language) {
        Station result = null;

        if (object.cs != null) {
            result = new Station(object.cs, language);
        }

        return result;
    }

    private static Train getTrain(Map object, Language language) {
        Train result = null;

        if (object.tNr != null) {
            result = new Train(object.tNr, language);
        }

        return result;
    }

    private static boolean isCanceledDeparture(Map object) {
        boolean departure = object.sD != null && object.sD == 1;

        return departure;
    }

    private static boolean isCanceledArrival(Map object) {
        boolean arrival = object.sA != null && object.sA == 1;

        return arrival;
    }

}
