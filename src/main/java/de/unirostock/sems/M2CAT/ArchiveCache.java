package de.unirostock.sems.M2CAT;

import java.util.HashMap;
import java.util.Map;

import de.unirostock.sems.M2CAT.datamodel.ArchiveInformation;
import de.unirostock.sems.M2CAT.datamodel.CachedArchiveInfo;


/**
 * Cache for already retrieved models
 * @author martin
 *
 */
public class ArchiveCache {
	
	private static volatile ArchiveCache instance = null;
	
	public static ArchiveCache getInstance() {
		
		// check synchronized, if instance is already present
		if( instance == null )
			instance = new ArchiveCache();
		
		return instance;
	}
	
	// ----------------------------------------
	
	private Map<String, CachedArchiveInfo> cache = new HashMap<String, CachedArchiveInfo>();
	
	/**
	 * default private constructor
	 */
	private ArchiveCache() {}
	
	// TODO add auto cache clean
	
	/**
	 * Registers an archive in the cache. Every archive id can only be registered once!
	 *
	 * @param archiveInformation the archive information
	 * @return the cached archive info
	 */
	public synchronized CachedArchiveInfo register(ArchiveInformation archiveInformation) {
		
		String id = archiveInformation.getId();
		if( id == null || id.isEmpty() )
			throw new IllegalArgumentException("The ID is not allowed to be null");
		
		if( cache.containsKey(id) && cache.get(id).isFinished() == false )
			throw new IllegalStateException("The archive is already registred in the cache and not finished yet.");
		
		CachedArchiveInfo cachedInfo = new CachedArchiveInfo(archiveInformation);
		cache.put(id, cachedInfo);
		
		return cachedInfo;
	}
	
	/**
	 * Checks if an archive is available in the cache aka. known/processing
	 *
	 * @param id the id
	 * @return true, if checks if is available
	 */
	public boolean isAvailable(String id) {
		return cache.containsKey(id);
	}
	
	/**
	 * Checks if an archive retrieval job is finished.
	 *
	 * @param id the id
	 * @return true, if checks if is finished
	 */
	public boolean isFinished(String id) {
		if( cache.containsKey(id) && cache.get(id).isFinished() )
			return true;
		else
			return false;
	}
	
	/**
	 * Returns archiveInformation for the given id, or null.
	 *
	 * @param id the id
	 * @return the cached archive info
	 */
	public CachedArchiveInfo get(String id) {
		return cache.get(id);
	}
	
}
