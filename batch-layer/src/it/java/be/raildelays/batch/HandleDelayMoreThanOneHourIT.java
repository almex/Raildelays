package be.raildelays.batch;

import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@DirtiesContext
// Because of issue [SPR-8849] (https://jira.springsource.org/browse/SPR-8849)
@ContextConfiguration(locations = {"/jobs/main-job-context.xml"})
@DataSet(value = "classpath:SearchDelaysIntoExcelSheetJobIT.xml", tearDownOperation = DBOperation.DELETE_ALL)
public class HandleDelayMoreThanOneHourIT extends AbstractContextIT {

//    private static final String CURRENT_PATH = "." + File.separator + "target" + File.separator;
//    private static final String OPEN_XML_FILE_EXTENSION = ".xlsx";
//    private static final String EXCEL_FILE_EXTENSION = ".xls";
//
//    private List<BatchExcelRow> items = new ArrayList<>();
//    private StepExecution stepExecution;
//
//    @Before
//    public void setUp() throws Exception {
//        File directory = new File(CURRENT_PATH);
//
//        if (!directory.exists()) {
//            directory.mkdir();
//        } else {
//            cleanUp();
//        }
//
//        items = new ArrayList<>();
//        DateFormat formatter = new SimpleDateFormat("HH:mm");
//        Iterator<Calendar> it = DateUtils.iterator(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000"), DateUtils.RANGE_MONTH_MONDAY);
//    }
//
//    public StepExecution getStepExecution() throws ParseException, IOException {
//        final Map<String, JobParameter> parameters = new HashMap<>();
//
//        parameters.put("input.file.path", new JobParameter("train-list.properties"));
//        parameters.put("date", new JobParameter(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000")));
//        parameters.put("station.a.name", new JobParameter("Liège-Guillemins"));
//        parameters.put("station.b.name", new JobParameter("Brussels (Bruxelles)-Central"));
//        parameters.put("excel.output.path", new JobParameter("./output.xls"));
//        parameters.put("excel.input.template", new JobParameter(new ClassPathResource("template.xls").getFile().getAbsolutePath()));
//
//        final JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution("mainJob", 1L, 1L, new JobParameters(parameters));
//
//        stepExecution = MetaDataInstanceFactory.createStepExecution(jobExecution, "handleDelayMoreThanOneHour", 1l);
//
//        return stepExecution;
//    }

    @Test
    @Ignore // FIXME add javax.mail to dependencies and set parameters correctly
    public void testStep3() throws ParseException, IOException {
        final Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("input.file.path", new JobParameter("train-list.properties"));
        parameters.put("date", new JobParameter(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000")));
        parameters.put("station.a.name", new JobParameter("Liège-Guillemins"));
        parameters.put("station.b.name", new JobParameter("Brussels (Bruxelles)-Central"));
        parameters.put("excel.output.path", new JobParameter("./output.xls"));
        parameters.put("excel.input.template", new JobParameter(new ClassPathResource("template.xls").getFile().getAbsolutePath()));

        JobExecution jobExecution = new JobLauncherTestUtils().launchStep("handleDelayMoreThanOneHour", new JobParameters(parameters));

        Assert.assertFalse(jobExecution.getStatus().isUnsuccessful());
        jobExecution.getStepExecutions().forEach(new Consumer<StepExecution>() {
            @Override
            public void accept(StepExecution stepExecution) {
                Assert.assertEquals(1, stepExecution.getCommitCount());
            }
        });
    }

//    @After
//    public void tearDown() throws InterruptedException {
//        cleanUp();
//    }
//
//    private File[] getExcelFiles() {
//        final File directory = new File(CURRENT_PATH);
//
//        File[] result = directory.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                return pathname.getName().endsWith(EXCEL_FILE_EXTENSION) || pathname.getName().endsWith(OPEN_XML_FILE_EXTENSION);
//            }
//        });
//
//        return result != null ? result : new File[0];
//    }
//
//    private void cleanUp() {
//        //-- We remove any result from the test
//        for (File file : getExcelFiles()) {
//            if (!file.delete()) {
//                file.delete();
//            }
//        }
//    }
}
