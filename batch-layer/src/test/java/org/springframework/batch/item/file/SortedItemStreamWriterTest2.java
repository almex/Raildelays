package org.springframework.batch.item.file;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.listener.ResourceLocatorListener;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.batch.support.ItemWriterResourceLocator;
import be.raildelays.batch.support.SimpleResourceItemSearch;
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
import org.springframework.batch.item.support.MultiResourceSupportItemWriter;
import org.springframework.batch.item.support.SortedItemStreamWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RunWith(BlockJUnit4ClassRunner.class)
public class SortedItemStreamWriterTest2 extends AbstractFileTest {

    private List<BatchExcelRow> items = new ArrayList<>();

    private StepExecution stepExecution;

    private ItemWriterResourceLocator resourceLocator;

    private MultiResourceSupportItemWriter<BatchExcelRow> writer;

    private ResourceLocatorListener listener;

    @Before
    public void setUp() throws Exception {
        SortedItemStreamWriter<BatchExcelRow> delegate = new SortedItemStreamWriter<>();
        ExcelSheetItemReader<BatchExcelRow> reader = new ExcelSheetItemReader<>();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        FileSystemResource resource = new FileSystemResource(CURRENT_PATH + "retard_sncb.xls");
        ExcelSheetItemWriter<BatchExcelRow> writer = new ExcelSheetItemWriter<>();
        SimpleResourceItemSearch resourceItemSearch = new SimpleResourceItemSearch();
        resourceLocator = new ItemWriterResourceLocator();
        this.writer = new MultiResourceSupportItemWriter<>();

        copyFile();

        writer.setName("test");
        writer.setSheetIndex(0);
        writer.setRowsToSkip(21);
        writer.setMaxItemCount(40);
        writer.setTemplate(new ClassPathResource("template.xls"));
        writer.setRowAggregator(new BatchExcelRowAggregator());
        writer.afterPropertiesSet();

        reader.setName("test");
        reader.setSheetIndex(0);
        reader.setRowsToSkip(21);
        reader.setMaxItemCount(40);
        reader.setRowMapper(new BatchExcelRowMapper());
        reader.setResource(resource);
        reader.afterPropertiesSet();

        resourceItemSearch.setReader(reader);

        listener = new ResourceLocatorListener();

        resourceLocator.setResource(resource);
        resourceLocator.setResourceItemSearch(resourceItemSearch);
        resourceLocator.setKeyName("foo");

        stepExecution = MetaDataInstanceFactory.createStepExecution();
        delegate.setReader(reader);
        delegate.setWriter(writer);
        delegate.afterPropertiesSet();
        delegate.open(stepExecution.getExecutionContext());

        this.writer.setName("test");
        this.writer.setDelegate(delegate);
        this.writer.setResourceLocator(resourceLocator);

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

    public void assertFile() {
        Assert.assertEquals(1, getExcelFiles().length);
    }


    @After
    public void tearDown() throws Exception {
        cleanUp();
    }

    @Test
    public void testWrite() throws Exception {
        listener.beforeStep(stepExecution);
        listener.beforeWrite(items);
        writer.open(stepExecution.getExecutionContext());
        writer.write(items);
        writer.close();

        assertFile();
    }
}