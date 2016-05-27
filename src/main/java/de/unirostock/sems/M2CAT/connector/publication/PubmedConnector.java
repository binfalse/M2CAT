package de.unirostock.sems.M2CAT.connector.publication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ebi.miriam.lib.MiriamLink;
import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.connector.RetrievalConnector;
import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;
import de.unirostock.sems.M2CAT.datamodel.information.AdditionalFileInformation;
import de.unirostock.sems.M2CAT.datamodel.information.PublicationInformation;
//TODO import MiriamJavaLib_standalone-1.1.6.jar

public class PubmedConnector implements RetrievalConnector {

	@Override
	public void startRetriving(ArchiveInformation archiveInformation) {
		
		// retrieving all Publications
		List<PublicationInformation> todo = new LinkedList<PublicationInformation>();
		for( AdditionalFileInformation file : archiveInformation.getAdditionFiles() ) {
			if( file instanceof PublicationInformation )
				todo.add((PublicationInformation) file);
		}
		
		// actually retrieving the paper
		for( PublicationInformation pubInfo : todo ) {
			stuff( pubInfo );
		}

	}

	private void stuff(PublicationInformation pubInfo)
	{
		// Creation of the link to the Web Services
		MiriamLink link = new MiriamLink();
		String[] webadress = null;
		String singlewebadress = null;

		String websiteinformation = null;
		
		//Define Patterns

		//Pattern if Pubmed.gov is in webadress
		String regexpubmedadress = "http://www.ncbi.nlm.nih.gov(.+)";

		//Pattern for the abstract only Pubmed.gov
		String regexabstract = "<Abs.*>(.+)</Abs.{0,10}>";

		//Pattern for the title only Pubmed.gov
		String regextitle = "<h1>(.+)</h1>";

		//No need
		//Pattern for the authors only Pubmed.gov
		//String regexauthor = "div class=\"auths\">.*>(.*)</a>(<sup>|,).*<div class=\"abstr\">";

		//Pattern for the pubyear only Pubmed.gov
		String regexpubyear = "<div class=\"cit\">.*</a> ([0-9][0-9][0-9][0-9]).*</div>";

		
		// Sets the address to access the Web Services
		link.setAddress("http://www.ebi.ac.uk/miriamws/main/MiriamWebServices");

		/*Hardcoded webadresses
		//webadress = link.getLocations("urn:miriam:pubmed:11429446");
		//System.out.println(webadress[0] + "\n");

		//alternative locations

		//webadress = link.getLocations("urn:miriam:pubmed:11923843");
		//System.out.println(webadress[0] + "\n");

		//webadress = link.getLocations("urn:miriam:pubmed:12050011");
		//System.out.println(webadress[0] + "\n");

		//webadress = link.getLocations("urn:miriam:pubmed:16007500");
		//System.out.println(webadress[0] + "\n");
		*/


		webadress = link.getLocations(pubInfo.getPubmedUrn().toString());


		Pattern pattern = Pattern.compile(regexpubmedadress);
		Matcher matcher = null;
		
		for (int i = 0; i < webadress.length; i++) {
			matcher = pattern.matcher(webadress[i]);
			if(matcher.find()==true){
				singlewebadress = webadress[i];
				break;
			}
		}
		

		//get the html as a string
		try {
			websiteinformation = savePage(singlewebadress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e, "no webadress");
			return;
		}
		
		if( websiteinformation == null || websiteinformation.isEmpty() )
			return;
		
		//List all locations saved in the urn


//		for (String loc: webadress)
//		{
//			LOGGER.debug("\t- ", loc);
//		}


		//String updated = websiteinformation.replaceAll(regex, "$2");

		//System.out.println(updated);

		//find the abstract
		pattern = Pattern.compile(regexabstract);
		matcher = pattern.matcher(websiteinformation);
		matcher.find();
		String abstr = matcher.group(1);
		pubInfo.setAbstractText(abstr);

//		LOGGER.debug("abstract: ", matcher.group(1));

		//find the title
		pattern = Pattern.compile(regextitle);
		matcher = pattern.matcher(websiteinformation);
		matcher.find();
		String title = matcher.group(1);
		pubInfo.setTitle(title);

//		LOGGER.debug("title: ", matcher.group(1));


		//find the pubyear
		pattern = Pattern.compile(regexpubyear);
		matcher = pattern.matcher(websiteinformation);
		matcher.find();
		String pubyear = matcher.group(1);

		Calendar cal = Calendar.getInstance();
		cal.set( Integer.parseInt(pubyear), 0, 0);
		pubInfo.setPublished( cal.getTime() );

//		LOGGER.debug("pubyear: ", matcher.group(1));

	}



	public static String savePage(final String URL) throws IOException {
		String line = "", all = "";
		URL myUrl = null;
		BufferedReader in = null;
		try {
			myUrl = new URL(URL);
			in = new BufferedReader(new InputStreamReader(myUrl.openStream()));

			while ((line = in.readLine()) != null) {
				all += line;
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		return all;
	}

}