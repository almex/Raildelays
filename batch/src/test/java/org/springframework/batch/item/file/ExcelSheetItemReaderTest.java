package org.springframework.batch.item.file;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Almex
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class ExcelSheetItemReaderTest {

    private static final String EXCEL_FILE_EXTENSION = ".xls";
    private ExcelSheetItemReader<BatchExcelRow> reader;

    @Before
    public void setUp() throws Exception {
        reader = new ExcelSheetItemReader<>();
        reader.setResource(new ClassPathResource("retard_sncb 20140522" + EXCEL_FILE_EXTENSION));
        reader.setRowsToSkip(21);
        reader.setSheetIndex(0);
        reader.setMaxItemCount(40);
        reader.setRowMapper(new BatchExcelRowMapper());
        reader.afterPropertiesSet();
        reader.setName("test");
    }

    /*
     * We expect to read the entire and that somewhere in that file we have a train number 515.
     */
    @Test
    public void testRead() throws Exception {
        reader.open(new ExecutionContext());

        List<ExcelRow> result = new ArrayList<>();
        for (ExcelRow row = reader.read(); row != null; row = reader.read()) {
            result.add(row);
        }

        Assert.assertTrue(result.stream().anyMatch(
                row -> "515".equals(row.getEffectiveTrain1().getEnglishName())
        ));

        reader.close();
    }

    /*
     * We expect that if we don't open the stream we get a ReaderNotOpenException.
     */
    @Test(expected = ReaderNotOpenException.class)
    public void testReaderNotOpenException() throws Exception {
        reader.read();
    }

    /*
     * We expect that if the resource does not exist then a read return null.
     */
    @Test
    public void testResourceDoesNotExist() throws Exception {
        reader.setResource(new FileSystemResource("./foo.xls"));
        reader.open(new ExecutionContext());

        Assert.assertNull(reader.read());
    }

    /*
     * We expect that if the resource is not an OLE2 nor an OOXML file then a read return null.
     */
    @Test
    public void testNotValidResource() throws Exception {
        reader.setResource(new ClassPathResource("log4j2.xml"));
        reader.open(new ExecutionContext());

        Assert.assertNull(reader.read());
    }

    /*
     * We expect that if the resource is an OLE2 file and that file is locked then we get an ItemStreamException.
     */
    @Test(expected = ItemStreamException.class)
    @Ignore // This test seems not working on Linux (We cannot lock file on Linux)
    public void testNotReadableResource() throws Exception {
        File directory = new ClassPathResource("log4j2.xml").getFile().getParentFile();
        File file = File.createTempFile("test", ".xls", directory);

        file.deleteOnExit();

        try (FileOutputStream output = new FileOutputStream(file)) {
            // We create a temporary but valid Excel file containing header bytes
            new HSSFWorkbook().write(output);

            reader.setResource(new FileSystemResource(file));

            // Try to deny access to that file
            try (FileLock ignored = output.getChannel().lock()) {
                reader.open(new ExecutionContext());
            }

        }
    }

    /**
     * We expect to get a ParseException if the RowMapper throw a RowMappingException
     */
    @Test(expected = ParseException.class)
    public void testParseException() throws Exception {
        reader.setRowMapper((row, rowIndex) -> {
            throw new RowMappingException("", row);
        });
        reader.open(new ExecutionContext());
        reader.read();
    }

}
