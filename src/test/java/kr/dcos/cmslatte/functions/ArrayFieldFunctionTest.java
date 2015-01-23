package kr.dcos.cmslatte.functions;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.core.CmsLatte;
import kr.dcos.cmslatte.exception.CmsLatteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayFieldFunctionTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(ArrayFieldFunctionTest.class);
	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testJoin() throws CmsLatteException {
		String s = "<@ a={1,2,3}; echo join(a,\",\",\"'\"); @>";
		CmsLatte cmsLatte = new CmsLatte();
		//logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		logger.debug(output);
		assertEquals(output,"'1','2','3'");
		
		s = "<@ a={1,2,3}; echo join(a,\"###\",\"'\",\"'\"); @>";
		output = cmsLatte.createPage(s.trim());
		logger.debug(output);
		assertEquals(output,"'1'###'2'###'3'");

		s = "<@ a={1,2,3}; echo a.join(\",\",\"'\",\"'\"); @>";
		output = cmsLatte.createPage(s.trim());
		logger.debug(output);
		assertEquals(output,"'1','2','3'");

		s = "<@ a={\"1\",\"2\",\"3\"}; echo a.join(\",\",\"'\",\"'\"); @>";
		output = cmsLatte.createPage(s.trim());
		logger.debug(output);
		assertEquals(output,"'1','2','3'");
		
	}

}
