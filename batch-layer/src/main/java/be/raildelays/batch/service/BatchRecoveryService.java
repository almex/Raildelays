package be.raildelays.batch.service;


public interface BatchRecoveryService {

		void stopAllRunningJobs();
		
		void markInconsistentJobsAsFailed();
		
		void restartAllFailedJobs();
		
		void restartAllStoppedJobs();
	
}
