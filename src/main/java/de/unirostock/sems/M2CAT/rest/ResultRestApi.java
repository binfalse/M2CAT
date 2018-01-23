package de.unirostock.sems.M2CAT.rest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import de.unirostock.sems.M2CAT.ArchiveCache;
import de.unirostock.sems.M2CAT.RetrievalFactory;
import de.unirostock.sems.M2CAT.connector.RetrievalJob;
import de.unirostock.sems.M2CAT.datamodel.CachedArchiveInfo;
import de.unirostock.sems.M2CAT.datamodel.rest.StatusReport;

@Path("result")
public class ResultRestApi {
	
	@GET
	@Path("/{id}/status")
	@Produces( MediaType.APPLICATION_JSON )
	public Response getResponseState( @PathParam("id") String aid ) {
		
		if( aid == null || aid.isEmpty() )
			return buildErrorResponse(400, "No id was provided");
				
		ArchiveCache cache = ArchiveCache.getInstance();
		
		// check if id is in cache
		if( cache.isAvailable(aid) == false )
			return buildErrorResponse(400, "archive not available");
		
		StatusReport report = new StatusReport();
		CachedArchiveInfo archiveInfo = cache.get(aid);
		
		report.setState( archiveInfo.isFinished() ? StatusReport.FINISHED : StatusReport.PROCESSING );
		report.setStarted( archiveInfo.getDate() );
		
		int finishedJobs = 0;
		for( RetrievalJob job : archiveInfo.getJobs() ) {
			if( job.isFinished() )
				finishedJobs++;
		}
		report.setProcess( (double) finishedJobs / (double) archiveInfo.getJobs().size() );
		
		return Response.status(200).entity(report).build();
	}
	
	@GET
	@Path("/{id}/info")
	@Produces( MediaType.APPLICATION_JSON )
	public Response getInfo( @PathParam("id") String aid ) {
		
		if( aid == null || aid.isEmpty() )
			return buildErrorResponse(400, "No id was provided");
				
		ArchiveCache cache = ArchiveCache.getInstance();
		
		// check if id is in cache
		if( cache.isAvailable(aid) == false )
			return buildErrorResponse(400, "archive not available");
		
		CachedArchiveInfo archiveInfo = cache.get(aid);
		return Response.status(200).entity( archiveInfo.getArchiveInformation() ).build();
	}
	
	@GET
	@Path("/{id}/start")
	@Produces( MediaType.APPLICATION_JSON )
	public Response startRetrieval( @PathParam("id") String aid ) {
		
		if( aid == null || aid.isEmpty() )
			return buildErrorResponse(400, "No id was provided");
		
		RetrievalFactory.getInstance().startRetrieving(aid);
		
		Map<String, String> result = new HashMap<String, String>();
		result.put("status", "ok");
		return Response.status(200).entity(result).build();
	}
	
	/**
	 * Generates an error response.
	 *
	 * @param status the status
	 * @param errors the errors
	 * @return Response
	 */
	protected Response buildErrorResponse( int status, String... errors ) {

		// ResponseBuilder builder = Response.status(status);
		ResponseBuilder builder = Response.status(status);

		Map<String, Object> result = new HashMap<String, Object>();
		List<String> errorList = new LinkedList<String>();

		for( String error : errors ) {
			errorList.add( error );
		}

		result.put("status", "error");
		result.put("errors", errorList);

		return builder.entity(result).build();
	}

}
