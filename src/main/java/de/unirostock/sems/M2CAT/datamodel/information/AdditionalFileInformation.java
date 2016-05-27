package de.unirostock.sems.M2CAT.datamodel.information;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import de.unirostock.sems.M2CAT.datamodel.meta.MetaData;
import de.unirostock.sems.M2CAT.datamodel.meta.OmexMetaData;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" )
@JsonSubTypes({
	@Type( value = AdditionalFileInformation.class, name = AdditionalFileInformation.FILE_NORMAL ),
	@Type( value = PublicationInformation.class, name = AdditionalFileInformation.FILE_PUBLICATION )
})
public class AdditionalFileInformation implements Serializable {
	
	public static final String FILE_NORMAL = "file";
	public static final String FILE_PUBLICATION = "publication";
	
	private static final long serialVersionUID = 8123505807217421757L;
	
	protected String fileName = null;
	protected URI fileSource = null;
	protected MetaData metaData = null;
	
	public AdditionalFileInformation(String fileName, URI fileSource,
			MetaData metaData) {
		super();
		this.fileName = fileName;
		this.fileSource = fileSource;
		this.metaData = metaData;
	}

	public AdditionalFileInformation() {
		super();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public URI getFileSource() {
		return fileSource;
	}

	public void setFileSource(URI fileSource) {
		this.fileSource = fileSource;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(OmexMetaData metaData) {
		this.metaData = metaData;
	}
	
	@JsonIgnore
	public InputStream getSourceStream() throws IOException {
		if( fileSource == null )
			throw new IOException("No source URL is null.");
		
		return fileSource.toURL().openStream();
	}
	
}
