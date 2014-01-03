package be.raildelays.admin;

import be.raildelays.admin.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

/**
 * Bootstrap powered by Spring Boot
 *
 * @author Almex
 */
@Configuration
public class SpringBootApplicationInitializer {

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory containerFactory = new TomcatEmbeddedServletContainerFactory(8080);

        //containerFactory.addInitializers(new ServletApplicationInitializer());
        containerFactory.setContextPath("/");

        return containerFactory;
    }

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(new Object[]{SpringBootApplicationInitializer.class, ApplicationConfig.class, RepositoryRestMvcConfiguration.class}, args);

    }

}
