package be.raildelays.service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;

public class Shell {

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"/spring/service/raildelays-service-integration-context.xml");

		try {
			RaildelaysService service = ctx
					.getBean(RaildelaysService.class);
			String[] trainIds = new String[] { "466", "467", "468", "514",
					"515", "516", "477", "478", "479", "529", "530", "531" };
			Calendar oneWeekBefore = Calendar.getInstance();
			oneWeekBefore.add(Calendar.DAY_OF_MONTH, -7);

			Iterator<?> iterator = DateUtils.iterator(oneWeekBefore,
					DateUtils.RANGE_WEEK_RELATIVE);

			
			while (iterator.hasNext()) {
				Calendar calendar = (Calendar) iterator.next();
				Set<LineStop> stops = new HashSet<LineStop>();
				Station sationA = new Station("Liège-Guillemins");
				Station stationB = new Station("Brussels (Bruxelles)-Central");
				
				for (String idTrain : Arrays.asList(trainIds)) {					
					//service.grabTrainLine(idTrain, calendar.getTime());	
				}				

				stops.addAll(service.searchDelaysBetween(calendar.getTime(),
						sationA, stationB, 15));

				print(stops, sationA, stationB);
			}			

		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
	}

	private static void print(Collection<LineStop> stops, Station departure, Station arrival) {
		for (LineStop stop : stops) {			
//			printFromDeparture(rewind(stop), departure, arrival);
			System.out.println(stop.toStringAll());
			System.out.println("=================================");
		}
	}
	
	private static void printFromDeparture(LineStop stop, Station departure, Station arrival) {
		if (stop.getStation().equals(departure)) {
			System.out.print(stop.toString() + " - ");
		} else if (stop.getStation().equals(arrival)) {
			System.out.println(stop.toString());
		} else if (stop.getNext() != null) {
			printFromDeparture(stop.getNext(), departure, arrival);
		}
	}
	
	private static LineStop rewind(LineStop stop) {
		if (stop.getPrevious() != null) {
			rewind(stop.getPrevious());
		}

		return stop;
	}
	
	
	
}
