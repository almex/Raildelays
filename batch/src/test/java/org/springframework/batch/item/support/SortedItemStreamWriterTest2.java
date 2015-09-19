package org.springframework.batch.item.support;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.batch.writer.ExcelRowAggregator;
import be.raildelays.batch.writer.MultiExcelFileToWriteLocator;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.ExcelSheetItemReader;
import org.springframework.batch.item.file.ExcelSheetItemWriter;
import org.springframework.batch.item.resource.ResourceLocatorItemWriterItemStream;
import org.springframework.batch.item.resource.SimpleResourceItemSearch;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(BlockJUnit4ClassRunner.class)
public class SortedItemStreamWriterTest2 extends AbstractFileTest {

    private List<ExcelRow> items = new ArrayList<>();

    private StepExecution stepExecution;

    private ResourceLocatorItemWriterItemStream<SortedItemStreamWriter<ExcelRow>, ExcelRow> writer;

    @Before
    public void setUp() throws Exception {
        SortedItemStreamWriter<ExcelRow> delegate = new SortedItemStreamWriter<>();
        ExcelSheetItemReader<BatchExcelRow> reader = new ExcelSheetItemReader<>();
        FileSystemResource resource = new FileSystemResource(CURRENT_PATH + "retard_sncb.xls");
        ExcelSheetItemWriter<ExcelRow> writer = new ExcelSheetItemWriter<>();
        MultiExcelFileToWriteLocator resourceLocator = new MultiExcelFileToWriteLocator();
        SimpleResourceItemSearch<BatchExcelRow> itemSearch = new SimpleResourceItemSearch<>();

        copyFile();

        writer.setName("test");
        writer.setSheetIndex(0);
        writer.setRowsToSkip(21);
        writer.setMaxItemCount(40);
        writer.setTemplate(new ClassPathResource("template.xls"));
        writer.setRowAggregator(new ExcelRowAggregator());
        writer.afterPropertiesSet();

        reader.setName("test");
        reader.setSheetIndex(0);
        reader.setRowsToSkip(21);
        reader.setMaxItemCount(40);
        reader.setRowMapper(new BatchExcelRowMapper());
        reader.setResource(resource);
        reader.afterPropertiesSet();

        stepExecution = MetaDataInstanceFactory.createStepExecution();
        delegate.setReader(reader);
        delegate.setWriter(writer);
        delegate.afterPropertiesSet();

        itemSearch.setReader(reader);

        resourceLocator.setDirectory(new FileSystemResource(CURRENT_PATH));
        resourceLocator.setFileExtension("xls");
        resourceLocator.setFilePrefix("retard_sncb");
        resourceLocator.setMaxItemCount(40);
        resourceLocator.setResourceItemSearch(itemSearch);

        this.writer = new ResourceLocatorItemWriterItemStream<>();
        this.writer.setName("test");
        this.writer.setDelegate(delegate);
        this.writer.setResourceLocator(resourceLocator);

        items = new ArrayList<>();

        BatchExcelRow from = new BatchExcelRow.Builder(LocalDate.parse("2014-05-27"), Sens.DEPARTURE) //
                .departureStation(new Station("Liège-Guillemins")) //
                .arrivalStation(new Station("Bruxelles-central")) //
                .expectedDepartureTime(LocalTime.parse("08:00")) //
                .expectedArrivalTime(LocalTime.parse("09:00")) //
                .expectedTrain1(new Train("466")) //
                .effectiveDepartureTime(LocalTime.parse("08:05")) //
                .effectiveArrivalTime(LocalTime.parse("09:15")) //
                .effectiveTrain1(new Train("466")) //
                .delay(15L) //
                .build();
        BatchExcelRow to = new BatchExcelRow.Builder(LocalDate.parse("2014-05-23"), Sens.ARRIVAL) //
                .departureStation(new Station("Bruxelles-central")) //
                .arrivalStation(new Station("Liège-Guillemins")) //
                .expectedDepartureTime(LocalTime.parse("14:00")) //
                .expectedArrivalTime(LocalTime.parse("15:00")) //
                .expectedTrain1(new Train("529")) //
                .effectiveDepartureTime(LocalTime.parse("14:05")) //
                .effectiveArrivalTime(LocalTime.parse("15:15")) //
                .effectiveTrain1(new Train("529")) //
                .delay(15L) //
                .build();
        BatchExcelRow replace = new BatchExcelRow.Builder(LocalDate.parse("2014-05-22"), Sens.DEPARTURE) //
                .departureStation(new Station("Liège-Guillemins")) //
                .arrivalStation(new Station("Bruxelles-central")) //
                .expectedDepartureTime(LocalTime.parse("14:00")) //
                .expectedArrivalTime(LocalTime.parse("15:00")) //
                .expectedTrain1(new Train("516")) //
                .effectiveDepartureTime(LocalTime.parse("14:05")) //
                .effectiveArrivalTime(LocalTime.parse("15:25")) //
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
        writer.open(stepExecution.getExecutionContext());
        writer.write(items);
        writer.close();

        assertFile();
    }
}