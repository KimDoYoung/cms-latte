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

public class ArrayFieldTest {

private static Logger logger = LoggerFactory.getLogger(ArrayFieldTest.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testEquals() throws CmsLatteException{
		ArrayField af1 = new ArrayField("a");
		ArrayField af2 = new ArrayField("b");
		assertEquals(af1,af2);
		af1.append(1);
		af2.append("1");
		assertFalse(af1.equals(af2));
	}
	@Test
	public void testEquals2() throws CmsLatteException{
		ArrayField af1 = new ArrayField("a");
		ArrayField af2 = new ArrayField("b");
		assertEquals(af1,af2);
		af1.append(1);
		af2.append(1);
		assertTrue(af1.equals(af2));
	}
	@Test
	public void testEquals3() throws CmsLatteException{
		ArrayField af1 = new ArrayField("a");
		ArrayField af2 = new ArrayField("b");
		assertEquals(af1,af2);
		af1.append(2);
		af1.append(1);
		af2.append(1);
		af2.append(2);
		assertFalse(af1.equals(af2));
	}
	@Test
	public void test() {
		ArrayField af = new ArrayField("a");
		try {
			af.appendOrReplace(10, "10");
			assertEquals(af.getValue(10),"10");
			assertEquals(af.getValue(0),null);
			//logger.debug("lastIndex : " + af.getLastIndex());
			assertEquals(af.getLastIndex(),10);
			af.appendOrReplace(10, "20");
			assertEquals(af.getValue(10),"20");
			
		} catch (CmsLatteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Test
	public void test2() throws CmsLatteException {
		ArrayField af = new ArrayField("b");
		af.append("20");
		assertEquals(af.getLastIndex(), 0);
		assertEquals(af.size(), 1);
		assertFalse(af.isEmpty());
		assertEquals(af.getType(), FieldType.Array);
	}
	@Test (expected=CmsLatteException.class)
	public void test3() throws CmsLatteException{
		ArrayField af = new ArrayField("c");
		af.getValue(-1);
	}
	
}
