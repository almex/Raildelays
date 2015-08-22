package org.springframework.batch.item.support;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.bean.ExcelRowComparator;
import be.raildelays.batch.listener.ResourceLocatorListener;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.batch.support.SimpleResourceItemSearch;
import be.raildelays.batch.support.ToWriteExcelResourceLocator;
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
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ExcelSheetItemReader;
import org.springframework.batch.item.file.ExcelSheetItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(BlockJUnit4ClassRunner.class)
public class MultiResourceMaxItemCountItemWriterTest extends AbstractFileTest {

    private MultiResourceMaxItemCountItemWriter writer;

    private List<BatchExcelRow> items = new ArrayList<>();

    private ExecutionContext executionContext;

    private StepExecution stepExecution;

    private ToWriteExcelResourceLocator resourceLocator;

    private ResourceLocatorListener listener;

    @Before
    public void setUp() throws Exception {
        File directory = new File(CURRENT_PATH);

        if (!directory.exists()) {
            directory.mkdir();
        } else {
            cleanUp();
        }

        stepExecution = MetaDataInstanceFactory.createStepExecution();
        ExcelSheetItemReader<BatchExcelRow> reader = new ExcelSheetItemReader<>();
        FileSystemResource resource = new FileSystemResource(CURRENT_PATH + "retard_sncb.xls");
        ExcelSheetItemWriter<BatchExcelRow> delegate = new ExcelSheetItemWriter<>();
        SimpleResourceItemSearch resourceItemSearch = new SimpleResourceItemSearch();
        resourceLocator = new ToWriteExcelResourceLocator();
        executionContext = stepExecution.getExecutionContext();
        writer = new MultiResourceMaxItemCountItemWriter();
        listener = new ResourceLocatorListener();


        reader.setName("test");
        reader.setSheetIndex(0);
        reader.setRowsToSkip(21);
        reader.setMaxItemCount(40);
        reader.setRowMapper(new BatchExcelRowMapper());
        reader.setResource(resource);
        reader.afterPropertiesSet();

        resourceItemSearch.setReader(reader);
        resourceItemSearch.setComparator(new ExcelRowComparator());

        resourceLocator.setResource(resource);
        resourceLocator.setResourceItemSearch(resourceItemSearch);
        resourceLocator.setKeyName("foo");

        delegate.setName("test2");
        delegate.setTemplate(new ClassPathResource("template.xls"));
        delegate.setRowAggregator(new BatchExcelRowAggregator());
        delegate.setSheetIndex(0);
        delegate.setRowsToSkip(21);
        delegate.setMaxItemCount(40);
        delegate.afterPropertiesSet();

        writer.setName("test1");
        writer.setDelegate(delegate);
        writer.setResourceLocator(resourceLocator);


        items = new ArrayList<>();

        List<LocalDate> dates = new ArrayList<>(40);
        LocalDate temp = LocalDate.now();

        for (int i = 0; i < 40; i++) {
            temp = temp.minus(1, ChronoUnit.WEEKS);
            dates.add(temp);
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
        listener.beforeStep(stepExecution);
        writer.open(executionContext);
        listener.beforeWrite(items.subList(0, 2));
        writer.write(items.subList(0, 2));
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
        Assert.assertEquals(117248, getExcelFiles()[0].length());
    }

    @Test
    public void testFileLimits() throws Exception {
        listener.beforeStep(stepExecution);
        listener.beforeWrite(items.subList(0, 10));
        writer.open(executionContext);
        writer.write(items.subList(0, 10));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(10, 20));
        writer.write(items.subList(10, 20));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(20, 30));
        writer.write(items.subList(20, 30));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(30, 40));
        writer.write(items.subList(30, 40));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(40, 50));
        writer.write(items.subList(40, 50));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(50, 60));
        writer.write(items.subList(50, 60));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(60, 70));
        writer.write(items.subList(60, 70));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(70, 80));
        writer.write(items.subList(70, 80));
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(2, getExcelFiles().length);
        Assert.assertEquals(124416, getExcelFiles()[0].length());
        Assert.assertEquals(124416, getExcelFiles()[1].length());
    }

    @Test
    public void testRestart() throws Exception {
        listener.beforeStep(stepExecution);
        listener.beforeWrite(items.subList(0, 20));
        writer.open(executionContext);
        writer.write(items.subList(0, 20));
        writer.update(executionContext);
        writer.close();

        listener.beforeWrite(items.subList(20, 40));
        writer.open(executionContext);
        writer.write(items.subList(20, 40));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(40, 50));
        writer.write(items.subList(40, 50));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(50, 60));
        writer.write(items.subList(50, 60));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(60, 70));
        writer.write(items.subList(60, 70));
        writer.update(executionContext);

        listener.beforeWrite(items.subList(70, 80));
        writer.write(items.subList(70, 80));
        writer.update(executionContext);

        writer.close();

        Assert.assertEquals(2, getExcelFiles().length);
        Assert.assertEquals(124416, getExcelFiles()[0].length());
        Assert.assertEquals(124416, getExcelFiles()[1].length());
    }

    @Test
    public void testEmptyList() throws Exception {
        listener.beforeStep(stepExecution);
        listener.beforeWrite(Collections.<BatchExcelRow>emptyList());
        writer.open(executionContext);
        writer.write(Collections.<BatchExcelRow>emptyList());
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(0, getExcelFiles().length);
    }

    @After
    public void tearDown() throws InterruptedException {
        cleanUp();
    }

}
