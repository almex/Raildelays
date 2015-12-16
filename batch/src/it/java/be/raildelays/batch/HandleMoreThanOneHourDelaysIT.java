package be.raildelays.batch;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@ContextConfiguration(locations = {"/jobs/steps/handle-more-than-one-hour-delays-job-context.xml"})
public class HandleMoreThanOneHourDelaysIT extends AbstractContextIT {

    private static List<String> FILES_NAMES = Collections.singletonList("20140522");

    private static Path TEMPLATE_PATH;

    private static Path TARGET_PATH; // Should be ./target/

    private static Path SOURCE_PATH;

    private static PathMatcher FILTER_EXCEL_FILES = FileSystems.getDefault().getPathMatcher("glob:**/*.{xls,xlsx}");

    @BeforeClass
    public static void setUp() throws IOException {
        SOURCE_PATH = new ClassPathResource("retard_sncb 20140522.xls").getFile().toPath().toAbsolutePath();
        TARGET_PATH = SOURCE_PATH.getParent().getParent().toAbsolutePath();
        TEMPLATE_PATH = new ClassPathResource("template.xls").getFile().toPath().toAbsolutePath();

        cleanUp();
        copyFiles();
    }

    protected static void copyFiles() throws IOException {
        Files.copy(SOURCE_PATH, TARGET_PATH.resolve(SOURCE_PATH.getFileName()), REPLACE_EXISTING);
    }

    protected static Stream<Path> getExcelFiles() {
        try {
            return Files.find(TARGET_PATH, 1, (path, basicFileAttributes) -> FILTER_EXCEL_FILES.matches(path));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error when accessing target path", e);
        }
    }

    protected static void cleanUp() {
        //-- We remove any result from the test
        try (Stream<Path> stream = getExcelFiles()) {
            stream.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @AfterClass
    public static void tearDown() {
        cleanUp();
    }

    @Test
    public void testCompleted() throws Exception {
        final Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("excel.output.path", new JobParameter(TARGET_PATH.toString()));
        parameters.put("excel.file.name", new JobParameter("retard_sncb"));
        parameters.put("excel.file.extension", new JobParameter("xls"));
        parameters.put("excel.archive.path", new JobParameter(TARGET_PATH.toString()));
        parameters.put("excel.template.path", new JobParameter(TEMPLATE_PATH.toString()));
        parameters.put("more.than.one.hour.excel.path", new JobParameter(TARGET_PATH.resolve(SOURCE_PATH.getFileName()).toString()));
        parameters.put("language", new JobParameter("en"));
        parameters.put("threshold.date", new JobParameter(Date.from(LocalDate.of(2015, 5, 1).atStartOfDay().toInstant(ZoneOffset.UTC))));
        // We don't test the last step sendEmail
        parameters.put("mail.server.host", new JobParameter("localhost"));
        parameters.put("mail.server.port", new JobParameter(25L));
        parameters.put("mail.account.username", new JobParameter(""));
        parameters.put("mail.account.password", new JobParameter(""));
        parameters.put("mail.account.address", new JobParameter(""));

        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(new JobParameters(parameters));

        Assert.assertFalse(jobExecution.getStatus().isUnsuccessful());

        Assert.assertEquals(1, getExcelFiles().count());

        getExcelFiles().forEach(path ->
                Assert.assertTrue("The file name is not one of those expectedTime",
                        FILES_NAMES.stream().anyMatch(fileName -> path.getFileName().toString().contains(fileName))));
    }

}
