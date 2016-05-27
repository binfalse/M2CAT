package de.unirostock.sems.M2CAT.datamodel.meta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.unirostock.sems.cbarchive.meta.MetaDataObject;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" )
@JsonSubTypes({
	@Type( value = OmexMetaData.class, name = MetaData.META_OMEX )
})
public abstract class MetaData implements Serializable {
	
	public static final String META_OMEX = "omex";
	
	private static final long serialVersionUID = 6816444526200069670L;
	
	@JsonIgnore
	public abstract MetaDataObject getCombineArchiveMetaData();
	
}
