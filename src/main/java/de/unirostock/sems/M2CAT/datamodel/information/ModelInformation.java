package de.unirostock.sems.M2CAT.datamodel.information;

import java.io.Serializable;
import java.net.URI;

import de.unirostock.sems.M2CAT.datamodel.meta.MetaData;

public class ModelInformation implements Serializable {
	
	private static final long serialVersionUID = -8826113081961908973L;
	
	protected String modelName = null;
	protected String modelId = null;
	protected URI documentURI = null;
	protected MetaData metaData = null;
	
	public ModelInformation(String modelName, String modelId, URI documentURI, MetaData metaData) {
		super();
		this.modelName = modelName;
		this.modelId = modelId;
		this.documentURI = documentURI;
		this.metaData = metaData;
	}
	
	public ModelInformation() {
		super();
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public URI getDocumentURI() {
		return documentURI;
	}

	public void setDocumentURI(URI documentURI) {
		this.documentURI = documentURI;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}
	
}
