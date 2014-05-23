/*
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.raildelays.batch.logging;

import org.apache.log4j.config.PropertySetter;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.OptionConverter;
import org.apache.logging.log4j.core.pattern.NDCPatternConverter;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * A Log4j {@link Appender} that delegates to a different {@link Appender} for
 * each value of the {@link NDCPatternConverter} that it encounters. Clients inject a delegate
 * appender, a layout and a property name to customize through configuration.
 * Then at runtime they set the NDC value, pushing and then ideally popping in a
 * finally block, and the appender copies the delegate using the layout to
 * customize a named property of the new instance. The logging layout itself is
 * controlled by the injected delegate appender.
 * </p>
 * <p>
 * Note that the Log4j properties file format does not support appender
 * references, so the configuration has to be done in Java, or in XML. E.g.
 * <p/>
 * <pre>
 * 	&lt;appender name="LOGGER" class="org.springframework.sample.service.DispatcherAppender"&gt;
 * 		&lt;param name="propertyName" value="file" /&gt;
 * 		&lt;layout class="org.apache.log4j.PatternLayout"&gt;
 * 			&lt;param name="ConversionPattern" value="target/logs/%x.log" /&gt;
 * 		&lt;/layout&gt;
 * 		&lt;appender-ref ref="FILE" /&gt;
 * 	&lt;/appender&gt;
 *
 * 	&lt;appender name="FILE" class="org.apache.log4j.FileAppender"&gt;
 * 		&lt;param name="File" value="target/logs/default.log" /&gt;
 * 		&lt;layout class="org.apache.log4j.PatternLayout"&gt;
 * 			&lt;param name="ConversionPattern" value="%5p: %m%n" /&gt;
 * 		&lt;/layout&gt;
 * 	&lt;/appender&gt;
 * </pre>
 * <p/>
 * In the example above the layout setting for the individual files is the same
 * as the FileAppender delegate, but the file location is changed using the
 * layout pattern from the {@link DispatcherAppender}. So the result would be a
 * directory <code>target/logs</code> with multiple files (one called
 * "default.log" and others with names set by the NDC).
 * </p>
 * <p/>
 * <p>
 * Subclasses of FileAppender can also be used, e.g. a
 * {@link RollingFileAppender} would be quite common, or other appender types as
 * long as they support customization through a named String valued property.
 * </p>
 *
 * @author Dave Syer
 */
@Plugin(name = "Dispatcher", category = "Core", elementType = "appender", printObject = true)
public class DispatcherAppender extends AbstractOutputStreamAppender {

    private Layout<String> layout;

    private AbstractOutputStreamAppender delegate;

    private ConcurrentMap<String, Appender> delegates = new ConcurrentHashMap<String, Appender>();

    private String propertyName;

    private volatile AppenderCopier copier;

    /**
     * Instantiate a WriterAppender and set the output destination to a
     * new {@link java.io.OutputStreamWriter} initialized with <code>os</code>
     * as its {@link java.io.OutputStream}.
     *
     * @param name             The name of the Appender.
     * @param layout           The layout to format the message.
     * @param filter
     * @param ignoreExceptions
     * @param immediateFlush
     * @param manager          The OutputStreamManager.
     */
    protected DispatcherAppender(String name, Layout layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, OutputStreamManager manager) {
        super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
    }

    @PluginFactory
    public static DispatcherAppender createAppender(@PluginAttribute("propertyName") String propertyName,
                                              @PluginAttribute("ignoreExceptions") String ignore,
                                              @PluginAttribute("immediateFlush") String immediate,
                                              @PluginElement("delegate") AbstractOutputStreamAppender delegate,
                                              @PluginElement("Layout") Layout layout,
                                              @PluginElement("Filters") Filter filter) {
        boolean ignoreExceptions = Boolean.parseBoolean(ignore);
        boolean immediateFlush = Boolean.parseBoolean(immediate);

        OutputStreamManager manager = delegate.getManager();
        if (manager == null) {
            return null;
        }

        if (layout == null) {
            throw new IllegalStateException("This appender requires a layout");
        }
        if (propertyName == null) {
            throw new IllegalStateException("This appender requires a propertyName (e.g. 'file')");
        }
        if (delegate == null) {
            throw new IllegalStateException(
                    "This appender requires a delegate appender (e.g. use <appender-ref/> in XML configuration)");
        }

        return new DispatcherAppender(propertyName, layout, filter, ignoreExceptions, immediateFlush, manager);
    }

