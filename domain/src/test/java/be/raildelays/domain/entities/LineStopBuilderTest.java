package be.raildelays.domain.entities;

import be.raildelays.delays.TimeDelay;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(BlockJUnit4ClassRunner.class)
public class LineStopBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void testBuildValidation() {
        new LineStop.Builder().build();
    }

    @Test
    public void testMostSimpleBuild() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new TrainLine())
                .station(new Station())
                .date(LocalDate.now());

        Assert.assertNotNull(builder.build());
    }

    @Test
    public void testToBuildAllValues() {
        final LineStop.Builder builder = new LineStop.Builder()
                .id(1L)
                .train(new TrainLine())
                .station(new Station())
                .date(LocalDate.now())
                .arrivalTime(TimeDelay.now())
                .departureTime(TimeDelay.now())
                .canceledArrival(true)
                .canceledDeparture(true);

        Assert.assertNotNull(builder.build());
    }

    @Test
    public void testTwoSubsequentBuilds() {
        final LocalDate firstDate = LocalDate.ofEpochDay(0);
        final LocalDate secondDate = LocalDate.ofEpochDay(5000);
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new TrainLine())
                .station(new Station())
                .date(firstDate);

        Assert.assertEquals(firstDate, builder.build().getDate());

        builder.date(secondDate);

        Assert.assertEquals(secondDate, builder.build().getDate());
    }

    @Test
    public void testAddNextBuilder() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new TrainLine())
                .station(new Station())
                .date(LocalDate.now())
                .addNext(new LineStop.Builder()
                        .train(new TrainLine())
                        .station(new Station())
                        .date(LocalDate.now()))
                .addNext(new LineStop.Builder()
                        .train(new TrainLine())
                        .station(new Station())
                        .date(LocalDate.now()));

        Assert.assertNotNull(builder.build().getNext());
        Assert.assertNotNull(builder.build().getNext().getNext());

        LineStop lineStop = builder.build();
        Assert.assertEquals(lineStop, lineStop.getNext().getPrevious());
        Assert.assertEquals(lineStop, lineStop.getNext().getNext().getPrevious().getPrevious());
    }

    @Test
    public void testAddNextLineStop() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new TrainLine())
                .station(new Station())
                .date(LocalDate.now())
                .addNext(new LineStop.Builder()
                        .train(new TrainLine())
                        .station(new Station())
                        .date(LocalDate.now())
                        .build())
                .addNext(new LineStop.Builder()
                        .train(new TrainLine())
                        .station(new Station())
                        .date(LocalDate.now())
                        .build());

        Assert.assertNotNull(builder.build().getNext());
        Assert.assertNotNull(builder.build().getNext().getNext());
    }

    @Test
    public void testAddPreviousBuilder() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new TrainLine())
                .station(new Station())
                .date(LocalDate.now())
                .addPrevious(new LineStop.Builder()
                        .train(new TrainLine())
                        .station(new Station())
                        .date(LocalDate.now()))
                .addPrevious(new LineStop.Builder()
                        .train(new TrainLine())
                        .station(new Station())
                        .date(LocalDate.now()));

        Assert.assertNotNull(builder.build().getPrevious());
        Assert.assertNotNull(builder.build().getPrevious().getPrevious());
    }

    @Test
    public void testAddPreviousLineStop() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new TrainLine())
                .station(new Station())
                .date(LocalDate.now())
                .addPrevious(new LineStop.Builder()
                        .train(new TrainLine())
                        .station(new Station())
                        .date(LocalDate.now())
                        .build())
                .addPrevious(new LineStop.Builder()
                        .train(new TrainLine())
                        .station(new Station())
                        .date(LocalDate.now())
                        .build());

        Assert.assertNotNull(builder.build().getPrevious());
        Assert.assertNotNull(builder.build().getPrevious().getPrevious());
    }
}
