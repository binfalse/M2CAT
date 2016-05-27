package de.unirostock.sems.M2CAT.executor;

public abstract class PriorityJob implements Runnable {
	
	public static final int MAX_PRIORITY = 100;
	public static final int MIN_PRIORITY = 0;
	
	protected int priority = MIN_PRIORITY;
	
	public PriorityJob(int priority) {
		if( priority > MAX_PRIORITY )
			priority = MAX_PRIORITY;
		else if( priority < MIN_PRIORITY )
			priority = MIN_PRIORITY;
		
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
	
}
