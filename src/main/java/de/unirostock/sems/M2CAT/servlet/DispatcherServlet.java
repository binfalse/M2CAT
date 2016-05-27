package de.unirostock.sems.M2CAT.servlet;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.ArchiveCache;
import de.unirostock.sems.M2CAT.Config;
import de.unirostock.sems.M2CAT.RetrievalFactory;
import de.unirostock.sems.M2CAT.executor.PriorityThreadPoolExecutor;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -7998373104245094795L;
	
	public void init(javax.servlet.ServletConfig config) throws ServletException {
		// place to init stuff
		Config.loadConfig(config.getServletContext());
		
		// init singletons
		ArchiveCache.getInstance();
		RetrievalFactory.getInstance();
		
	}
	
	public void destroy() {
		// place to save stuff to disk, before exit
		PriorityThreadPoolExecutor executor = Config.getConfig().getExecutor();
		executor.shutdown();
		try {
			while( executor.awaitTermination(5, TimeUnit.SECONDS) == false );
		} catch (InterruptedException e) {
			LOGGER.warn(e, "Interrupt Exception while waiting for ThreadPool termination");
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		
		String path = req.getRequestURI();
		String contextPath = req.getContextPath();
		if( path.startsWith(contextPath) )
			path = path.substring( contextPath.length() );
		
		String redirect = "/index.jsp";
		
		switch (path) {
		case "/":
			redirect = "/index.jsp";
			break;
			
		case "/search":
			redirect = "/searchresults.jsp";
			break;
			
		case "/model":
			redirect = "/modelresult.jsp";
			break;
			
		case "/about":
		case "/impress":
//			redirect = "/impress.jsp";
			break;
			
		default: 
			req.setAttribute("de.unirostock.sems.M2CAT.error", "notfound");
//			redirect = "/error.jsp";
			break;
		}
		
		req.getRequestDispatcher(redirect).forward(req, resp); 
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
	
	
}
