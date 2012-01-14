package be.raildelays.httpclient.impl

import static be.raildelays.util.ParsingUtil.*

import java.io.Reader
import java.util.Date

import org.apache.log4j.Logger

import be.raildelays.httpclient.RequestStreamer

class RailtimeRequestStreamer extends AbstractRequestStreamBuilder implements RequestStreamer {

	def static log = Logger.getLogger(RailtimeRequestStreamer.class)

	def static final ROOT = 'http://www.railtime.be';

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getTrainList(String stationNameFrom, String stationNameTo, Date day, Integer hour, String language = 'en') {
		// http://www.railtime.be/mobile/HTML/RouteDetail.aspx?snd=Bruxelles-Central&std=215&sna=Li%C3%A8ge-Guillemins&sta=726&da=D&ti=00%3a02&sla=1&rca=21&rcb=0&l=EN&s=1
		return httpGet('/mobile/HTML/RouteDetail.aspx', [snd: stationNameFrom, sna: stationNameTo, sta: 726, da: 'D', ti: formatTime(day), sla: 1, rca: 21, rcb: 0, l: DEFAULT_LANGUAGE, s: 1]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getStationList(String language = 'en') {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getDelays(String idTrain, Date day, String language = 'en') {
		return httpGet('/mobile/HTML/TrainDetail.aspx', [ l:DEFAULT_LANGUAGE, tid: idTrain, dt: formatDate(day) ]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Reader httpGet(path, parameters) {
		httpGet(ROOT, path, parameters)
	}
}
