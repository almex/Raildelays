package be.raildelays.repository.impl;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/raildelays-dao.xml")
@Transactional
public class LineStopJpaDaoIT {
	
	@Resource
	private LineStopJpaDao lineStopDao;
	
	@Test
	public void createTest() {
		lineStopDao.createLineStop(new LineStop(new Train("466"), new Station("Liège-Guillemins"), new TimestampDelay(), new TimestampDelay()));
	}

	
}
