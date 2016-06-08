package de.unirostock.sems.M2CAT;

import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.executor.PriorityThreadPoolExecutor;

public class Config {
	
	private static volatile Config instance = null;
	
	public static Config getConfig() {
		return instance;
	}
	
	public static Config loadConfig(ServletContext context) {
		// TODO
		if( instance == null ) {
			instance = new Config();
			instance.init();
		}
		
		return instance;
	}
	
	// ----------------------------------------
	
	/**
	 * Default private Constructor
	 */
	private Config() {}
	
	/** Bolt Connection to the neo4j instance */
//	private String neo4jUrl		= "bolt://morre.sems.uni-rostock.de/";
	/** Url to morre */
//	private String morreUrl		= "http://morre.sems.uni-rostock.de:7474/morre/query";
	
	/** Bolt Connection to the neo4j instance */
	private String neo4jUrl		= "bolt://localhost";
	/** Url to morre */
	private String morreUrl		= "http://localhost:7474/morre/query";
	/** Url to webCat, leave null to deactivate */
	private String webCatUrl	= "http://webcat.sems.uni-rostock.de";//null;
	/** default (minimum) size of the execution thread pool */
	private int threadPoolSize				= 2;
	/** maximum size of the execution thread pool */
	private int threadPoolMaxSize			= 5;
	/** maximum idle time for one thread in seconds*/
	private long threadPoolKeepAliveTime	= 60;
	/** prefix for all temp files created by this applicaiton */
	private String tempFilePrefix			= "m2cat";
	
//	/** cached connection to the neo4j database */
//	private Connection jdbcConnection = null;
	
	/** central thread pool executor */
	private PriorityThreadPoolExecutor executor = null;
	
	/** Bolt driver */
	private Driver driver = null;
	
	/**
	 * Inits stuff.
	 */
	private void init() {
		
		// init connection to neo4j
		try {
			driver = GraphDatabase.driver(neo4jUrl);
		} catch (Exception e) {
			LOGGER.error(e, "Cannot initialize connection to neo4j");
		}
		
		// init executor
		executor = new PriorityThreadPoolExecutor(threadPoolSize, threadPoolMaxSize, threadPoolKeepAliveTime, TimeUnit.SECONDS);
	}

	public String getNeo4jUrl() {
		return neo4jUrl;
	}
	
	public String getMorreUrl() {
		return morreUrl;
	}

	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public int getThreadPoolMaxSize() {
		return threadPoolMaxSize;
	}

	public long getThreadPoolKeepAliveTime() {
		return threadPoolKeepAliveTime;
	}
	
	public String getTempFilePrefix() {
		return tempFilePrefix;
	}
	
	public String getWebCatUrl() {
		return webCatUrl;
	}
	
	@JsonIgnore
	public Session getDatabaseSession() {
		return driver.session();
	}
	
	@JsonIgnore
	public PriorityThreadPoolExecutor getExecutor() {
		return executor;
	}
}
