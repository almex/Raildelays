package be.raildelays.batch.writer;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import be.raildelays.domain.xls.ExcelRow.Builder;
import org.apache.commons.lang.time.DateUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class ExcelSheetItemWriterTest {

    private ExcelSheetItemWriter writer;

    private static final String CURRENT_PATH = "." + File.separator + "target" + File.separator;

    private static final String OPEN_XML_FILE_EXTENSION = ".xlsx";

    private static final String EXCEL_FILE_EXTENSION = ".xls";

    private List<ExcelRow> items = new ArrayList<>();

    private ExecutionContext executionContext;

    @Before
    public void setUp() throws Exception {
        File directory = new File(CURRENT_PATH);

        if (!directory.exists()) {
            directory.mkdir();
        } else {
            cleanUp();
        }


        executionContext = MetaDataInstanceFactory.createStepExecution().getExecutionContext();
        writer = new ExcelSheetItemWriter<ExcelRow>();
        writer.setTemplate(new ClassPathResource("template.xls"));
        writer.setResource(new FileSystemResource(CURRENT_PATH + "output" + EXCEL_FILE_EXTENSION));
        writer.setRowAggregator(new ExcelRowAggregator());
        writer.setName("test");
        writer.setRowsToSkip(21);
        writer.setMaxItemCount(40);
        writer.afterPropertiesSet();
        writer.open(executionContext);


        items = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Iterator<Calendar> it = DateUtils.<Calendar>iterator(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000"), DateUtils.RANGE_MONTH_MONDAY);

        for (int i = 0; i < 80 && it.hasNext(); i++) {
            List<ExcelRow> excelRows = new ArrayList<>();
            Date date = it.next().getTime();
            ExcelRow from = new Builder(date, Sens.DEPARTURE) //
                    .departureStation(new Station("Liège-Guillemins")) //
                    .arrivalStation(new Station("Bruxelles-central")) //
                    .expectedDepartureTime(formatter.parse("08:00")) //
                    .expectedArrivalTime(formatter.parse("09:00")) //
                    .expectedTrain1(new Train("466")) //
                    .effectiveDepartureTime(formatter.parse("08:05")) //
                    .effectiveArrivalTime(formatter.parse("09:15")) //
                    .effectiveTrain1(new Train("466")) //
                    .build();
            ExcelRow to = new Builder(date, Sens.ARRIVAL) //
                    .departureStation(new Station("Bruxelles-central")) //
                    .arrivalStation(new Station("Liège-Guillemins")) //
                    .expectedDepartureTime(formatter.parse("14:00")) //
                    .expectedArrivalTime(formatter.parse("15:00")) //
                    .expectedTrain1(new Train("529")) //
                    .effectiveDepartureTime(formatter.parse("14:05")) //
                    .effectiveArrivalTime(formatter.parse("15:15")) //
                    .effectiveTrain1(new Train("529")) //
                    .build();

            items.add(from);
            items.add(to);
        }
    }

    @Test
    public void testTemplate() throws Exception {
        writer.write(items.subList(0, 2));
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
        Assert.assertEquals(117248, getExcelFiles()[0].length());
    }

    @Test
    public void testFileLimits() throws Exception {
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
        Assert.assertEquals(123392, getExcelFiles()[0].length());
    }

    @Test
    public void testRestart() throws Exception {
        writer.write(items.subList(0, 10));
        writer.update(executionContext);
        writer.close();
        writer.open(executionContext); //-- By retrieving the same execution context it should be able to restart
        writer.write(items.subList(10, 40));
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
        Assert.assertEquals(123392, getExcelFiles()[0].length());
    }

    @Test
    public void testEmptyList() throws Exception {
        writer.write(Collections.<ExcelRow>emptyList());
        writer.update(executionContext);
        writer.close();

        Assert.assertEquals(1, getExcelFiles().length);
        Assert.assertEquals(117248, getExcelFiles()[0].length());
    }

    private File[] getExcelFiles() {
        final File directory = new File(CURRENT_PATH);

        File[] result = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(EXCEL_FILE_EXTENSION) || pathname.getName().endsWith(OPEN_XML_FILE_EXTENSION);
            }
        });

        return result != null ? result : new File[0];
    }

    @After
    public void tearDown() throws InterruptedException {
        cleanUp();
    }

    private void cleanUp() {
        //-- We remove any result from the test
        for (File file : getExcelFiles()) {
            file.delete();
        }
    }
}
