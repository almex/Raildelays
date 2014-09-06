package be.raildelays.domain.entities;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class LineStopBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void testBuildValidation() {
        new LineStop.Builder().build();
    }

    @Test
    public void testMostSimpleBuild() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new Train())
                .station(new Station())
                .date(new Date());

        Assert.assertNotNull(builder.build());
    }

    @Test
    public void testToBuildAllValues() {
        final LineStop.Builder builder = new LineStop.Builder()
                .id(1L)
                .train(new Train())
                .station(new Station())
                .date(new Date())
                .arrivalTime(new TimestampDelay())
                .departureTime(new TimestampDelay())
                .canceled(true);

        Assert.assertNotNull(builder.build());
    }

    @Test
    public void testTwoSubsequentBuilds() {
        final Date firstDate = new Date(0);
        final Date secondDate = new Date(5000);
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new Train())
                .station(new Station())
                .date(firstDate);

        Assert.assertEquals(firstDate, builder.build().getDate());

        builder.date(secondDate);

        Assert.assertEquals(secondDate, builder.build().getDate());
    }

    @Test
    public void testAddNextBuilder() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new Train())
                .station(new Station())
                .date(new Date())
                .addNext(new LineStop.Builder()
                        .train(new Train())
                        .station(new Station())
                        .date(new Date()))
                .addNext(new LineStop.Builder()
                        .train(new Train())
                        .station(new Station())
                        .date(new Date()));

        Assert.assertNotNull(builder.build().getNext());
        Assert.assertNotNull(builder.build().getNext().getNext());
    }

    @Test
    public void testAddNextLineStop() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new Train())
                .station(new Station())
                .date(new Date())
                .addNext(new LineStop.Builder()
                        .train(new Train())
                        .station(new Station())
                        .date(new Date())
                        .build())
                .addNext(new LineStop.Builder()
                        .train(new Train())
                        .station(new Station())
                        .date(new Date())
                        .build());

        Assert.assertNotNull(builder.build().getNext());
        Assert.assertNotNull(builder.build().getNext().getNext());
    }

    @Test
    public void testAddPreviousBuilder() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new Train())
                .station(new Station())
                .date(new Date())
                .addPrevious(new LineStop.Builder()
                        .train(new Train())
                        .station(new Station())
                        .date(new Date()))
                .addPrevious(new LineStop.Builder()
                        .train(new Train())
                        .station(new Station())
                        .date(new Date()));

        Assert.assertNotNull(builder.build().getPrevious());
        Assert.assertNotNull(builder.build().getPrevious().getPrevious() );
    }

    @Test
    public void testAddPreviousLineStop() {
        final LineStop.Builder builder = new LineStop.Builder()
                .train(new Train())
                .station(new Station())
                .date(new Date())
                .addPrevious(new LineStop.Builder()
                        .train(new Train())
                        .station(new Station())
                        .date(new Date())
                        .build())
                .addPrevious(new LineStop.Builder()
                        .train(new Train())
                        .station(new Station())
                        .date(new Date())
                        .build());

        Assert.assertNotNull(builder.build().getPrevious());
        Assert.assertNotNull(builder.build().getPrevious().getPrevious() );
    }
}
