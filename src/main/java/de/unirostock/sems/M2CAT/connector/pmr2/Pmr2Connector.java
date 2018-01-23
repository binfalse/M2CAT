package de.unirostock.sems.M2CAT.connector.pmr2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.connector.RetrievalConnector;
import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;
import de.unirostock.sems.M2CAT.datamodel.VCard;
import de.unirostock.sems.M2CAT.datamodel.information.AdditionalFileInformation;
import de.unirostock.sems.M2CAT.datamodel.information.ModelInformation;
import de.unirostock.sems.M2CAT.datamodel.meta.MetaData;
import de.unirostock.sems.M2CAT.datamodel.meta.OmexMetaData;

public class Pmr2Connector implements RetrievalConnector {

	private static Pattern modelFileNamePattern = Pattern.compile("^https?\\://(models\\.cellml\\.org|models\\.physiomeproject\\.org)/(([^/]*/)*)([^/]+\\.[\\w]+)");
	private static Map<String, File> gitCache = new HashMap<String, File>();

	protected static File getGit(String aid) {
		synchronized (gitCache) {
			return gitCache.get(aid);
		}
	}

	protected static void putGit(String aid, File tempDir) {
		synchronized (gitCache) {
			gitCache.put(aid, tempDir);
		}
	}

	// ----------------------------------------
	
	// took a shit load of code from webCAT
	//   http://sems.uni-rostock./trac/combinearchive-web
	
	private File tempDir = null;
	private Git repo = null;
	private ArchiveInformation archiveInfo = null;
	private String modelFileName = null;

	@Override
	public void startRetriving(ArchiveInformation archiveInformation) {
		archiveInfo = archiveInformation;
		
		// check if link points to cellML/PMR2
		String modelLink = new String(Base64.getUrlDecoder().decode( archiveInfo.getId() ));
		if( modelLink.matches("^https?\\://(models\\.cellml\\.org|models\\.physiomeproject\\.org)/.*$") ) {
			// prozess Links
			try {
				String repoLink = processNzRepoLink(modelLink + "/view");
				archiveInfo.setRepositoryUrl(repoLink);
				
				// get model filename
				modelFileName = getModelFileName(modelLink);
				
				cloneGit();
				scanRepository(tempDir);
				
			}
			catch (Pmr2Exception e) {
				LOGGER.error(e, "Exception in PMR2 Importer");
			}
		}
		
	}
	
	private String getModelFileName( String modelLink ) {
		
		Matcher matcher = modelFileNamePattern.matcher(modelLink);
		if( matcher.find() )
			return matcher.group(4);
		
		return null;
	}
	
	private void cloneGit() throws Pmr2Exception {

		// get a temp dir
		try {
			tempDir = getGit( archiveInfo.getRepositoryUrl() );
			if( tempDir == null || !tempDir.isDirectory () ) {
				tempDir = Files.createTempDirectory("", PosixFilePermissions.asFileAttribute( PosixFilePermissions.fromString("rwx------") ) ).toFile();
				if( !tempDir.isDirectory () && !tempDir.mkdirs() )
					throw new Pmr2Exception("The temporary directories could not be created: " + tempDir.getAbsolutePath ());
				putGit( archiveInfo.getRepositoryUrl(), tempDir );
				tempDir.deleteOnExit();
			}
			else {
				updateGit();
				return;
			}
		} catch (IOException e1) {
			LOGGER.error(e1, "Cannot create temp directory");
			throw new Pmr2ImporterException("Cannot create temp directory.", e1);
		}

		try {
			// clone the repo
			repo = Git.cloneRepository()
					.setURI( archiveInfo.getRepositoryUrl() )
					.setDirectory( tempDir )
					.setCloneSubmodules(true)		// include all submodules -> important for PMR2-Project
					.call();

		} catch (GitAPIException e) {
			LOGGER.error(e, "Cannot clone git repository: ", archiveInfo.getRepositoryUrl());
			throw new Pmr2ImporterException("Exception while cloning git archiv", e);
		}

	}
	
	private void updateGit() throws Pmr2Exception {
		
		if( tempDir == null || !tempDir.isDirectory() )
			throw new Pmr2Exception("temp dir is not existing");
		
		try {
			repo = Git.open(tempDir);
			repo.pull();
		} catch (IOException e) {
			LOGGER.error(e, "Cannot pull git repository: ", archiveInfo.getRepositoryUrl());
			throw new Pmr2ImporterException("Exception while pulling git archiv", e);
		}
		
	}

