package be.raildelays.batch.test;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.batch.support.ResourceAwareItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.time.Instant;
import java.util.Date;

/**
 * Created by xbmc on 28-06-15.
 */
public class DummyItemStreamReader extends AbstractItemStreamItemReader implements ResourceAwareItemStream, InitializingBean {

    private boolean open = true;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.open = true;
    }

    @Override
    public Object read() throws Exception {
        ExcelRow result = null;

        if (open) {
            result = new ExcelRow.Builder(Date.from(Instant.now()), Sens.ARRIVAL).delay(65L).build(false);
            open = false;
        }

        return result;
    }

    @Override
    public Resource getResource() {
        return new FileSystemResource("./dummy.xls");
    }

    @Override
    public void setResource(Resource resource) {

    }
}
