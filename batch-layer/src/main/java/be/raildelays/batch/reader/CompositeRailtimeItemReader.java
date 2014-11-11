package be.raildelays.batch.reader;

import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.httpclient.impl.DelaysRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Composition of {@link FlatFileItemReader} and two {@link RailtimeItemReader}.
 * <p>
 * This reader is restartable from the last FAILED {@link Chunk}.
 *
 * @author Almex
 */
public class CompositeRailtimeItemReader extends CompositeItemStream implements ItemReader<TwoDirections>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompositeRailtimeItemReader.class);

    private RailtimeItemReader<Direction, DelaysRequest> departureReader;

    private RailtimeItemReader<Direction, DelaysRequest> arrivalReader;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(arrivalReader, "You must provide a arrivalReader");
        Assert.notNull(departureReader, "You must provide a departureReader");

        LOGGER.debug("Reader initialized with arrivalReader={} and departureReader={}", arrivalReader, departureReader);
    }

    public TwoDirections read() throws Exception {
        TwoDirections result = null;

        Direction departureDirection = departureReader.read();
        Direction arrivalDirection = arrivalReader.read();

        if (departureDirection != null && arrivalDirection != null) {
            result = new TwoDirections(departureDirection, arrivalDirection);
        }

        return result;
    }

    public void setArrivalReader(RailtimeItemReader arrivalReader) {
        this.arrivalReader = arrivalReader;
    }

    public void setDepartureReader(RailtimeItemReader departureReader) {
        this.departureReader = departureReader;
    }

}
