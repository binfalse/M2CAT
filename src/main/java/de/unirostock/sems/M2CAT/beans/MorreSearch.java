package de.unirostock.sems.M2CAT.beans;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ctc.wstx.util.StringUtil;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.Config;
import de.unirostock.sems.morre.client.Morre;
import de.unirostock.sems.morre.client.dataholder.ModelResult;
import de.unirostock.sems.morre.client.exception.MorreCommunicationException;
import de.unirostock.sems.morre.client.exception.MorreException;
import de.unirostock.sems.morre.client.impl.HttpMorreClient;

public class MorreSearch implements Serializable {

	private static final long serialVersionUID = 3604277281309851087L;
	
	private Morre morre = null;
	
	private String request = null;
	private List<ModelResult> results = new LinkedList<ModelResult>();
	private String aggregationType = null;
	private String modelRankerWeight = "0";
	private String annotationRankerWeight = "0";
	private String personRankerWeight = "0";
	private String publicationRankerWeight = "0";
	
	public void doSearch() {
		
		// create Morre connector
		if( morre == null ) {
			try {
				morre = new HttpMorreClient( Config.getConfig().getMorreUrl() );
			} catch (MalformedURLException e) {
				LOGGER.error(e, "Cannot init morre connector, due to malformed URL");
				// TODO error message
				return;
			}
		}
		
		// check for empty search string
		if( request == null || request.isEmpty() ) {
			// TODO error message
			return;
		}
		
		// do the search
		try {
			results = morre.modelQuery(request);
		} catch (MorreCommunicationException | MorreException e) {
			LOGGER.error(e, "Cannot perfom morre search.");
			// TODO error message
			return;
		}
		
		if( results != null ) {
			// sort the results (just in case)
			Collections.sort( results );
		}
		else
			results = new LinkedList<ModelResult>();
	}
	
	public void doAggregatedSearch() {
		// create Morre connector
		if( morre == null ) {
			try {
				morre = new HttpMorreClient( Config.getConfig().getMorreUrl() );
			} catch (MalformedURLException e) {
				LOGGER.error(e, "Cannot init morre connector, due to malformed URL");
				// TODO error message
				return;
			}
		}
		
		// check for empty search string
		if( request == null || request.isEmpty() ) {
			// TODO error message
			return;
		}
		
		int rankersWeights = 0;
		
		if(aggregationType.equals("SUPERVISED_LOCAL_KEMENIZATION")){
			if(modelRankerWeight != null && !StringUtils.isEmpty(modelRankerWeight))
				rankersWeights += Integer.parseInt(modelRankerWeight);
			else
				rankersWeights += 5;
			if(annotationRankerWeight != null && !StringUtils.isEmpty(annotationRankerWeight))
				rankersWeights+= 100 * Integer.parseInt(annotationRankerWeight);
			else
				rankersWeights += 100 * 3;
			if(personRankerWeight != null && !StringUtils.isEmpty(personRankerWeight))
				rankersWeights+= 10000 * Integer.parseInt(personRankerWeight);
			else
				rankersWeights += 10000 * 1;
			if(publicationRankerWeight != null && !StringUtils.isEmpty(publicationRankerWeight))
				rankersWeights+= 1000000 * Integer.parseInt(publicationRankerWeight);
			else
				rankersWeights += 1000000 * 1;
		}
		
		// do the search
		try {
			results = morre.aggregatedModelQuery(request, aggregationType, Integer.toString(rankersWeights));
		} catch (MorreCommunicationException | MorreException e) {
			LOGGER.error(e, "Cannot perfom morre search.");
			// TODO error message
			return;
		}
		
		if( results != null ) { //TODO Unterscheide, ob Liste leer oder Fehler aufgetreten
			// sort the results (just in case)
			//Collections.sort( results );
		}
		else
			results = new LinkedList<ModelResult>();
	}

	public boolean isValid() {
		if( request != null && request.isEmpty() == false )
			return true;
		else
			return false;
	}
	
	public String getIdFromResult(ModelResult result) {
		return Base64.getEncoder().encodeToString( result.getDocumentURI().getBytes() );
	}
	
	// getter / setter
	
	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public List<ModelResult> getResults() {
		return results;
	}
	
	public int getResultCount() {
		return results != null ? results.size() : 0;
	}
	
	public String getAggregationType() {
		return aggregationType;
	}
	
	public void setAggregationType(String aggregationType){
		this.aggregationType = aggregationType;
	}
	
	public String getModelRankerWeight() {
		return modelRankerWeight;
	}

	public void setModelRankerWeight(String modelRankerWeight) {
		this.modelRankerWeight = modelRankerWeight;
	}

	public String getAnnotationRankerWeight() {
		return annotationRankerWeight;
	}

	public void setAnnotationRankerWeight(String annotationRankerWeight) {
		this.annotationRankerWeight = annotationRankerWeight;
	}

	public String getPersonRankerWeight() {
		return personRankerWeight;
	}

	public void setPersonRankerWeight(String personRankerWeight) {
		this.personRankerWeight = personRankerWeight;
	}

	public String getPublicationRankerWeight() {
		return publicationRankerWeight;
	}

	public void setPublicationRankerWeight(String publicationRankerWeight) {
		this.publicationRankerWeight = publicationRankerWeight;
	}


}
