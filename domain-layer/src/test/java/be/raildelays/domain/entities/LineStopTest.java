package be.raildelays.domain.entities;

import java.util.Date;

import org.junit.Before;
import org.junit.experimental.theories.DataPoint;

import be.raildelays.test.AbstractObjectTest;

public class LineStopTest extends AbstractObjectTest  {

	@DataPoint
	public static LineStop DATA_POINT1;
	
	@DataPoint
	public static LineStop DATA_POINT2;
	
	@DataPoint
	public static LineStop DATA_POINT3;
	
	@DataPoint
	public static LineStop DATA_POINT4;

	@Override
	@Before
	public void setUp() {
		Train train = new Train("466");
		Station station = new Station("Liège (Liège-Guillemins)");
		Date date = new Date();
		
		DATA_POINT1 = new LineStop(date, train, station);
		
		DATA_POINT2 = DATA_POINT1;
		
		DATA_POINT2 = new LineStop(date, train, station);
		
		DATA_POINT3 = new LineStop(new Date(), new Train("469"), new Station("Brussels (Bruxelles-central)"));		
	}

}
