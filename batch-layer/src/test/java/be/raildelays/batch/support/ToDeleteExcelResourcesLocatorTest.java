package be.raildelays.batch.support;

import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by xbmc on 16-04-15.
 */
public class ToDeleteExcelResourcesLocatorTest {

    private static Path TARGET_PATH; // Should be ./target/

    private static Path SOURCE_PATH;

    private static Path DESTINATION_PATH;

    private static Path[] PATHS;

    private static final Logger LOGGER = LoggerFactory.getLogger(ToDeleteExcelResourcesLocator.class);

    @Before
    public void setUp() throws Exception {
        File templateFile = new ClassPathResource("template.xls").getFile();

        TARGET_PATH = templateFile.getParentFile().getParentFile().toPath();
        SOURCE_PATH = Paths.get(TARGET_PATH.toString(), "source");
        DESTINATION_PATH = Paths.get(TARGET_PATH.toString(), "destination");
        PATHS = new Path[]{
                Paths.get(SOURCE_PATH.toString(), "2010.xls"),
                Paths.get(SOURCE_PATH.toString(), "2011.xls"),
                Paths.get(SOURCE_PATH.toString(), "2012.xls"),
                Paths.get(DESTINATION_PATH.toString(), "2010.xls"),
                Paths.get(DESTINATION_PATH.toString(), "2011.xls"),
                Paths.get(DESTINATION_PATH.toString(), "2012.java"),
                Paths.get(DESTINATION_PATH.toString(), "2015.xls")
        };

        cleanUp();
        createFiles();
    }

    @Test
    public void testGetResources() throws IOException {
        Assert.assertEquals(3, Files.list(SOURCE_PATH).count());
        Assert.assertEquals(4, Files.list(DESTINATION_PATH).count());

        Resource[] result = ToDeleteExcelResourcesLocator.getResources(new FileSystemResource(SOURCE_PATH.toFile()),
                new FileSystemResource(DESTINATION_PATH.toFile()));

        Assert.assertEquals(2, result.length);
    }

    @Test(expected = AssertionError.class)
    @Ignore
    public void testAssertion() throws IOException {
        ToDeleteExcelResourcesLocator.getResources(new FileSystemResource(PATHS[0].toFile()),
                new FileSystemResource(DESTINATION_PATH.toFile()));
    }

    @After
    public void tearDown() throws Exception {
        cleanUp();
    }

    public static void createFiles() {
        try {
            FileUtils.forceMkdir(SOURCE_PATH.toFile());
            FileUtils.forceMkdir(DESTINATION_PATH.toFile());
        } catch (IOException e) {
            LOGGER.error("[CreateFiles] Error when creating source or destination directory", e);
        }

        Stream.of(PATHS)
                .map(Path::toFile) // Go to synchronous access
                .forEach(file -> {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        LOGGER.error("[CreateFiles] Error when creating source or destination file", e);
                    }
                });
    }

    /**
     * Using NIO API we have no guarantee that the previous operation succeed.
     * I mean it's an asynchronous I/O access. So, a creation subsequent to a delete may failed
     * because the file still exists.
     *
     * @throws IOException
     */
    public static void cleanUp() {
        //-- We remove any result from the test
        try {
            Stream.concat(Files.list(SOURCE_PATH), Files.list(DESTINATION_PATH))
                    .map(Path::toFile) // Go to synchronous access
                    .filter(file -> !file.delete())
                    .forEach(File::deleteOnExit);

            SOURCE_PATH.toFile().delete();
            DESTINATION_PATH.toFile().delete();
        } catch (NoSuchFileException e) {
            LOGGER.info("[CleanUp] Source or destination directory does not exist");
        } catch (IOException e) {
            LOGGER.info("[CleanUp] Source or destination directory is not accessible");
        }
    }


}