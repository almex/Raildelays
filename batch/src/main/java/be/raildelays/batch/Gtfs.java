package be.raildelays.batch;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;
import org.onebusaway.gtfs.services.HibernateGtfsFactory;

import java.io.File;


public class Gtfs {

    public static final String URL = "c:/nmbs-latest.zip";

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();

        config.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        config.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:data/raildelays");
        config.setProperty("hibernate.connection.username", "sa");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
        config.setProperty("hibernate.hbm2ddl.auto", "update");
        config.addResource("org/onebusaway/gtfs/model/GtfsMapping.hibernate.xml");
        config.addResource("org/onebusaway/gtfs/impl/HibernateGtfsRelationalDaoImpl.hibernate.xml");

        SessionFactory sessionFactory = config.buildSessionFactory();
        HibernateGtfsFactory factory = new HibernateGtfsFactory(sessionFactory);

        GtfsReader reader = new GtfsReader();
        reader.setInputLocation(new File(URL));

        GtfsMutableRelationalDao dao = factory.getDao();
        reader.setEntityStore(dao);
        reader.run();
        reader.close();
    }
}
