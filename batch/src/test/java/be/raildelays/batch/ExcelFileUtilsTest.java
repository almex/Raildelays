package be.raildelays.batch;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Almex
 */
public class ExcelFileUtilsTest {

    private LocalDate previous = null;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetFile() throws Exception {
        File expected = new File("./prefix 20000101.exe");
        File actual = ExcelFileUtils.getFile(new File("./"), "prefix", LocalDate.of(2000, 1, 1), ".exe");

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFile1() throws Exception {
        File expected = new File("./prefix suffix.exe");
        File actual = ExcelFileUtils.getFile(new File("./"), "prefix", "suffix", ".exe");

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFileName() throws Exception {
        String expected = "foo";
        String actual = ExcelFileUtils.getFileName(new File("./foo.bar"));

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFileExtension() throws Exception {
        String expected = ".bar";
        String actual = ExcelFileUtils.getFileExtension(new File("foo.bar"));

        assertEquals(expected, actual);
    }

    @Test
    public void testGenerateListOfDates() throws Exception {
        List<LocalDate> actual = ExcelFileUtils.generateListOfDates();
        actual.forEach(localDate -> {
            if (previous != null) {
                assertTrue(previous.isBefore(localDate));
            }
            previous = localDate;
        });
    }
}