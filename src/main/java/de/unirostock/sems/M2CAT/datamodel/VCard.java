package de.unirostock.sems.M2CAT.datamodel;

import java.io.Serializable;

import org.jdom2.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VCard extends de.unirostock.sems.cbarchive.meta.omex.VCard implements Serializable {
	
	private static final long serialVersionUID = 8307573033389580175L;

	public VCard() {
		super();
	}

	public VCard(Element element) {
		super(element);
	}

	public VCard(String familyName, String givenName, String email, String organization) {
		super(familyName, givenName, email, organization);
	}
	
	@JsonIgnore
	public boolean isEmpty() {
		return super.isEmpty();
	}
	
	/**
	 * Checks if given and family name is set
	 * 
	 * @return
	 */
	@JsonIgnore
	public boolean isSufficient() {
		return (getFamilyName() == null || getFamilyName().length () < 1)
				&& (getGivenName() == null || getGivenName().length () < 1);
	}
}
