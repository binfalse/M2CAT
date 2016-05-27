package de.unirostock.sems.M2CAT.connector;

import java.util.LinkedList;
import java.util.List;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.Config;
import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;
import de.unirostock.sems.M2CAT.executor.PriorityJob;
import de.unirostock.sems.M2CAT.executor.PriorityThreadPoolExecutor;

/**
 * Class representing a job to retrieve additional information
 * corresponding an archive/model
 * 
 * @author martin
 *
 */
public class RetrievalJob extends PriorityJob {
	
	public static enum JobState {
		JOB_START,
		JOB_RUNNING,
		JOB_FINISHED,
		JOB_ERROR
	}
	
	protected RetrievalConnector connector = null;
	protected ArchiveInformation archiveInformation = null;
	
	protected volatile JobState state = JobState.JOB_START;
	protected Exception jobException = null;
	protected List<RetrievalJob> executeAfter = null;
	
	public RetrievalJob(ArchiveInformation archiveInformation, RetrievalConnector connector, int priority) {
		super(priority);
		this.archiveInformation = archiveInformation;
		this.connector = connector;
		
		if( connector == null )
			throw new IllegalArgumentException("Connector is not allowed to be null");
		if( archiveInformation == null )
			throw new IllegalArgumentException("ArchiveInfomration is not allowed to be null");
	}
	
	/**
	 * Adds a Job, executed after this job
	 * 
	 * @param job
	 */
	public void addExecuteAfter(RetrievalJob job) {
		if( executeAfter == null )
			executeAfter = new LinkedList<RetrievalJob>();
		
		if( job != null )
			executeAfter.add(job);
	}
	
	public List<RetrievalJob> getExecutedAfter() {
		return executeAfter;
	}
	/**
	 * Returns true, if the job has finished.
	 * @return
	 */
	public boolean isFinished() {
		if( state == JobState.JOB_FINISHED || state == JobState.JOB_ERROR )
			return true;
		else
			return false;
	}
	
	/**
	 * Returns true, if the job had thrown an exception.
	 * 
	 * @return
	 */
	public boolean hasError() {
		if( state == JobState.JOB_ERROR )
			return true;
		else
			return false;
	}
	
	/**
	 * Returns the exception caused by this job or null.
	 * @return
	 */
	public Exception getException() {
		if( state == JobState.JOB_ERROR )
			return jobException;
		else
			return null;
	}

	@Override
	public void run() {
		
		try {
			state = JobState.JOB_RUNNING;
			retrieve();
			state = JobState.JOB_FINISHED;
		} catch (Exception e) {
			state = JobState.JOB_ERROR;
			jobException = e;
			LOGGER.error(e, "Retrieval Job didn't end correctly");
		}
		
		// execute after jobs
		if( executeAfter != null ) {
			PriorityThreadPoolExecutor executor = Config.getConfig().getExecutor();
			for( RetrievalJob job : executeAfter ) {
				executor.execute(job);
			}
		}
	}
	
	/**
	 * Execution routine. Does the retrieval job.
	 * @throws Exception
	 */
	protected void retrieve() throws Exception {
		// do the stuff
		connector.startRetriving(archiveInformation);
	}
}
