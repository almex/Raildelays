package be.raildelays.batch;

import be.raildelays.batch.poi.Format;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Almex
 */
public class AbstractFileTest {
    protected static final String CURRENT_PATH = "." + File.separator + "target" + File.separator;
    protected static final String EXCEL_FILE_PREFIX = "retard_sncb";
    protected static final String EXCEL_FILE_NAME = EXCEL_FILE_PREFIX + " 20140522" + Format.OLE2.getFileExtension();
    protected static final String DATE_TO_STRING = "20140121";
    protected static Date DATE;

    static {
        try {
            DATE = new SimpleDateFormat("yyyyMMdd").parse(DATE_TO_STRING);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected void copyFile() throws IOException {
        Path source = new ClassPathResource(EXCEL_FILE_NAME).getFile().toPath();
        Path destination = Paths.get(CURRENT_PATH + EXCEL_FILE_NAME);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    private File[] getExcelFiles() {
        final File directory = new File(CURRENT_PATH);

        File[] result = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
            }
        });

        return result != null ? result : new File[0];
    }

    protected void cleanUp() {
        //-- We remove any result from the test
        for (File file : getExcelFiles()) {
            if (!file.delete()) {
                file.delete();
            }
        }
    }
}
