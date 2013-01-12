package be.raildelays.repository;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class LoadDatabase {
	
	EmbeddedDatabase db;
	
	public void startUp() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder(new DefaultResourceLoader());
		String tmpDir = System.getProperty("java.io.tmpdir");
	    db = builder.setType(EmbeddedDatabaseType.DERBY).addScript("spring-batch-schema-derby.ddl").build();
	}
	
	public void shutdown() {
		db.shutdown();
	}

}
