package be.raildelays.parser

import be.raildelays.domain.Language
import be.raildelays.domain.entities.LineStop
import be.raildelays.httpclient.DefaultStream
import be.raildelays.httpclient.impl.DelaysRequestV2
import be.raildelays.parser.impl.DelaysStreamParserV2
import be.raildelays.util.ParsingUtil
import org.junit.Assert
import org.junit.Test

/**
 * Tests for the {@link be.raildelays.parser.impl.DelaysStreamParserV2} class.
 */
class DelaysStreamParserV2IT {


    @Test
    void testParseDelayFromSample1() {
        LineStop lineStop = new DelaysStreamParserV2().parse(new DefaultStream<DelaysRequestV2>(
                new InputStreamReader(this.getClass().getResourceAsStream("/sample1.json"), "cp1252"),
                new DelaysRequestV2("8444", ParsingUtil.parseDate("08/04/2015"), Language.EN)));


        Assert.assertNotNull("This method should return a result", lineStop);
        Assert.assertEquals("Bruxelles-Midi", lineStop.getStation().getName(Language.EN))
        Assert.assertEquals("Bruxelles-Central", lineStop.getNext().getStation().getName(Language.EN))
    }


    @Test
    void testParseDelayFromSample2() {
        LineStop lineStop = new DelaysStreamParserV2().parse(new DefaultStream<DelaysRequestV2>(
                new InputStreamReader(this.getClass().getResourceAsStream("/sample2.json"), "cp1252"),
                new DelaysRequestV2("7444", ParsingUtil.parseDate("30/04/2015"), Language.EN)));


        Assert.assertNotNull("This method should return a result", lineStop);
        Assert.assertEquals("Liege-Guillemins", lineStop.getNext().getNext().getStation().getName(Language.EN));
        Assert.assertTrue(lineStop.getNext().getNext().isCanceledArrival());
        Assert.assertFalse(lineStop.getNext().getNext().isCanceledDeparture());
    }
}
