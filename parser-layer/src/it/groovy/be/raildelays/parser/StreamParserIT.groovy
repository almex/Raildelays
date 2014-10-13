package be.raildelays.parser

import be.raildelays.domain.Language
import be.raildelays.domain.Sens
import be.raildelays.domain.railtime.Direction
import be.raildelays.domain.railtime.Step
import be.raildelays.httpclient.DefaultStream
import be.raildelays.httpclient.impl.DelaysRequest
import be.raildelays.httpclient.impl.DelaysRequestStreamer
import be.raildelays.parser.impl.DelaysStreamParser
import be.raildelays.util.ParsingUtil
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Tests for the {@link StreamParser} class.
 */
class StreamParserIT {

    def Logger log = LoggerFactory.getLogger(StreamParserIT.class)

    /**
     * Test train 466 today.
     */
    @Test
    @Ignore
    void testParseDelayFrom466() {
        Object object = new DelaysStreamParser()
                .parse(new DelaysRequestStreamer()
                .stream(new DelaysRequest('466', new Date(), , Sens.DEPARTURE, Language.EN)));
        Assert.assertNotNull("This method should return a result", object);
        Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
    }

    @Test
    void testParseDelayFromSample1() {
        Object object = new DelaysStreamParser().parse(new DefaultStream<DelaysRequest>(
                new InputStreamReader(this.getClass().getResourceAsStream("/Sample1.html"), "UTF-8"),
                new DelaysRequest("477", ParsingUtil.parseDate("13/01/2012"), Sens.DEPARTURE, Language.EN)));
        Assert.assertNotNull("This method should return a result", object);
        Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
        Direction direction = (Direction) object;
        Step[] steps = (Step[]) direction.getSteps().toArray();
        Assert.assertEquals("Step1 should from station", "Gouvy", steps[0].getStation().getName());
        Assert.assertEquals("Step12 should have a delay of", 0, steps[0].getDelay());
        Assert.assertEquals("Step12 should have timestamp of", ParsingUtil.parseTimestamp("13/01/201205:07"), steps[0].getTimestamp());
        Assert.assertEquals("Step12 should from station", "Liège-Guillemins", steps[12].getStation().getName());
        Assert.assertEquals("Step12 should have a delay of", 0, steps[12].getDelay());
        Assert.assertEquals("Step12 should have timestamp of", ParsingUtil.parseTimestamp("13/01/201206:34"), steps[12].getTimestamp());
        Assert.assertTrue("Step15 should be canceled", steps[13].isCanceled());
        Assert.assertTrue("Step15 should be canceled", steps[14].isCanceled());
        Assert.assertTrue("Step15 should be canceled", steps[15].isCanceled());
        Assert.assertTrue("Step15 should be canceled", steps[16].isCanceled());

    }

    @Test
    void testParseDelayFromSample2() {
        Object object = new DelaysStreamParser().parse(new DefaultStream<DelaysRequest>(
                new InputStreamReader(this.getClass().getResourceAsStream("/Sample2.html"), "UTF-8"),
                new DelaysRequest("466", ParsingUtil.parseDate("11/01/2012"), Sens.DEPARTURE, Language.EN)));
        Assert.assertNotNull("This method should return a result", object);
        Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
        Direction direction = (Direction) object;
        Step[] steps = (Step[]) direction.getSteps().toArray();
        Assert.assertEquals("Step1 should from station", "Brussels (Bruxelles)-Midi", steps[0].getStation().getName());
        Assert.assertEquals("Step12 should have a delay of", 19, steps[0].getDelay());
        Assert.assertEquals("Step12 should have timestamp of", ParsingUtil.parseTimestamp("11/01/201216:24"), steps[0].getTimestamp());
        Assert.assertEquals("Step12 should from station", "Brussels (Bruxelles)-Central", steps[1].getStation().getName());
        Assert.assertEquals("Step12 should have a delay of", 22, steps[1].getDelay());
        Assert.assertEquals("Step12 should have timestamp of", ParsingUtil.parseTimestamp("11/01/201216:28"), steps[1].getTimestamp());
        Assert.assertEquals("Step12 should from station", "Brussels (Bruxelles)-Nord", steps[2].getStation().getName());
        Assert.assertEquals("Step12 should have a delay of", 22, steps[2].getDelay());
        Assert.assertEquals("Step12 should have timestamp of", ParsingUtil.parseTimestamp("11/01/201216:33"), steps[2].getTimestamp());
        Assert.assertTrue("Step15 should be canceled", steps[4].isCanceled());
        Assert.assertTrue("Step15 should be canceled", steps[5].isCanceled());
    }

    @Test
    void testParseDelayFrom1hDelayDeparture() {
        Object object = new DelaysStreamParser().parse(new DefaultStream<DelaysRequest>(
                new InputStreamReader(this.getClass().getResourceAsStream("/1h delay departure.html"), "UTF-8"),
                new DelaysRequest("516", ParsingUtil.parseDate("12/11/2013"), Sens.DEPARTURE, Language.EN)));

        Direction direction = (Direction) object;
        Step[] steps = (Step[]) direction.getSteps().toArray();

        Assert.assertEquals("Liège-Guillemins", steps[7].getStation().getName());
        Assert.assertEquals(64L, steps[7].getDelay());
    }

    @Test
    void testParseDelayFrom1hDelayArrival() {
        Object object = new DelaysStreamParser().parse(new DefaultStream<DelaysRequest>(
                new InputStreamReader(this.getClass().getResourceAsStream("/1h delay arrival.html"), "UTF-8"),
                new DelaysRequest("516", ParsingUtil.parseDate("12/11/2013"), Sens.DEPARTURE, Language.EN)));

        Direction direction = (Direction) object;
        Step[] steps = (Step[]) direction.getSteps().toArray();

        Assert.assertEquals("Liège-Guillemins", steps[7].getStation().getName());
        Assert.assertEquals(64L, steps[7].getDelay());
    }
}
