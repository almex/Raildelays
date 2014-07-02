package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.SimpleResourceItemSearch;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.batch.reader.ExcelSheetItemReader;
import be.raildelays.batch.support.ExcelFileSystemResourceDecorator;
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

import java.io.File;
import java.io.FileFilter;
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
public class SortedItemStreamWriterTest2 {

    public static final String EXCEL_FILE_NAME = "retard_sncb 20140522.xls";

    public static final String EXCEL_FILE_SOURCE_PATH = "." + File.separator + "src"
            + File.separator + "it" + File.separator + "resources" + File.separator + EXCEL_FILE_NAME;

    public static final String EXCEL_FILE_DESTINATION_PATH = "." + File.separator + "target" + File.separator + EXCEL_FILE_NAME;

    private SortedItemStreamWriter<BatchExcelRow> sortedItemStreamWriter;

    private static final String BASE_DIRECTORY = "." + File.separator + "target" + File.separator;

    private List<BatchExcelRow> items = new ArrayList<>();

    private ExecutionContext executionContext;

    @Before
    public void setUp() throws Exception {
        MultiResourceItemWriter writer = new MultiResourceItemWriter();
        ExcelSheetItemReader<BatchExcelRow> reader = new ExcelSheetItemReader<>();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ExcelFileSystemResourceDecorator resource = new ExcelFileSystemResourceDecorator<BatchExcelRow>(BASE_DIRECTORY);
        SimpleResourceItemSearch<BatchExcelRow> container = new SimpleResourceItemSearch<>();

        copyFile();

        writer.setSheetIndex(0);
        writer.setRowsToSkip(21);
        writer.setMaxItemCount(40);
        writer.setTemplate(new ClassPathResource("template.xls"));
        writer.setResourceDecorator(resource);
        writer.afterPropertiesSet();

        reader.setName("test");
        reader.setSheetIndex(0);
        reader.setRowsToSkip(21);
        reader.setMaxItemCount(40);
        reader.setRowMapper(new BatchExcelRowMapper());
        reader.setResource(resource);
        reader.afterPropertiesSet();

        container.setReader(reader);
        resource.setResourceItemSearch(container);

        sortedItemStreamWriter = new SortedItemStreamWriter<>();
        executionContext = MetaDataInstanceFactory.createStepExecution().getExecutionContext();
        sortedItemStreamWriter.setResource(resource);
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
        BatchExcelRow to = new BatchExcelRow.Builder(dateFormat.parse("23/05/2014"), Sens.ARRIVAL) //
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

    public void deleteFile() throws IOException {
        for (File file : getFiles()) {
            file.delete();
        }
    }

    public void assertFile() {
        Assert.assertEquals(1, getFiles().length);
    }

    public void copyFile() throws IOException {
        Path source = new ClassPathResource(EXCEL_FILE_NAME).getFile().toPath();
        Path destination = Paths.get(EXCEL_FILE_DESTINATION_PATH);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    File[] getFiles() {
        return new File(BASE_DIRECTORY).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".xls") || pathname.getName().endsWith(".xlsx");
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        deleteFile();
    }

    @Test
    public void testWrite() throws Exception {
        sortedItemStreamWriter.write(items);

        assertFile();
    }
}