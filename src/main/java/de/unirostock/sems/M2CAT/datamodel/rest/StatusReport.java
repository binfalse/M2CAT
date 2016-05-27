package de.unirostock.sems.M2CAT.datamodel.rest;

import java.io.Serializable;
import java.util.Date;

public class StatusReport implements Serializable {

	private static final long serialVersionUID = -3607877163282198142L;
	
	public static final String FINISHED = "finished";
	public static final String PROCESSING = "processing";
	public static final String ERROR = "error";
	
	private String state = PROCESSING;
	private Date started = null;
	private double process = 0.0f;
	
	public StatusReport(String state, Date started, double process) {
		super();
		this.state = state;
		this.started = started;
		this.process = process;
	}

	public StatusReport() {
		super();
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public double getProcess() {
		return process;
	}

	public void setProcess(double process) {
		this.process = process;
	}
	
}
