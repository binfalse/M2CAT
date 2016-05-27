package de.unirostock.sems.M2CAT.datamodel.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.unirostock.sems.M2CAT.datamodel.VCard;
import de.unirostock.sems.cbarchive.meta.MetaDataObject;
import de.unirostock.sems.cbarchive.meta.OmexMetaDataObject;
import de.unirostock.sems.cbarchive.meta.omex.OmexDescription;

public class OmexMetaData extends MetaData implements Serializable {
	
	private static final long serialVersionUID = -2315543576852484372L;
	
	private List<VCard> creators = new ArrayList<VCard>();
	private Date created = null;
	private List<Date> modified = new ArrayList<Date>();
	private String description = null;
	
	public List<VCard> getCreators() {
		return creators;
	}
	public void setCreators(List<VCard> creators) {
		this.creators = creators;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public List<Date> getModified() {
		return modified;
	}
	public void setModified(List<Date> modified) {
		this.modified = modified;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	@JsonIgnore
	public MetaDataObject getCombineArchiveMetaData() {
		
		List<de.unirostock.sems.cbarchive.meta.omex.VCard> omexCreators = new ArrayList<de.unirostock.sems.cbarchive.meta.omex.VCard>();
		for( VCard person : creators ) {
			omexCreators.add( (de.unirostock.sems.cbarchive.meta.omex.VCard) person );
		}
		
		OmexDescription omex = new OmexDescription( omexCreators, modified, created, description );
		return new OmexMetaDataObject(omex);
		
	}
}
