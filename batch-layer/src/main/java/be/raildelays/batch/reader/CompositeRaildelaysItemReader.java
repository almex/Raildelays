package be.raildelays.batch.reader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.Resource;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;

public class CompositeRaildelaysItemReader implements
		ItemReader<List<LineStop>>, ItemStreamReader<List<LineStop>> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CompositeRaildelaysItemReader.class);

	@javax.annotation.Resource
	Validator validator;

	private ItemReaderAdapter<List<LineStop>> delegate;

	private FlatFileItemReader<String> fileReader;

	private String date;

	private Resource resource;

	private StepExecution stepExecution;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		fileReader.open(executionContext);

	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		fileReader.update(executionContext);

	}

	@Override
	public void close() throws ItemStreamException {
		fileReader.close();
	}

	public List<LineStop> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		List<LineStop> result = null;
		fileReader.setResource(resource);
		String trainId = fileReader.read();
		SimpleDateFormat formater = new SimpleDateFormat("dd/mm/yyyy");
		Date date = formater.parse(this.date);

		if (trainId != null) {
			System.out.printf("Processing %s...\n", trainId);
			Object[] arguments = new Object[] { date,
					new Station("Liège-Guillemins"),
					new Station("Brussels (Bruxelles)-Central"), 15 };

			delegate.setArguments(arguments);

			result = delegate.read();
		}

		return result;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setFileReader(FlatFileItemReader<String> fileReader) {
		this.fileReader = fileReader;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setDelegate(ItemReaderAdapter<List<LineStop>> delegate) {
		this.delegate = delegate;
	}

}
