package be.raildelays.javafx.spring;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Preloader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * @author Almex
 * @since 1.2
 */
public class CountBeanPostProcessor implements BeanPostProcessor {

    private double counter = 0;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {

        LauncherImpl.notifyPreloader(null,
                new Preloader.ProgressNotification(counter++ / applicationContext.getBeanDefinitionCount()));

        return o;
    }
}