    /**
     * The name of the property in the delegate that will be overridden to
     * create a new Appender using the layout. E.g. use "file" for
     * {@link FileAppender} and subclasses. This property is mandatory with no
     * default.
     *
     * @param propertyName the property name to set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void addAppender(AbstractOutputStreamAppender newAppender) {
        if (delegate != null) {
            throw new IllegalStateException("A delegate was already set. "
                    + "Only one <appender-ref/> is allowed in the configuration.");
        }
        delegate = newAppender;
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getAllAppenders() {
        List<Appender> list = delegate == null ? Collections.<Appender>emptyList() : Arrays
                .<Appender>asList(delegate);
        return new Vector<Appender>(list).elements();
    }

    public Appender getAppender(String name) {
        return delegate;
    }

    public boolean isAttached(Appender appender) {
        return appender == delegate;
    }

    public void removeAllAppenders() {
        delegate = null;
    }

    public void removeAppender(Appender appender) {
        if (appender == delegate) {
            delegate = null;
        }
    }

    public void removeAppender(String name) {
        if (name.equals(delegate.getName())) {
            delegate = null;
        }
    }

    @Override
    public void append(LogEvent event) {
        String key = event.getFQCN();
        Appender appender = delegate;
        if (key != null) {
            if (!delegates.containsKey(key)) {
                if (copier == null) {
                    copier = new AppenderCopier(delegate, OptionConverter.substVars(propertyName, null));
                }
                appender = copier.create(OptionConverter.substVars(layout.toSerializable(event), null));
                delegates.putIfAbsent(key, appender);
            } else {
                appender = delegates.get(key);
            }
        }
        appender.append(event);
    }

    /**
     * Assert that mandatory properties are set. This is called by the Log4j
     * configurator if using XML configuration.
     *
     * @see Appender#start()
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * Convenience class encapsulating the creation, copying and override of a
     * single named property in an Appender.
     */
    private static class AppenderCopier {

        private final String key;

        private final AbstractOutputStreamAppender input;

        private boolean overridable;

        public AppenderCopier(AbstractOutputStreamAppender input, String key) {
            this.input = input;
            this.key = key;
            this.overridable = verifyProperty(input, key);
        }

        private boolean verifyProperty(Object bean, String name) {
            try {
                BeanInfo bi = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] props = bi.getPropertyDescriptors();
                for (int i = 0; i < props.length; i++) {
                    if (name.equals(props[i].getName())) {
                        return true;
                    }
                }
            } catch (IntrospectionException ex) {
                LOGGER.error("Failed to introspect " + bean + ": " + ex.getMessage());
            }
            LOGGER.error("No property named '" + name + "' was found on bean of type " + bean.getClass()
                    + " (all logs will go to the default appender)");
            return false;
        }

        /**
         * Copies the input Appender, creating a new instance from the default
         * constructor, and transferring all the supported (primitive)
         * properties, plus the layout (if there is one). Special treatment is
         * given to a property with name equal to the key, which is overridden
         * by the injected value instead of copied from the input Appender.
         *
         * @return a new Appender instance
         */
        public Appender create(final String override) {
            Appender output = null;

            if (!this.overridable) {
                output = input;
            } else {
                output = new DispatcherAppender(input.getName(), input.getLayout(), input.getFilter(), input.ignoreExceptions(), false, input.getManager());
            }

            return output;
        }

    }

}
