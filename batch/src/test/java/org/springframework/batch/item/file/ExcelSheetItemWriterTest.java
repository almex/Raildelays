package org.springframework.batch.item.file;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.writer.BatchExcelRowAggregator;
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
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(BlockJUnit4ClassRunner.class)
public class ExcelSheetItemWriterTest extends AbstractFileTest {

    private ExcelSheetItemWriter<BatchExcelRow> writer;

    private List<BatchExcelRow> items = new ArrayList<>();

    private ExecutionContext executionContext;

    @Before
    public void setUp() throws Exception {
        File directory = new File(CURRENT_PATH);

        if (!directory.exists()) {
            Assert.assertTrue("Cannot create a directory for the test", directory.mkdir());
        } else {
            cleanUp();
        }


        executionContext = MetaDataInstanceFactory.createStepExecution().getExecutionContext();
        writer = new ExcelSheetItemWriter<>();
        writer.setTemplate(new ClassPathResource("template.xls"));
        writer.setResource(new FileSystemResource(CURRENT_PATH + "output.xls"));
        writer.setRowAggregator(new BatchExcelRowAggregator());
        writer.setName("test");
        writer.setRowsToSkip(21);
        writer.setMaxItemCount(40);
        writer.afterPropertiesSet();


        items = new ArrayList<>();

        List<LocalDate> dates = new ArrayList<>(80);

        for (int i = 0; i < 80; i++) {
            dates.add(LocalDate.now().minus(1, ChronoUnit.DAYS));
        }


        for (LocalDate date : dates) {
            BatchExcelRow from = new BatchExcelRow.Builder(date, Sens.DEPARTURE) //
                    .departureStation(new Station("Liège-Guillemins")) //
                    .arrivalStation(new Station("Bruxelles-central")) //
                    .expectedDepartureTime(LocalTime.parse("08:00")) //
                    .expectedArrivalTime(LocalTime.parse("09:00")) //
                    .expectedTrain1(new Train("466")) //
                    .effectiveDepartureTime(LocalTime.parse("08:05")) //
                    .effectiveArrivalTime(LocalTime.parse("09:15")) //
                    .effectiveTrain1(new Train("466")) //
                    .build();
            BatchExcelRow to = new BatchExcelRow.Builder(date, Sens.ARRIVAL) //
                    .departureStation(new Station("Bruxelles-central")) //
                    .arrivalStation(new Station("Liège-Guillemins")) //
                    .expectedDepartureTime(LocalTime.parse("14:00")) //
                    .expectedArrivalTime(LocalTime.parse("15:00")) //
                    .expectedTrain1(new Train("529")) //
                    .effectiveDepartureTime(LocalTime.parse("14:05")) //
                    .effectiveArrivalTime(LocalTime.parse("15:15")) //
                    .effectiveTrain1(new Train("529")) //
                    .build();

            items.add(from);
            items.add(to);
        }
    }

    @Test
    public void testTemplate() throws Exception {
        writer.open(executionContext);
        writer.write(items.subList(0, 2));
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
        Assert.assertEquals(117248, getExcelFiles()[0].length());
    }

    @Test
    public void testNoTemplateOLE2() throws Exception {
        writer.setTemplate(null);
        writer.setResource(new FileSystemResource(CURRENT_PATH + "output.xls"));
        writer.open(executionContext);
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
    }

    @Test
    public void testNoTemplateOOXML() throws Exception {
        writer.setTemplate(null);
        writer.setResource(new FileSystemResource(CURRENT_PATH + "output.xlsx"));
        writer.open(executionContext);
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
    }

    @Test(expected = ItemStreamException.class)
    public void testInvalidFormatException() throws Exception {
        Path path = Paths.get(CURRENT_PATH, "output.txt");

        try (OutputStream outputStream = Files.newOutputStream(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.DELETE_ON_CLOSE)) {
            outputStream.write(new byte[]{1, 2, 3});
        }

        try {
            writer.setTemplate(null);
            writer.setResource(new FileSystemResource(path.toFile()));
            writer.open(executionContext);
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void testShouldDeleteIfExists() throws Exception {
        Path path = Paths.get(CURRENT_PATH);
        Path file = Files.createTempFile(path, "output", ".xls");

        Instant original = Files
                .readAttributes(file, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)
                .creationTime()
                .toInstant();

        Thread.sleep(100);

        writer.setResource(new FileSystemResource(file.toFile()));
        writer.setShouldDeleteIfExists(true);
        writer.open(executionContext);
        writer.close();

        Instant actual = Files
                .readAttributes(file, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)
                .creationTime()
                .toInstant();

        // Apply best effort pattern to delete the file at the end of the process
        try (OutputStream ignored = Files.newOutputStream(file, StandardOpenOption.DELETE_ON_CLOSE)) {
            Assert.assertNotEquals(original, actual);
        }
    }

    @Test
    public void testFileLimits() throws Exception {
        writer.open(executionContext);
        writer.write(items.subList(0, 10));
        writer.update(executionContext);
        writer.write(items.subList(10, 20));
        writer.update(executionContext);
        writer.write(items.subList(20, 30));
        writer.update(executionContext);
        writer.write(items.subList(30, 40));
        writer.update(executionContext);
        writer.write(items.subList(40, 80));
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
        Assert.assertEquals(124416, getExcelFiles()[0].length());
    }

    @Test
    public void testRestart() throws Exception {
        writer.open(executionContext);
        writer.write(items.subList(0, 10));
        writer.update(executionContext);
        writer.close();
        writer.open(executionContext); //-- By retrieving the same execution context it should be able to restart
        writer.write(items.subList(10, 40));
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
        Assert.assertEquals(124416, getExcelFiles()[0].length());
    }

    @Test
    public void testEmptyList() throws Exception {
        writer.open(executionContext);
        writer.write(Collections.emptyList());
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
        Assert.assertEquals(117248, getExcelFiles()[0].length());
    }

    @After
    public void tearDown() throws InterruptedException {
        writer.close();
        cleanUp();
    }
}
