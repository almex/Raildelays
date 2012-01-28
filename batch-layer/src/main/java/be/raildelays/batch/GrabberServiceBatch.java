package be.raildelays.batch.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.springframework.scheduling.quartz.JobDetailBean;

public class BatchWorker extends JobDetailBean {

	@Override
	public JobKey getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobDataMap getJobDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDurable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPersistJobDataAfterExecution() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConcurrentExectionDisallowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requestsRecovery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobBuilder getJobBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

}
