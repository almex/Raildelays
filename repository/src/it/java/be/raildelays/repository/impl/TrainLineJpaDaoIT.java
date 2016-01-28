package be.raildelays.repository.impl;

import be.raildelays.domain.entities.TrainLine;
import be.raildelays.repository.TrainLineDao;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author Almex
 */
public class TrainLineJpaDaoIT extends AbstractIT {

    @Resource
    private TrainLineDao trainLineDao;

    @Test
    public void createTest() {
        TrainLine train = trainLineDao.save(new TrainLine.Builder(466L).build());

        Assert.assertNotNull("The create method should return a result", train);
        Assert.assertNotNull("The persisted station should returned with an id", train.getId());
    }

    @Test
    public void searchTest() {
        Long id = 515L;
        TrainLine expected = trainLineDao.save(new TrainLine.Builder(id).build());
        TrainLine actual = trainLineDao.findByRouteId(id);

        Assert.assertNotNull("The create method should return a result", actual);
        Assert.assertEquals("We should retrieve the one previously created", expected, actual);
    }

}
