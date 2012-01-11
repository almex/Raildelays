package be.railrelays.parser

import org.junit.Assert
import be.raildelays.util.ParsingUtil
import org.junit.Before
import org.junit.Test

import be.raildelays.domain.railtime.Direction
import be.raildelays.httpclient.RequestStreamer
import be.raildelays.httpclient.impl.RailtimeRequestStreamer
import be.raildelays.parser.StreamParser
import be.raildelays.parser.impl.RailtimeStreamParser

/**
 * Tests for the {@link StreamParser} class.
 */
class StreamParserTest
{
	
	StreamParser parser;
	RequestStreamer streamer;
	Date date; 
	
	@Before 
	void setUp() {
		date = ParsingUtil.parseDate('11/01/2012');		
		streamer = new RailtimeRequestStreamer();		
	} 
	
	@Test
    void testParseDelayFrom466() {
		parser = new RailtimeStreamParser(streamer.getDelays("466", date));
		Object object = parser.parseDelayFrom("466", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}
	
	/*@Test
    void testParseDelayFrom467() {
		parser = new RailtimeStreamParser(streamer.getDelays("467", date));
		Object object = parser.parseDelayFrom("467", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}
	
	@Test
    void testParseDelayFrom468() {
		parser = new RailtimeStreamParser(streamer.getDelays("468", date));
		Object object = parser.parseDelayFrom("468", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}
	
	@Test
    void testParseDelayFrom514() {
		parser = new RailtimeStreamParser(streamer.getDelays("514", date));
		Object object = parser.parseDelayFrom("514", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}
	
	@Test
    void testParseDelayFrom515() {
		parser = new RailtimeStreamParser(streamer.getDelays("515", date));
		Object object = parser.parseDelayFrom("515", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}
	
	@Test
    void testParseDelayFrom477() {
		parser = new RailtimeStreamParser(streamer.getDelays("477", date));
		Object object = parser.parseDelayFrom("477", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}
	
	@Test
    void testParseDelayFrom478() {
		parser = new RailtimeStreamParser(streamer.getDelays("478", date));
		Object object = parser.parseDelayFrom("478", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}
	
	@Test
    void testParseDelayFrom529() {
		parser = new RailtimeStreamParser(streamer.getDelays("529", date));
		Object object = parser.parseDelayFrom("529", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}
	
	@Test
    void testParseDelayFrom530() {
		parser = new RailtimeStreamParser(streamer.getDelays("530", date));
		Object object = parser.parseDelayFrom("530", date);
		Assert.assertNotNull("This method should return a result", object);
		Assert.assertNotNull("This method should return a Direction", object instanceof Direction);
	}*/
}
