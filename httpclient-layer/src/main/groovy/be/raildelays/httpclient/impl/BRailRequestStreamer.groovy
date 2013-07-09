package be.raildelays.httpclient.impl

import static be.raildelays.util.ParsingUtil.*

import java.io.Reader
import java.util.Date

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import be.raildelays.httpclient.RequestStreamer

class BRailRequestStreamer extends AbstractRequestStreamBuilder implements RequestStreamer {

	def static Logger log = LoggerFactory.getLogger(BRailRequestStreamer.class)

	def static final DEFAULT_ROOT = 'http://hari.b-rail.be';

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getTrainList(String stationNameFrom, String stationNameTo, Date day, Integer hour, String language = '1') {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getStationList(String language = '1') {
		return httpGet('/infsta/StationList.ashx', [lang: language]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getDelays(String idTrain, Date day, String language = '1', String sens = 'A') {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Reader httpGet(path, parameters) {
		httpGet(ROOT, path, parameters)
	}
}
