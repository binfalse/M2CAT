package de.unirostock.sems.M2CAT.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.unirostock.sems.M2CAT.datamodel.information.AdditionalFileInformation;
import de.unirostock.sems.M2CAT.datamodel.information.ModelInformation;

public class ArchiveInformation implements Serializable {
	
	private static final long serialVersionUID = -9155899700765005927L;
	
	public static abstract class REPOSITORY_TYPE {
		public static final String NONE = "none";
		public static final String GIT = "git";
		public static final String HTTP = "http";
	}
	
	protected String id = null;
	
	protected ModelInformation model = null;
	protected List<AdditionalFileInformation> additionFiles = new ArrayList<AdditionalFileInformation>();
	
	protected String repositoryUrl = null;
	protected String repositoryType = REPOSITORY_TYPE.NONE;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ModelInformation getModel() {
		// create empty model data holder
		if( model == null )
			this.model = new ModelInformation();
		
		return model;
	}
	public void setModel(ModelInformation model) {
		this.model = model;
	}
	public List<AdditionalFileInformation> getAdditionFiles() {
		return additionFiles;
	}
	public void addAdditionalFile(AdditionalFileInformation additionalFile) {
		additionFiles.add(additionalFile);
	}
	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}
	public String getRepositoryType() {
		return repositoryType;
	}
	public void setRepositoryType(String repositoryType) {
		this.repositoryType = repositoryType;
	}
	public void setAdditionFiles(List<AdditionalFileInformation> additionFiles) {
		this.additionFiles = additionFiles;
	}
	
}