	private void scanRepository( File directory ) throws Pmr2ImporterException {

		String[] dirContent = directory.list();
		for(int index = 0; index < dirContent.length; index++ ) {
			File entry = new File( directory, dirContent[index] );

			if( entry.isDirectory() && entry.exists() && !entry.getName().startsWith(".") ) {
				// Entry is a directory and not hidden (begins with a dot) -> recursive
				scanRepository(entry);
			}
			else if( entry.isFile() && entry.exists() ) {
				
				// skip .DS_Store files
				if( ".DS_Store".equals(FilenameUtils.getName(entry.toString())) )
					continue;
				
				// Entry is a file
				// make Path relative
				Path relativePath = tempDir.toPath().relativize( entry.toPath() );
				
				// check if main model, or not
				if( relativePath.toString().equals(modelFileName) ) {
					
					ModelInformation modelInfo = archiveInfo.getModel();
					if( modelInfo == null ) {
						modelInfo = new ModelInformation();
						archiveInfo.setModel(modelInfo);
					}
					
					modelInfo.setDocumentURI(entry.toURI());
					modelInfo.setModelName( relativePath.toString() );
					modelInfo.setMetaData( getOmexForFile(relativePath) );
					
				}
				else {
					// add file and scan log for omex description
					archiveInfo.addAdditionalFile(
							new AdditionalFileInformation(relativePath.toString(), entry.toURI(), getOmexForFile(relativePath) )
							);
				}
			}
		}

	}

	private MetaData getOmexForFile( Path relativePath ) throws Pmr2ImporterException {
		LinkedHashSet<ImportVCard> contributors = new LinkedHashSet<ImportVCard>();
		List<Date> modified = new LinkedList<Date>();
		Date created = null;

		try {
			Iterable<RevCommit> commits = repo.log()
					.addPath( relativePath.toString() )
					.call();

			for( RevCommit current : commits ) {
				// add person
				contributors.add( new ImportVCard(current.getAuthorIdent()) );

				// add time stamp
				Date timeStamp = current.getAuthorIdent().getWhen(); 
				modified.add( timeStamp );

				// set created time stamp
				if( created == null || timeStamp.before(created) )
					created = timeStamp;
			}

		} catch (GitAPIException e) {
			LOGGER.error(e, "Error while getting log for file");
			throw new Pmr2ImporterException("Error while getting file log", e);
		}

		OmexMetaData omex = new OmexMetaData();
		omex.setCreated(created);
		omex.setCreators( new ArrayList<VCard>( contributors ) );
		omex.setModified(modified);

		return omex; 
	}
	
