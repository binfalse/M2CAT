package de.unirostock.sems.M2CAT;

import java.text.MessageFormat;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public abstract class Util {
	
	/**
	 * Returns the feedbackUrl if set, otherwise null
	 * substitutes the first parameter with the url encoded request uri, if request != null.
	 * Otherwise it will set 'WEB_INTERFACE'
	 * 
	 * @param request
	 * @return
	 */
	public static String getFeedbackUrl(HttpServletRequest request) {
		Config config = Config.getConfig();
		if( config.getFeedbackUrl() == null )
			return null;
		
		if( request == null )
			return MessageFormat.format(config.getFeedbackUrl(), "WEB_INTERFACE");
		
		try {
			// try to get the request Uri
			String requestUrl = Util.getCurrentUrl(request);
			return MessageFormat.format(config.getFeedbackUrl(), requestUrl);
		} catch (Exception e) {
			// just return feedbackUrl with default param, if *anything* goes wrong
			return MessageFormat.format(config.getFeedbackUrl(), "WEB_INTERFACE");
		}
	}
	
	/**
	 * reconstructs the current request url
	 * 
	 * @param request
	 * @return
	 */
	public static String getCurrentUrl(HttpServletRequest request) {
		String scheme = request.getScheme();             
		String serverName = request.getServerName(); 
		int serverPort = request.getServerPort();    
		String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");
		String prmstr = (String) request.getAttribute("javax.servlet.forward.query_string");
		return scheme + "://" +serverName + ":" + serverPort + uri + "?" + prmstr;
	}
	
	/**
	 * Returns the context init parameter key or defaultValue
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getParam(ServletContext context, String key, String defaultValue) {
		
		if( context == null )
			return defaultValue;
		
		String value = context.getInitParameter(key);
		if( value != null && value.isEmpty() == false )
			return value;
		else
			return defaultValue;
	}
	
	/**
	 * Returns the context init parameter key or defaultValue
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static long getParam(ServletContext context, String key, long defaultValue) {
		
		if( context == null )
			return defaultValue;
		
		String value = context.getInitParameter(key);
		if( value != null && value.isEmpty() == false )
			return Long.valueOf(value);
		else
			return defaultValue;
	}
	
	/**
	 * Returns the context init parameter key or defaultValue
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static double getParam(ServletContext context, String key, double defaultValue) {
		
		if( context == null )
			return defaultValue;
		
		String value = context.getInitParameter(key);
		if( value != null && value.isEmpty() == false )
			return Double.valueOf(value);
		else
			return defaultValue;
	}
}