package be.raildelays.batch.support;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.File;
import java.io.IOException;

/**
 * @author Almex
 */
public interface ExcelFileResource<T extends Comparable<? super T>> extends WritableResourceDecorator {

    int getContentRowIndex();

    File getFile(T content) throws IOException;

}
