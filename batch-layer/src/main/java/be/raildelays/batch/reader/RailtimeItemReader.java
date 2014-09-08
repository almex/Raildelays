package be.raildelays.batch.reader;

import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.httpclient.RequestStreamer;
import be.raildelays.parser.StreamParser;
import be.raildelays.parser.impl.RailtimeStreamParser;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Reader;
import java.util.Date;
import java.util.Locale;

public class RailtimeItemReader implements ItemReader<Direction>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RailtimeItemReader.class);

    @Resource
    private RequestStreamer streamer;

    @Resource
    private Mapper mapper;

    private String trainId;

    private Date date;

    private Sens sens;

    private String language = Language.EN.name();

    @Override
    public void afterPropertiesSet() throws Exception {
        // Validate all job parameters
        Assert.notNull(date, "You must provide the date parameter to this Reader.");
        Assert.notNull(sens, "You must provide the sens parameter to this Reader.");
        Assert.notNull(language, "You must provide the language parameter to this Reader.");
    }

    public Direction read() throws Exception, UnexpectedInputException,
            ParseException, NonTransientResourceException {
        Direction result = null;

        if (trainId != null && date != null && sens != null && language != null) {

            LOGGER.debug("Requesting Railtime for trainId={} date={} sens={} language={}", new Object[]{trainId, date, sens, language});

            // -- Create a request to target Railtime
            Reader englishStream = streamer.getDelays(trainId, date,
                    Language.valueOf(language.toUpperCase(Locale.US)).getRailtimeParameter(),
                    sens.getRailtimeParameter());

            // -- Parse the content
            StreamParser parser = new RailtimeStreamParser(englishStream);

            waitRandomly();

            result = parser.parseDelay(trainId, date);

            reset();
        }

        return result;
    }

    /**
     * Reset the reader for the next iteration
     */
    private void reset() {
        trainId = null;
    }

    /**
     * Wait a certain period of time before processing. It's more respectful for
     * grabber to do so.
     * <p>
     * Wait between 1 and 5 seconds.
     *
     * @throws InterruptedException
     */
    private void waitRandomly() throws InterruptedException {
        long waitTime = 1000 + Math.round(5000L * Math.random());

        LOGGER.debug("Waiting " + (waitTime / 1000) + " seconds...");

        Thread.sleep(waitTime);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Sens getSens() {
        return sens;
    }

    public void setSens(Sens sens) {
        this.sens = sens;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
