package de.unirostock.sems.M2CAT.datamodel.information;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.unirostock.sems.M2CAT.datamodel.VCard;
import de.unirostock.sems.M2CAT.datamodel.meta.OmexMetaData;

public class PublicationInformation extends AdditionalFileInformation implements Serializable {
	
	private static final long serialVersionUID = 7842972641878106623L;
	
	protected URI pubmedUrn = null;
	protected String title = null;
	protected String abstractText = null;
	protected String journal = null;
	
	public PublicationInformation(String title, String abstractText, String journal, List<VCard> authors, Date published) {
		super();
		setTitle(title);
		this.abstractText = abstractText;
		this.journal = journal;
		
		metaData = new OmexMetaData();
		((OmexMetaData) metaData).setCreators(authors);
		((OmexMetaData) metaData).setCreated(published);
		((OmexMetaData) metaData).getModified().add(published);
	}

	public PublicationInformation() {
		super();
	}
	
	@Override
	public InputStream getSourceStream() throws IOException {
		
		StringBuilder text = new StringBuilder();
		
		text.append( title ).append("\n");
		for( int i = 1; i <= title.length(); i++ )
			text.append("=");
		text.append("\n\n");
		
		for( VCard author : getAuthors() ) {
			text.append("  *")
				.append( author.getGivenName() )
				.append(" ")
				.append( author.getFamilyName() )
				.append("* (")
				.append( author.getOrganization() )
				.append("), ");
		}
		text.append("\n");
		
		text.append("  published at: ")
			.append(journal);
		
		if( metaData != null && metaData instanceof OmexMetaData && ((OmexMetaData) metaData).getCreated() != null ) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime( ((OmexMetaData) metaData).getCreated() );
			
			text.append(", ")
				.append( calendar.get(Calendar.YEAR) );
		}
		
		text.append("\n\n");
		
		text.append("Abstract\n")
			.append("--------")
			.append("\n\n")
			.append(abstractText);
		
		return new ByteArrayInputStream( text.toString().getBytes() );
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		fileName = title.replaceAll("\\W", "_") + "_abstract.md";
	}

	public String getAbstractText() {
		return abstractText;
	}

	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
		
		if( metaData == null )
			metaData = new OmexMetaData();
		
		if( metaData instanceof OmexMetaData ) {
			((OmexMetaData) metaData).setDescription(abstractText);
		}
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public URI getPubmedUrn() {
		return pubmedUrn;
	}

	public void setPubmedUrn(URI pubmedUrn) {
		this.pubmedUrn = pubmedUrn;
	}

	public void setPublished(Date published) {
		if( metaData == null )
			metaData = new OmexMetaData();
		
		if( metaData instanceof OmexMetaData ) {
			((OmexMetaData) metaData).setCreated(published);
			((OmexMetaData) metaData).getModified().add(published);
		}
	}

	@JsonIgnore
	public List<VCard> getAuthors() {
		if( metaData == null )
			return null;
		else if( metaData instanceof OmexMetaData )
			return ((OmexMetaData) metaData).getCreators();
		else return null;
	}

	@JsonIgnore
	public void setAuthors(List<VCard> authors) {
		if( metaData == null )
			metaData = new OmexMetaData();
		
		if( metaData instanceof OmexMetaData )
			((OmexMetaData) metaData).setCreators(authors);
	}
	
}
