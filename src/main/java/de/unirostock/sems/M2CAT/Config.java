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
		
		if( instance == null ) {
			instance = new Config();
			instance.load(context);
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
	/** Url to a feedback/ticket form, leave null to deactivate */
	private String feedbackUrl	= "https://sems.uni-rostock.de/trac/m2cat/newticket?from={0}";
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
	 * load settings from context
	 */
	private void load(ServletContext context) {
		
		LOGGER.setMinLevel(LOGGER.WARN);
		String logLevel = Util.getParam(context, "LOGLEVEL", "WARN");
		if( logLevel != null ) {
			LOGGER.warn("Set desired loglevel to ", logLevel);
			
			if( logLevel.equals("DEBUG") ) {
				LOGGER.setMinLevel(LOGGER.DEBUG);
				LOGGER.setLogStackTrace(true);
			}
			else if( logLevel.equals("INFO") )
				LOGGER.setMinLevel(LOGGER.INFO);
			else if( logLevel.equals("WARN") )
				LOGGER.setMinLevel(LOGGER.WARN);
			else if( logLevel.equals("ERROR") )
				LOGGER.setMinLevel(LOGGER.ERROR);
			else if( logLevel.equals("NONE") )
				LOGGER.setLogToStdErr(false);
			else
				LOGGER.error("Unknown log level!");
		}
		
		this.neo4jUrl = Util.getParam(context, "NEO4J_URL", this.neo4jUrl);
		this.morreUrl = Util.getParam(context, "MORRE_URL", this.morreUrl);
		this.webCatUrl = Util.getParam(context, "WEBCAT_URL", this.webCatUrl);
		this.feedbackUrl = Util.getParam(context, "FEEDBACK_URL", feedbackUrl);
		
		this.tempFilePrefix = Util.getParam(context, "TEMP_PREFIX", this.tempFilePrefix);
		
		this.threadPoolSize = (int) Util.getParam(context, "THREADPOOL_SIZE", this.threadPoolSize);
		this.threadPoolMaxSize = (int) Util.getParam(context, "THREADPOOL_MAX_SIZE", this.threadPoolMaxSize);
		this.threadPoolKeepAliveTime = Util.getParam(context, "THREADPOOL_KEEPALIVE", this.threadPoolKeepAliveTime);
		
	}
	
	/**
	 * Inits stuff.
	 */
	private void init() {
		
		// init connection to neo4j
		try {
			driver = GraphDatabase.driver(neo4jUrl, org.neo4j.driver.v1.Config.build().withEncryptionLevel(org.neo4j.driver.v1.Config.EncryptionLevel.NONE).toConfig());
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
	
	public String getFeedbackUrl() {
		return feedbackUrl;
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