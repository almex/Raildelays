package be.raildelays.batch.test;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.resource.ResourceContext;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.batch.support.ResourceContextAccessibleItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;

/**
 * @author Almex
 */
public class DummyItemStreamReader
        extends AbstractItemStreamItemReader
        implements ResourceContextAccessibleItemStream, InitializingBean {

    private boolean open = true;
    private ResourceContext resourceContext = new ResourceContext(new ExecutionContext(), "foo");

    @Override
    public void afterPropertiesSet() throws Exception {
        this.open = true;
    }

    @Override
    public Object read() throws Exception {
        ExcelRow result = null;

        if (open) {
            result = new ExcelRow.Builder(LocalDate.now(), Sens.ARRIVAL).delay(65L).build(false);
            open = false;
        }

        return result;
    }

    @Override
    public ResourceContext getResourceContext() {

        resourceContext.changeResource(new FileSystemResource("./dummy.xls"));

        return resourceContext;
    }

    @Override
    public void setResource(Resource resource) {
        resourceContext.setResource(resource);
    }
}
