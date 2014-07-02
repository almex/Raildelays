package be.raildelays.batch.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

/**
 * Created by xbmc on 02-07-14.
 */
public interface ResourceCreator {

    Resource create(ExecutionContext context);
}
