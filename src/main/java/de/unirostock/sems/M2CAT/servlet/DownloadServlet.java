package de.unirostock.sems.M2CAT.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.unirostock.sems.M2CAT.ArchiveCache;
import de.unirostock.sems.M2CAT.RetrievalFactory;
import de.unirostock.sems.M2CAT.connector.RetrievalJob;
import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;

public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = -1026948523012585498L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// splitting request URL
		String[] requestUrl =  request.getRequestURI().substring(request.getContextPath().length()).split ("/");

		if( requestUrl.length >= 3 && requestUrl[2] != null ) {
			String aid = requestUrl[2];
			RetrievalFactory factory = RetrievalFactory.getInstance();
			ArchiveCache cache = ArchiveCache.getInstance();

			// Retrieve if not done yet
			if( cache.isAvailable(aid) == false )
				factory.startRetrieving(aid, RetrievalJob.MAX_PRIORITY);
			File archive = factory.assemble(aid);

			if( archive == null )
				return;

			ArchiveInformation info = cache.get(aid).getArchiveInformation();

			// set MIME-Type to something downloadable
			response.setContentType("application/octet-stream");

			// set the filename of the downloaded file
			response.addHeader("Content-Disposition", MessageFormat.format("inline; filename=\"{0}.{1}\"", info.getModel().getModelName(), "omex") );
			
			// set ContentLength (FileSize)
			response.addHeader("Content-Length", String.valueOf(archive.length()) );
			response.setContentLength( (int) archive.length() );
			
			OutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(archive);
			
			// push it to the socket
			IOUtils.copy(input, output);
			
			// flush'n'close
			output.flush();
			output.close();
			input.close();
			
			response.flushBuffer();
					
		}

	}

}
