package de.unirostock.sems.M2CAT.connector.masymos;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Values;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.Config;
import de.unirostock.sems.M2CAT.connector.RetrievalConnector;
import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;
import de.unirostock.sems.M2CAT.datamodel.VCard;
import de.unirostock.sems.M2CAT.datamodel.information.ModelInformation;
import de.unirostock.sems.M2CAT.datamodel.information.PublicationInformation;

public class MasymosConnector implements RetrievalConnector {

	private Config config = null;
	private Session session = null;

	@Override
	public void startRetriving(ArchiveInformation archiveInformation) {
		config = Config.getConfig();
		session = config.getDatabaseSession();

		getBasicModelMeta(archiveInformation);
		getPublicationInformation(archiveInformation);

		session.close();

	}

	private void getBasicModelMeta(ArchiveInformation archiveInformation) {

		// Get modelName and modelId (not this id, used to identify different models, more the xml-node id)
		String query = "Match (d:DOCUMENT)-->(m:MODEL) Where d.URI={1} Return m.NAME as NAME, m.ID as MID";
		try {
			// set URI as identifier
			String url = new String(Base64.getDecoder().decode(
					archiveInformation.getId()));

			StatementResult result = session.run(query, Values.parameters("1", url));
			if( result.hasNext() ) {
				ModelInformation modelInfo = archiveInformation.getModel();		// returns an empty Model obj, if it wasn't set yet
				try {
					modelInfo.setDocumentURI(new URI(url));
				}
				catch (URISyntaxException e) {
					LOGGER.warn(e, "Cannot parse model url.");
					modelInfo.setDocumentURI(null);
				}
				
				Record record = result.next();
				String modelId = record.get("MID").asString();
				modelInfo.setModelId( modelId == null || modelId.isEmpty() ? null : modelId );

				String name = record.get("NAME").asString();
				modelInfo.setModelName( name == null || name.isEmpty() ? null : name );
			}
		} catch (Exception e) {
			LOGGER.error(e, "Not able to gather basic model information for ", archiveInformation.getId() );
		}

	}

	private void getPublicationInformation(ArchiveInformation archiveInformation) {

		// Get publication information
		String query = "Match (d:DOCUMENT)-->(m:MODEL)-->(a:ANNOTATION)-->(p:PUBLICATION) Where d.URI={1} Return p.PUBID as PUBID, p.ABSTRACT as ABSTRACT, p.TITLE as TITLE, p.YEAR as YEAR, p.JOURNAL as JOURNAL, id(p) as PID";

		try {
			// set URI as identifier
			String url = new String(Base64.getDecoder().decode(
					archiveInformation.getId()));

			StatementResult result = session.run(query, Values.parameters("1", url));

			while (result.hasNext()) {
				// add all publications to the dataholder
				PublicationInformation pub = new PublicationInformation();

				Record record = result.next();
				String title = record.get("TITLE").asString();
				pub.setTitle(title == null || title.isEmpty() ? null : title);

				String journal = record.get("JOURNAL").asString();
				pub.setJournal(journal == null || journal.isEmpty() ? null
						: journal);

				String abstractText = record.get("ABSTRACT").asString();
				pub.setAbstractText(abstractText == null
						|| abstractText.isEmpty() ? null : abstractText);

				int year = record.get("YEAR").asInt();
				if (year > 0) {
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, year);
					pub.setPublished(cal.getTime());
				}

				try {
					String pubmed = record.get("PUBID").asString();
					pub.setPubmedUrn(pubmed == null || pubmed.isEmpty() ? null
							: new URI(pubmed));
				} catch (URISyntaxException e) {
					LOGGER.warn(e, "Cannot parse pubmed urn.");
					pub.setFileSource(null);
				}

				int pid = record.get("PID").asInt();
				getPersonFromPublication(pid, pub);

				archiveInformation.addAdditionalFile(pub);
			}

		} catch (Exception e) {
			LOGGER.error(e, "Not able to gather publications for ", archiveInformation.getId() );
		}

	}

	private void getPersonFromPublication(int pubId, PublicationInformation pub) {

		String query = "Match (p:PUBLICATION)-[*0..2]->(n:PERSON) Where id(p)={1} Return n.GIVENNAME as GIVENNAME, n.FAMILYNAME as FAMILYNAME, n.ORGANIZATION as ORGANIZATION, n.EMAIL as EMAIL";
		try {
			// set ID
			StatementResult result = session.run(query, Values.parameters("1", pub));

			List<VCard> persons = new LinkedList<VCard>();
			while (result.hasNext()) {
				Record record = result.next();

				String givenName = record.get("GIVENNAME").asString();
				String familyName = record.get("FAMILYNAME").asString();
				String organization = record.get("ORGANIZATION").asString();
				String email = record.get("EMAIL").asString();

				VCard vcard = new VCard(familyName, givenName, email,
						organization);
				if (vcard.isSufficient() == false)
					persons.add(vcard);
			}

			// apply authors
			pub.setAuthors(persons);

		} catch (Exception e) {
			LOGGER.error(e, "Not able to gather persons for publication nodeId: ", pubId );
		}

	}

}
