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

class DelaysStreamParserV2 implements StreamParser<LineStop, DelaysRequestV2> {

    def slurper;
    List<List<Map>> json;

    private void init(Reader reader) {
        slurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)
        json = slurper.parse(reader)
        assert json instanceof List
    }

    def Logger log = LoggerFactory.getLogger(DelaysStreamParserV2.class)

    @Override
    public LineStop parse(Stream<DelaysRequestV2> stream) {
        LineStop.Builder result = null;
        init(stream.reader)

        json[0].forEach { object ->
            if (!"".equals(object.csAt) && !"".equals(object.csDt)) {
                LineStop.Builder builder = new LineStop.Builder();

                builder.date(stream.request.day)
                        .train(getTrain(object))
                        .station(getStation(object, stream.request.language))
                        .departureTime(getTimestampDelay(object))
                        .arrivalTime(getTimestampDelay(object))
                        .canceled(false);

                if (result == null) {
                    result = builder;
                } else {
                    result.addNext(builder);
                }
            }
        }

        return result.build();
    }

    private static TimestampDelay getTimestampDelay(Map object) {
        final SimpleDateFormat parser = new SimpleDateFormat("hh:mm");
        TimestampDelay result = null;

        if (object.csDt != null) {
            result = new TimestampDelay(parser.parse(object.csDt), object.dD);
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

    private static Train getTrain(Map object) {
        Train result = null;

        if (object.tNr != null) {
            result = new Train(object.tNr);
        }

        return result;
    }

}
