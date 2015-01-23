package kr.dcos.cmslatte.cache;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.utils.CacheManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CacheManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		CacheManager.getInstance().put("a", 1);
		assertNotNull(CacheManager.getInstance().get("a"));
		CacheManager.getInstance().put("b", 2);
		assertNotNull(CacheManager.getInstance().get("b"));
		CacheManager.getInstance().put("c", 3);
		assertNotNull(CacheManager.getInstance().get("c"));
		CacheManager.getInstance().put("d", 4);
		assertNotNull(CacheManager.getInstance().get("d"));
		CacheManager.getInstance().put("e", 5);
		assertNotNull(CacheManager.getInstance().get("e"));
		CacheManager.getInstance().put("f", 6);
		assertNotNull(CacheManager.getInstance().get("f"));
		CacheManager.getInstance().put("g", 7);
		assertNotNull(CacheManager.getInstance().get("g"));
		
		Object o = CacheManager.getInstance().get("a");
		assertNull(o);
	}

}
