package de.unirostock.sems.M2CAT.connector;

import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;

public interface RetrievalConnector {
	
	public void startRetriving( ArchiveInformation archiveInformation );
	
}
