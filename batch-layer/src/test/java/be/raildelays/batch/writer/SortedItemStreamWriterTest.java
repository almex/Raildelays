package be.raildelays.batch.writer;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.batch.reader.ExcelSheetItemReader;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RunWith(BlockJUnit4ClassRunner.class)
public class SortedItemStreamWriterTest extends AbstractFileTest {

    public static final String EXCEL_FILE_DESTINATION_PATH = "." + File.separator + "target" + File.separator + EXCEL_FILE_NAME;

    private SortedItemStreamWriter<BatchExcelRow> sortedItemStreamWriter;

    private List<BatchExcelRow> items = new ArrayList<>();

    private ExecutionContext executionContext;

    @Before
    public void setUp() throws Exception {
        ExcelSheetItemWriter<BatchExcelRow> writer = new ExcelSheetItemWriter<>();
        ExcelSheetItemReader<BatchExcelRow> reader = new ExcelSheetItemReader<>();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        copyFile();

        writer.setName("test");
        writer.setSheetIndex(0);
        writer.setRowsToSkip(21);
        writer.setMaxItemCount(40);
        writer.setRowAggregator(new BatchExcelRowAggregator());
        writer.setTemplate(new ClassPathResource("template.xls"));
        writer.setResource(new FileSystemResource(CURRENT_PATH));
        writer.afterPropertiesSet();

        reader.setName("test");
        reader.setSheetIndex(0);
        reader.setRowsToSkip(21);
        reader.setMaxItemCount(40);
        reader.setRowMapper(new BatchExcelRowMapper());
        reader.setResource(new FileSystemResource(CURRENT_PATH));
        reader.afterPropertiesSet();

        sortedItemStreamWriter = new SortedItemStreamWriter<>();
        executionContext = MetaDataInstanceFactory.createStepExecution().getExecutionContext();
        sortedItemStreamWriter.setResource(new FileSystemResource(EXCEL_FILE_DESTINATION_PATH));
        sortedItemStreamWriter.setReader(reader);
        sortedItemStreamWriter.setWriter(writer);
        sortedItemStreamWriter.afterPropertiesSet();
        sortedItemStreamWriter.open(executionContext);

        items = new ArrayList<>();

        BatchExcelRow from = new BatchExcelRow.Builder(dateFormat.parse("27/05/2014"), Sens.DEPARTURE) //
                .departureStation(new Station("Liège-Guillemins")) //
                .arrivalStation(new Station("Bruxelles-central")) //
                .expectedDepartureTime(timeFormat.parse("08:00")) //
                .expectedArrivalTime(timeFormat.parse("09:00")) //
                .expectedTrain1(new Train("466")) //
                .effectiveDepartureTime(timeFormat.parse("08:05")) //
                .effectiveArrivalTime(timeFormat.parse("09:15")) //
                .effectiveTrain1(new Train("466")) //
                .delay(15L) //
                .build();
        BatchExcelRow to = new BatchExcelRow.Builder(dateFormat.parse("21/05/2014"), Sens.ARRIVAL) //
                .departureStation(new Station("Bruxelles-central")) //
                .arrivalStation(new Station("Liège-Guillemins")) //
                .expectedDepartureTime(timeFormat.parse("14:00")) //
                .expectedArrivalTime(timeFormat.parse("15:00")) //
                .expectedTrain1(new Train("529")) //
                .effectiveDepartureTime(timeFormat.parse("14:05")) //
                .effectiveArrivalTime(timeFormat.parse("15:15")) //
                .effectiveTrain1(new Train("529")) //
                .delay(15L) //
                .build();
        BatchExcelRow replace = new BatchExcelRow.Builder(dateFormat.parse("22/05/2014"), Sens.DEPARTURE) //
                .departureStation(new Station("Liège-Guillemins")) //
                .arrivalStation(new Station("Bruxelles-central")) //
                .expectedDepartureTime(timeFormat.parse("14:00")) //
                .expectedArrivalTime(timeFormat.parse("15:00")) //
                .expectedTrain1(new Train("516")) //
                .effectiveDepartureTime(timeFormat.parse("14:05")) //
                .effectiveArrivalTime(timeFormat.parse("15:25")) //
                .effectiveTrain1(new Train("516")) //
                .delay(25L) //
                .index(1L) //
                .build();

        items.add(from);
        items.add(to);
        items.add(replace);
    }

    public void copyFile() throws IOException {
        Path source = new ClassPathResource(EXCEL_FILE_NAME).getFile().toPath();
        Path destination = Paths.get(EXCEL_FILE_DESTINATION_PATH);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    public void assertFile() {
        Path destination = Paths.get(EXCEL_FILE_DESTINATION_PATH);
        Path tempFile = Paths.get(EXCEL_FILE_DESTINATION_PATH + ".tmp");
        Path backupFile = Paths.get(EXCEL_FILE_DESTINATION_PATH + ".bak");

        Path newPAth = Paths.get(".");
        System.out.println("path=" + newPAth.toFile().getAbsolutePath());

        Assert.assertTrue(destination.toFile().exists());
        Assert.assertTrue(destination.toFile().isFile());
        Assert.assertFalse(tempFile.toFile().exists());
        Assert.assertFalse(backupFile.toFile().exists());
    }

    @After
    public void tearDown() throws Exception {
        cleanUp();
    }

    @Test
    public void testWrite() throws Exception {
        sortedItemStreamWriter.write(items);

        assertFile();
    }
}