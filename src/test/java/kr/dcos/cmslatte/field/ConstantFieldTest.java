package kr.dcos.cmslatte.field;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantFieldTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(ConstantFieldTest.class);
	


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCode() {
		ConstantField cf1 = new ConstantField(null, null);
		ConstantField cf2 = new ConstantField(null, null);
		assertEquals(cf1.hashCode(),cf2.hashCode());
		logger.debug(Integer.toString(cf1.hashCode()));
		logger.debug(cf1.toString());
	}

	@Test
	public void testEqualsObject() {
		ConstantField cf1 = new ConstantField(null, null);
		ConstantField cf2 = new ConstantField(null, null);
		assertEquals(cf1,cf2);
		cf1 = new ConstantField("a", 1);
		//cf2 = new ConstantField("a", "1");
		cf2  = cf1;
		logger.debug(cf1.toString()+" "+cf1.hashCode());
		logger.debug(cf2.toString()+" "+cf2.hashCode());
		assertTrue(cf1.equals(cf2));
		//cf2  = cf1;
		assertSame(cf1,cf2);
		cf1 = new ConstantField("a", 1);
		cf2 = new ConstantField("b", 1);
		assertEquals(cf1,cf2);
	}

}
