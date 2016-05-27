package de.unirostock.sems.M2CAT.datamodel;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.unirostock.sems.M2CAT.connector.RetrievalJob;

/**
 * Class representing a cached retrieved Archive
 * 
 * @author martin
 *
 */
public class CachedArchiveInfo implements Serializable {
	
	private static final long serialVersionUID = -4542207528502336599L;
	
	protected ArchiveInformation archiveInformation = null;
	protected Date date = new Date();
	protected List<RetrievalJob> jobs = new LinkedList<RetrievalJob>();
	
	public CachedArchiveInfo(ArchiveInformation archiveInformation, Date date) {
		this.archiveInformation = archiveInformation;
		this.date = date;
	}

	public CachedArchiveInfo(ArchiveInformation archiveInformation) {
		this.archiveInformation = archiveInformation;
	}
	
	/**
	 * Adds a job and all "executed after" jobs to this
	 * retrival cache
	 * 
	 * @param job
	 */
	@JsonIgnore
	public void addRetrievalJob(RetrievalJob job) {
		jobs.add(job);
		
		if( job.getExecutedAfter() != null )
			for( RetrievalJob afterJob : job.getExecutedAfter() ) {
				jobs.add(afterJob);
			}
	}
	
	public ArchiveInformation getArchiveInformation() {
		return archiveInformation;
	}

	public Date getDate() {
		return date;
	}

	@JsonIgnore
	public List<RetrievalJob> getJobs() {
		return jobs;
	}
	
	/**
	 * Returns true, if all jobs are finished
	 * 
	 * @return
	 */
	public boolean isFinished() {
		boolean finished = true;
		for( RetrievalJob job : jobs ) {
			if( job.isFinished() == false ) {
				finished = false;
				break;
			}
		}
		
		return finished;
			
	}
	
}