	private String processNzRepoLink (String link) throws Pmr2ImporterException {
		
		/*
		 * cellml feature 1:
		 * 
		 * hg path for exposures such as 
		 * http://models.cellml.org/exposure/2d0da70d5253291015a892326fa27b7b/aguda_b_1999.cellml/view
		 * http://models.cellml.org/e/4c/goldbeter_1991.cellml/view
		 * is
		 * http://models.cellml.org/workspace/aguda_b_1999
		 * http://models.cellml.org/workspace/goldbeter_1991
		 */
		if ((link.toLowerCase().contains("cellml.org/e") || link.toLowerCase().contains("physiomeproject.org/e")))
		{
			LOGGER.debug ("apparently got an exposure url: ", link);
			InputStream in = null;
			try {
				in = new URL (link).openStream();
			} catch (IOException e1) {
				LOGGER.error("Got a malformed URL to hg clone: ", link);
				throw new Pmr2ImporterException("Got a malformed URL", e1);
			}
			
			try {
				String source = IOUtils.toString (in);
				Pattern hgClonePattern = Pattern.compile ("<input [^>]*value=.git clone ([^'\"]*). ");
				Matcher matcher = hgClonePattern.matcher (source);
				if (matcher.find()) {
					link = matcher.group (1);
					LOGGER.debug ("resolved exposure url to: ", link);
				}
			} catch (IOException e) {
				LOGGER.warn (e, "failed to retrieve cellml exposure source code");
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		/*
		 * cellml feature 2:
		 * 
		 * hg path for files such as 
		 * http://models.cellml.org/workspace/aguda_b_1999/file/56788658c953e1d0a6bc745b81bdb0c0c20e9821/aguda_1999_bb.ai
		 * is
		 * http://models.cellml.org/workspace/aguda_b_1999
		 */
		if ((link.toLowerCase().contains("cellml.org/workspace/") || link.toLowerCase().contains("physiomeproject.org/workspace/")) && link.toLowerCase().contains("/file/"))
		{
			LOGGER.debug ("apparently got an cellml/physiome file url: ", link);
			int pos = link.indexOf ("/file/");
			link = link.substring (0, pos);
			LOGGER.debug ("resolved file url to: ", link);
		}
		
		// now we assume it is a link to a workspace, which can be hg-cloned.
		return link;
	}

	/**
	 * Wrapper class for VCard datamodel,
	 * adding equals and hashCode for better
	 * determination in HashMaps/-Sets
	 *
	 */
	protected static class ImportVCard extends VCard {

		private static final long serialVersionUID = 1512465714255928601L;

		/**
		 * Generates a VCard from a JGit PersonIdent.
		 *
		 * @param person the person
		 */
		public ImportVCard( PersonIdent person ) {
			super(	GitNameTransformer.getFamilyName( person.getName() ),
					GitNameTransformer.getGivenName( person.getName() ),
					person.getEmailAddress(),
					"" );
		}

		/**
		 * Generates a VCard from a default mail string <br><br>
		 * 
		 * Given-Name Family-Name &lt;mail@example.org&gt; <br>
		 * Given-Name Family-Name
		 *
		 * @param MailUserString the Mail user string
		 */
		public ImportVCard( String MailUserString ) {
			super(	DefaultNameTransformer.getFamilyName( MailUserString ),
					DefaultNameTransformer.getGivenName( MailUserString ),
					DefaultNameTransformer.getEmail( MailUserString ),
					"" );
		}

		public int hashCode() {

			if( getEmail() == null && getFamilyName() == null && getGivenName() == null && getOrganization() == null )
				return 0;

			int hash = 1;
			hash = hash * 17 + (getEmail() == null ? 0 : getEmail().hashCode());
			hash = hash * 31 + (getFamilyName() == null ? 0 : getFamilyName().hashCode());
			hash = hash * 13 + (getGivenName() == null ? 0 : getGivenName().hashCode());
			hash = hash * 07 + (getOrganization() == null ? 0 : getOrganization().hashCode());

			return hash;
		}

		public boolean equals(Object obj) {

			if( (obj instanceof ImportVCard || obj instanceof VCard) && obj.hashCode() == hashCode() )
				return true;
			else
				return false;

		}
	}

	protected static class GitNameTransformer {
		protected static String getGivenName( String name ) {
			if( name == null || name.isEmpty() )
				return "";

			String[] splitted = splitIt(name);
			if( splitted[0] == null || splitted[0].isEmpty() )
				return "";
			else
				return splitted[0];
		}

		protected static String getFamilyName( String name ) {
			if( name == null || name.isEmpty() )
				return "";

			String[] splitted = splitIt(name);
			if( splitted[0] == null || splitted[0].isEmpty() )
				return name;
			else if( splitted.length < 2 || splitted[1] == null || splitted[1].isEmpty() )
				return "";
			else
				return splitted[1];
		}

		private static String[] splitIt( String name ) {
			return name.split("\\s+", 2);
		}
	}

	protected static class DefaultNameTransformer {

		private static Pattern namePattern = Pattern.compile("((\\w+\\s+)+)(\\w+)");
		private static Pattern mailPattern = Pattern.compile("<?(\\w+@\\w+\\.\\w+)>?");

		protected static String getGivenName( String name ) {
			Matcher matcher = namePattern.matcher(name);
			if( matcher.find() ) {
				String result = matcher.group(1);
				return result != null && result.isEmpty() == false ? result.trim() : "";
			}
			else
				return "";
		}

		protected static String getFamilyName( String name ) {
			Matcher matcher = namePattern.matcher(name);
			if( matcher.find() ) {
				for( int i = 0; i < matcher.groupCount(); i++ )
					System.out.println( matcher.group(i) );
				String result = matcher.group(3);
				return result != null && result.isEmpty() == false ? result.trim() : "";
			}
			else
				return "";
		}

		protected static String getEmail( String name ) {
			Matcher matcher = mailPattern.matcher(name);
			if( matcher.find() ) {
				String result = matcher.group(1);
				return result != null && result.isEmpty() == false ? result.trim() : "";
			}
			else
				return "";
		}
	}

}
