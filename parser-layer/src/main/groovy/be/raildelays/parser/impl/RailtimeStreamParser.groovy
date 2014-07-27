package be.raildelays.parser.impl

import be.raildelays.domain.railtime.Direction
import be.raildelays.domain.railtime.Step
import be.raildelays.domain.railtime.Train
import be.raildelays.parser.StreamParser
import be.raildelays.util.ParsingUtil
import org.ccil.cowan.tagsoup.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RailtimeStreamParser implements StreamParser {


    public static final String TIMESTAMP_FORMAT = "dd/MM/yyyy hh:mm";

    private Reader reader;
    def tagsoupParser;
    def slurper;
    def html;


    public RailtimeStreamParser(reader) {
        this.reader = reader;
        init();
    }

    private void init() {
        tagsoupParser = new Parser()
        slurper = new XmlSlurper(tagsoupParser)
        html = slurper.parse(reader)
    }

    def Logger log = LoggerFactory.getLogger(RailtimeStreamParser.class)

    /**
     * {@inheritDoc}
     */
    @Override
    public Direction parseDelay(String idTrain, Date date) {
        def steps = [] as List

        def body = html.body.'**'
        Direction direction = new Direction(new Train(idTrain))

        def title = body.find { it.name() == 'h1' }.text()
        log.debug("title=" + title)
        direction.setLibelle(title);

        // Parse the page
        int ordinance = 0;
        body.findAll { it.name() == 'tr' && it.@class.text().contains('rowHeightTraject') }.each { tr ->
            String station = tr.td[1].text()
            String hour = tr.td[2].text()
            String delay = tr.td[3].text()
            //boolean canceled = tr.td[1].a.@class.text().contains("red")
            boolean canceled = tr.td[0].@class.text().contains("Deleted")

            log.debug("station=" + station)
            log.debug("hour=" + hour)
            log.debug("delay=" + delay)
            log.debug("canceled=" + canceled)

            Step step = new Step(ordinance, station, ParsingUtil.parseTimestamp(ParsingUtil.formatDate(date) + hour), parseDelay(delay), canceled)


            steps.add(step)
            ordinance++
        }
        direction.steps = steps

        return direction;
    }

    def static Long stringToLong(String value) {
        String target = value.trim()
        if (target.isInteger())
            return Long.parseLong(target)
    }

    def static String extractDelay(String value) {
        return value.toString().replaceAll("[^0-9:]", "")
    }

    def parseDelay(String value) {
        Long delay = stringToLong(extractDelay(value))

        if (delay == null) {
            List<String> tokens = value.tokenize(':');

            if (tokens.size() == 2) {
                delay = stringToLong(extractDelay(tokens.get(1)));
                delay += stringToLong(extractDelay(tokens.get(0))) * 60;
            }
        }

        return delay != null ? delay : 0
    }
}
