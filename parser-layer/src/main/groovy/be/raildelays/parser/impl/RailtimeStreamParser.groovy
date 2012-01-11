package be.raildelays.parser.impl

import java.io.Reader

import org.apache.log4j.Logger

import be.raildelays.domain.railtime.Direction
import be.raildelays.domain.railtime.Step
import be.raildelays.domain.railtime.Train
import be.raildelays.parser.StreamParser
import be.raildelays.util.ParsingUtil

class RailtimeStreamParser implements StreamParser {
	
	private Reader reader;
	def tagsoupParser;
	def slurper;
	def html;
	
	public RailtimeStreamParser(reader) {
		this.reader = reader;
		init();
	}
	
	private void init() {
		tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
		slurper = new XmlSlurper(tagsoupParser)
		html = slurper.parse(reader)
	}
	
	Logger log = Logger.getLogger(RailtimeStreamParser.class)
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Direction parseDelayFrom(String idTrain, Date date) {
		def steps = [] as List
	
		def body = html.body.'**'
		Direction direction = new Direction(new Train(idTrain))
		
		def title = body.find { it.name() == 'h1' }.text()
		log.debug("title="+title)
		direction.setLibelle(title);
		
		
		// Parse the page
		body.findAll { it.name() == 'tr' && it.@class.text().contains('rowHeightTraject') }.each { tr ->
			String station = tr.td[1].text()
			String hour = tr.td[2].text()
			String delay = tr.td[3].text()
			
			Step step = new Step(station, ParsingUtil.parseTimestamp(ParsingUtil.formatDate(date)+hour), null, parseDelay(delay))
	
			if(step.delay > 15) {
				log.debug("station="+step.station.name)
				log.debug("hour="+step.from)
				log.debug("delay="+step.delay)
			}
			steps.add(step)
		}
		direction.steps = steps
		
		return direction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Direction parseDelayTo(String idTrain, Date date) {
		throw new UnsupportedOperationException();
	}
	
	def Integer stringToInteger(String value) {
		String target = value.trim()
		if (target.isInteger())
			return Integer.parseInt(target)
	}

	def String extractDelay(String value) {
		return value.toString().replaceAll("'", "")
	}

	def parseDelay(String value) {
		Integer delay = stringToInteger(extractDelay(value))	
		return delay != null ? delay : 0
	}
}
