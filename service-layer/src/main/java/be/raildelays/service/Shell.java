package be.raildelays.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
			RaildelaysGrabberService service = ctx
					.getBean(RaildelaysGrabberService.class);
			String[] trainIds = new String[] { "466", "467", "468", "514",
					"515", "516", "477", "478", "479", "529", "530", "531" };
			Calendar oneWeekBefore = Calendar.getInstance();
			oneWeekBefore.add(Calendar.DAY_OF_MONTH, -7);

			Iterator<?> iterator = DateUtils.iterator(oneWeekBefore,
					DateUtils.RANGE_WEEK_RELATIVE);

			List<LineStop> stops = new ArrayList<LineStop>();
			
			while (iterator.hasNext()) {
				Calendar calendar = (Calendar) iterator.next();
				
				for (String idTrain : Arrays.asList(trainIds)) {
					service.grabTrainLine(idTrain, calendar.getTime());
					
					Station departure = new Station("Liège-Guillemins");
					Station arrival = new Station("Brussels (Bruxelles)-Central");

					 stops.addAll(service.searchAllDelays(calendar.getTime(),
							departure, arrival, 15));
				}
			}			

			print(stops);

		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
	}

	private static void print(Collection<LineStop> stops) {
		for (LineStop stop : stops) {
			System.out.println(stop.toString());
		}
	}
}
