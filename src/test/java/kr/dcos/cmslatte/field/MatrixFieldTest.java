package kr.dcos.cmslatte.field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import kr.dcos.cmslatte.exception.CmsLatteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatrixFieldTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(MatrixFieldTest.class);
	


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void test0() throws CmsLatteException {
		MatrixField mf = new MatrixField("m");
		assertEquals(mf.size(), 0);
		assertTrue(mf.isEmpty());
	}
	@Test
	public void test1() throws CmsLatteException {
		MatrixField mf = new MatrixField("m",10	,10);
		mf.appendOrReplace(1, 1, "11");
		assertEquals(mf.getValue(1,1),"11");
		assertEquals(mf.getValue(1,2),null);
		assertEquals(mf.size(),100);
	}
	@Test
	public void testToString() throws CmsLatteException {
		MatrixField mf = new MatrixField("a");
		mf.appendOrReplace(0, 0, 1);
		mf.append(2);
		mf.append(3);
		logger.debug(mf.toString());
	}
	@Test
	public void testEquals() throws CmsLatteException {
		MatrixField mf1 = new MatrixField("a");
		MatrixField mf2 = new MatrixField("b");
		assertEquals(mf1,mf2);
		mf1.appendOrReplace(0, 0, 1);
		mf2.appendOrReplace(0, 0, 1);
		assertTrue(mf1.equals(mf2));
		mf2.appendOrReplace(0, 1, 2);
		assertFalse(mf1.equals(mf2));
		mf1.appendOrReplace(0, 1, 2);
		assertTrue(mf1.equals(mf2));
	}
	@Test
	public void testSort() throws CmsLatteException {
		MatrixField mf1 = new MatrixField("a");
		MatrixField mf2 = new MatrixField("b");
		mf1.appendOrReplace(0, 0, "10");
		mf1.appendOrReplace(0, 1, 3);
		mf1.appendOrReplace(0, 2, "AAA");
		
		mf1.appendOrReplace(1, 0, "110");
		mf1.appendOrReplace(1, 1, 2);
		mf1.appendOrReplace(1, 2, "BBB");
		
		mf1.appendOrReplace(2, 0, "20");
		mf1.appendOrReplace(2, 1, 1);
		mf1.appendOrReplace(2, 2, "CCC");
		
		logger.debug("Org:"+mf1.toString());
		mf1.sort(0, "DESC");
		
		logger.debug("DESC:"+mf1.toString());
		mf1.sort(0, "ASC");
		logger.debug("ASC:"+mf1.toString());
	}
}
