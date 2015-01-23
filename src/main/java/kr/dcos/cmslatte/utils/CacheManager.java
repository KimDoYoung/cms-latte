package kr.dcos.cmslatte.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CacheManager {
	private static final int CACHE_SIZE = 5;
    private volatile static CacheManager instance;
	public static CacheManager getInstance() {
		if (instance == null) {
			synchronized (CacheManager.class) {
				if (instance == null) {
					instance = new CacheManager();
				}
			}
		}
		return instance;
	}
	private  class LruCache<A, B> extends LinkedHashMap<A, B> {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 916498046829872490L;
		private final int maxEntries;

	    public LruCache(final int maxEntries) {
	        super(maxEntries + 1, 0.75f, true);
	        this.maxEntries = maxEntries;
	    }
	    @Override
	    protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
	        return super.size() > maxEntries;
	    }
	}
	Map<String, Object> map = Collections.synchronizedMap(new LruCache<String, Object>(CACHE_SIZE));
	public void put(String key,Object obj){
		map.put(key, obj);
	}
	public Object get(String key){
		return map.get(key);
	}
}
