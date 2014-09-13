package org.springframework.batch.item.file;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class ExcelSheetItemReaderTest {

    private static final String CURRENT_PATH = "." + File.separator + "target" + File.separator;
    private static final String OPEN_XML_FILE_EXTENSION = ".xlsx";
    private static final String EXCEL_FILE_EXTENSION = ".xls";
    private ExcelSheetItemReader<BatchExcelRow> reader;

    @Before
    public void setUp() throws Exception {
        reader = new ExcelSheetItemReader<BatchExcelRow>();
        reader.setResource(new ClassPathResource("retard_sncb 20140522" + EXCEL_FILE_EXTENSION));
        reader.setRowsToSkip(21);
        reader.setSheetIndex(0);
        reader.setMaxItemCount(40);
        reader.setRowMapper(new BatchExcelRowMapper());
        reader.afterPropertiesSet();
        reader.setName("test");
        reader.open(MetaDataInstanceFactory.createStepExecution()
                .getExecutionContext());
    }

    @Test
    public void testRead() throws Exception {
        ExcelRow row = reader.read();

        Assert.assertNotNull(row);
        Assert.assertEquals("515", row.getEffectiveTrain1().getEnglishName());
    }

    @After
    public void tearDown() {
        reader.close();
    }

}
