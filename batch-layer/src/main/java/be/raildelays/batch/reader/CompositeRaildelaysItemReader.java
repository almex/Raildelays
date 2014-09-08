package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.CompositeItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Composition between {@link DelaysItemReader} and {@link DatabaseDatesItemReader}.
 * <p>
 * This reader is restartable from the last FAILED {@link Chunk}.
 *
 * @author Almex
 */
public class CompositeRaildelaysItemReader extends CompositeItemStream implements ItemReader<List<LineStop>>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompositeRaildelaysItemReader.class);

    private DelaysItemReader delaysItemReader;

    private DatabaseDatesItemReader datesItemReader;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(delaysItemReader, "You must provide a DelaysItemReader");
        Assert.notNull(datesItemReader, "You must provide a DatabaseDatesItemReader");
        register(datesItemReader);
    }

    public List<LineStop> read() throws Exception, UnexpectedInputException,
            ParseException, NonTransientResourceException {
        List<LineStop> result = null; // The end of this reader is when we have no more date
        Date date = datesItemReader.read();

        if (date != null) {
            delaysItemReader.setDate(date);

            // At this point we must return a non null value to continue reading
            result = new ArrayList<>();

            for (LineStop lineStop = delaysItemReader.read(); lineStop != null; lineStop = delaysItemReader.read()) {
                if (lineStop != null) {
                    result.add(lineStop);
                }
            }


            LOGGER.debug("Found {} delays for {}", result.size(), date);
        }

        return result;
    }

    public void setDelaysItemReader(DelaysItemReader delaysItemReader) {
        this.delaysItemReader = delaysItemReader;
    }

    public void setDatesItemReader(DatabaseDatesItemReader datesItemReader) {
        this.datesItemReader = datesItemReader;
    }

}
