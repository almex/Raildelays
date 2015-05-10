package be.raildelays.batch;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class helpful to build all what is related to Excel file location/path/name.
 *
 * @author Almex
 * @since 1.2
 */
public class ExcelFileUtils extends FileUtils {

    public static File getFile(File directory, String prefix, Date date, String extension) throws IOException {
        assert date != null : "You must provide a date!";

        String suffix = new SimpleDateFormat("yyyyMMdd").format(date);

        return getFile(directory, prefix, suffix, extension);
    }


    public static File getFile(File directory, String prefix, String suffix, String extension) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append(prefix);
        builder.append(" ");
        builder.append(suffix);
        builder.append(extension);

        return new File(directory, builder.toString());
    }

    public static String getFileName(File file) {
        String originalFileName = file.getName();
        int extensionIndex = originalFileName.lastIndexOf(".");

        return originalFileName.substring(0, extensionIndex);
    }

    public static String getFileExtension(File file) {
        String originalFileName = file.getName();
        int extensionIndex = originalFileName.lastIndexOf(".");

        return originalFileName.substring(extensionIndex);
    }
}
