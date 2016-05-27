package de.unirostock.sems.M2CAT;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.M2CAT.connector.RetrievalJob;
import de.unirostock.sems.M2CAT.connector.bmdb.BmdbConnector;
import de.unirostock.sems.M2CAT.connector.masymos.MasymosConnector;
import de.unirostock.sems.M2CAT.connector.pmr2.Pmr2Connector;
import de.unirostock.sems.M2CAT.connector.publication.PubmedConnector;
import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;
import de.unirostock.sems.M2CAT.datamodel.CachedArchiveInfo;
import de.unirostock.sems.M2CAT.datamodel.information.AdditionalFileInformation;
import de.unirostock.sems.M2CAT.datamodel.information.ModelInformation;
import de.unirostock.sems.M2CAT.executor.PriorityThreadPoolExecutor;
import de.unirostock.sems.cbarchive.ArchiveEntry;
import de.unirostock.sems.cbarchive.CombineArchive;
import de.unirostock.sems.cbarchive.CombineArchiveException;
import de.unirostock.sems.cbext.Formatizer;

public class RetrievalFactory {
	
	private static volatile RetrievalFactory instance = null;
	
	public static RetrievalFactory getInstance() {
		
		// check synchronized, if instance is already present
		if( instance == null )
			instance = new RetrievalFactory();
		
		return instance;
	}
	
	// ----------------------------------------
	
	public static final int DEFAULT_PRIORITY = (RetrievalJob.MAX_PRIORITY - RetrievalJob.MIN_PRIORITY)/2;
	
	private Config config = null;
	private PriorityThreadPoolExecutor executor = null;
	private ArchiveCache cache = null;
	
	/**
	 * default private constructor
	 * 
	 */
	private RetrievalFactory() {
		config = Config.getConfig();
		executor = config.getExecutor();
		cache = ArchiveCache.getInstance();
	}
	
	public CachedArchiveInfo startRetrieving( String aid ) {
		return startRetrieving(aid, DEFAULT_PRIORITY);
	}
	
	public CachedArchiveInfo startRetrieving( String aid, int priority ) {
		
		if( aid == null || aid.isEmpty() )
			throw new IllegalArgumentException("An id should be provided.");
		
		// return cached instance, if not finished -> re-doing the job, not necessary
		if( cache.isAvailable(aid) && cache.isFinished(aid) == false )
			return cache.get(aid);
		
		CachedArchiveInfo cachedInfo = null;
		ArchiveInformation info = new ArchiveInformation();
		info.setId(aid);
		
		// register in Cache
		cachedInfo = cache.register(info);
		
		//TODO smooth things up
		
		// do MaSyMoS 
		RetrievalJob initialJob = new RetrievalJob(info, new MasymosConnector(), priority);
		initialJob.addExecuteAfter( new RetrievalJob(info, new PubmedConnector(), priority) );
		initialJob.addExecuteAfter( new RetrievalJob(info, new Pmr2Connector(), priority) );
		initialJob.addExecuteAfter( new RetrievalJob(info, new BmdbConnector(), priority) );
		//initialJob.addExecuteAfter( ... );
		
		// submit job
		cachedInfo.addRetrievalJob(initialJob);
		executor.execute(initialJob);
		
		return cachedInfo;
	}
	
	public File assemble( String aid ) {
		
		if( aid == null || aid.isEmpty() )
			throw new IllegalArgumentException("An id should be provided.");
		
		// check for exist
		if( cache.isAvailable(aid) == false )
			throw new IllegalStateException("the archive is not in the cache");
		
		CachedArchiveInfo cachedInfo = cache.get(aid);
		// wait to finish
		while( cachedInfo.isFinished() == false ) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
		}
		
		File archiveFile = null;
		CombineArchive archive = null;
		try {
			archiveFile = File.createTempFile(config.getTempFilePrefix(), ".omex");
			archiveFile.delete();
			archive = new CombineArchive(archiveFile);
			ArchiveInformation info = cachedInfo.getArchiveInformation();
			
			// adding model file
			addModel(info.getModel(), archive);
			
			// adding additional files
			for( AdditionalFileInformation file : info.getAdditionFiles() ) {
				addEntry(file, archive);
			}
			
		} catch (IOException e) {
			LOGGER.error(e, "IO Exception while assembling CombineArchive");
		} catch (JDOMException | ParseException | CombineArchiveException e) {
			LOGGER.error(e, "CombineArchive related exception while assembling");
		} finally {
			
			try {
				if( archive != null ) {
					archive.pack();
					archive.close();
				}
			} catch (IOException | TransformerException e) {
				LOGGER.error(e, "Exception while pack'n'close of combine archive");
			}
		}
		
		return archiveFile;
	}
	
	private ArchiveEntry addModel(ModelInformation model, CombineArchive archive) throws MalformedURLException, IOException {
		
		ArchiveEntry entry = null;
		
		// get the file
		if( model == null || model.getDocumentURI() == null )
			return null;
		
		URL modelUrl = model.getDocumentURI().toURL();
		InputStream input = modelUrl.openStream();
		if( input == null )
			return null;
		
		String fileName = modelUrl.getPath();
		if( fileName != null && !fileName.isEmpty() ) {
			fileName = FilenameUtils.getName(fileName);
			
			String extension = FilenameUtils.getExtension(fileName);
			if( fileName.equals("download") || extension == null || extension.isEmpty() )
				fileName = model.getModelName() + ".xml";
				
		}
		else
			fileName = "model.xml";
		
		File temp = File.createTempFile(config.getTempFilePrefix(), fileName);
		FileOutputStream output = new FileOutputStream(temp);
		IOUtils.copy(input, output);
		
		input.close();
		output.flush();
		output.close();
		
		// add it to the archive
		entry = archive.addEntry( temp, "/" + fileName, Formatizer.guessFormat(temp) );
		if( model.getMetaData() != null )
			entry.addDescription( model.getMetaData().getCombineArchiveMetaData() );
		
		return entry;
	}

	private ArchiveEntry addEntry(AdditionalFileInformation file, CombineArchive archive) throws IOException {
		
		ArchiveEntry entry = null;
		
		// get the file
		InputStream input = file.getSourceStream();
		if( input == null )
			return null;
		
		File temp = File.createTempFile(config.getTempFilePrefix(), file.getFileName());
		FileOutputStream output = new FileOutputStream(temp);
		IOUtils.copy(input, output);
		
		input.close();
		output.flush();
		output.close();
		
		// add it to the archive
		entry = archive.addEntry( temp, "/" + file.getFileName(), Formatizer.guessFormat(temp) );
		if( file.getMetaData() != null )
			entry.addDescription( file.getMetaData().getCombineArchiveMetaData() );
		
		return entry;
	}
}
