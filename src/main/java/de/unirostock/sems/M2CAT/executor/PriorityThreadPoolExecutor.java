package de.unirostock.sems.M2CAT.executor;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		super( corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<Runnable>(20, new PriorityComparator()) );
	}
	
	public void execute(PriorityJob command) {
		super.execute(command);
	}
	
	private static class PriorityComparator<T extends PriorityJob> implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			return o1.getPriority() - o2.getPriority();
		}

	}
}
