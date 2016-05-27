package de.unirostock.sems.M2CAT.connector.bmdb;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.connector.RetrievalConnector;
import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;
import de.unirostock.sems.M2CAT.datamodel.information.AdditionalFileInformation;


public class BmdbConnector implements RetrievalConnector {

	private static Pattern bmdbIdPattern = Pattern.compile("BIOMD[0-9]{10}");

	public void startRetriving(ArchiveInformation archiveInformation) {

		String bmdbId = new String(Base64.getDecoder().decode( archiveInformation.getId() ));
		Matcher matcher = bmdbIdPattern.matcher(bmdbId);
		if( matcher.find() == false )
			return;

		bmdbId = matcher.group(0);
		try {
			URI modelUri = new URI("https://www.ebi.ac.uk/biomodels-main/download?mid=" + bmdbId);
			URI pngUri = new URI ("https://www.ebi.ac.uk/biomodels-main/download?mid=" + bmdbId + "&format=PNG");
			URI pdfUri = new URI ("https://www.ebi.ac.uk/biomodels-main/download?mid=" + bmdbId + "&format=PDF");
			
			String name = archiveInformation.getModel().getModelId();
			if( name == null || name.isEmpty() )
				name = bmdbId;
			
			if(getResponseCode(modelUri)==200){
				archiveInformation.getModel().setDocumentURI(modelUri);
			}

			if(getResponseCode(pdfUri)==200){
				AdditionalFileInformation modelpdf = new AdditionalFileInformation();
				modelpdf.setFileName( name + ".pdf" );
				modelpdf.setFileSource(pdfUri);
				archiveInformation.addAdditionalFile(modelpdf);
			}

			if(getResponseCode(pngUri)==200){
				AdditionalFileInformation modelpng = new AdditionalFileInformation();
				modelpng.setFileName( name + ".png" );
				modelpng.setFileSource(pngUri);
				archiveInformation.addAdditionalFile(modelpng);
			}

		} catch (URISyntaxException e) {
			LOGGER.error(e, "Error while generation URI");
			return;
		} catch (IOException e) {
			LOGGER.error(e, "IOException while checking BioModels files.");
		}
	}

	public static int getResponseCode(URI url) throws IOException {
		HttpURLConnection connection =  (HttpURLConnection)  url.toURL().openConnection(); 
		connection.setRequestMethod("HEAD"); 
		connection.connect(); 
		return connection.getResponseCode();
	}
}

