package be.raildelays.domain.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.experimental.theories.DataPoint;

import be.raildelays.test.AbstractObjectTest;

public class StationTest extends AbstractObjectTest  {

	@DataPoint
	public static Station DATA_POINT1;
	
	@DataPoint
	public static Station DATA_POINT2;
	
	@DataPoint
	public static Station DATA_POINT3;
	
	@DataPoint
	public static Station DATA_POINT4;
    
	@DataPoint
	public static Station DATA_POINT5;
    
	@DataPoint
	public static Station DATA_POINT6;

	@Override
	@Before
	public void setUp() throws ParseException {		
		
		DATA_POINT1 = new Station("Liège (Liège-Guillemins)", "Luik (Luik-Guillemins)", "Liège (Liège-Guillemins)");
		
		DATA_POINT2 = DATA_POINT1;
		
		DATA_POINT3 = new Station("Brussels (Bruxelles-central)", "Brussels (Brussel-Centraal)", "Bruxelles (Bruxelles-central)");
		
		DATA_POINT4 = new Station("", "", "");		
        
		DATA_POINT5 = new Station("Brussels (Bruxelles-central)", "", "");              
        
		DATA_POINT6 = new Station("Brussels (Bruxelles-central)", null, null);
	}

}
