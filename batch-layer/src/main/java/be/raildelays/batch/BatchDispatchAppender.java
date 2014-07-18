package be.raildelays.batch;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.net.Advertiser;

import java.io.Serializable;

/**
 * @author Almex
 */
@Plugin(name = "DispatchFile", category = "Batch", elementType = "appender", printObject = true)
public final class BatchDispatchAppender extends AbstractOutputStreamAppender<RollingFileManager> {

    private AbstractOutputStreamAppender<? extends FileManager> delegate;

    /**
     * Instantiate a WriterAppender and set the output destination to a
     * new {@link java.io.OutputStreamWriter} initialized with <code>os</code>
     * as its {@link java.io.OutputStream}.
     *  @param name             The name of the Appender.
     * @param layout           The layout to format the message.
     * @param filter
     * @param ignoreExceptions
     * @param immediateFlush
     * @param manager          The OutputStreamManager.
     * @param delegate
     */
    protected BatchDispatchAppender(String name,
                                    Layout<? extends Serializable> layout,
                                    Filter filter, boolean ignoreExceptions,
                                    boolean immediateFlush,
                                    RollingFileManager manager,
                                    AbstractOutputStreamAppender<? extends FileManager> delegate) {
        super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
        this.delegate = delegate;
    }
}
