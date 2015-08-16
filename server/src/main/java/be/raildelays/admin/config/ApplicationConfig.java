package be.raildelays.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;

/**
 * @author Almex
 */
@ImportResource("classpath:spring/batch/raildelays-batch-integration-context.xml")
public class ApplicationConfig {

    @Bean
    PersistenceAnnotationBeanPostProcessor getPersistenceAnnotationBeanPostProcessor() {
        PersistenceAnnotationBeanPostProcessor bean = new PersistenceAnnotationBeanPostProcessor();

        bean.setDefaultPersistenceUnitName("raildelays-repository-jta");

        return bean;
    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setObjectMapper(new HibernateAwareObjectMapper());

        return converter;
    }

}
