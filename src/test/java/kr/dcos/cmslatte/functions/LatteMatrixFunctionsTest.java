package kr.dcos.cmslatte.functions;

import static org.junit.Assert.*;

import java.io.IOException;

import kr.dcos.cmslatte.core.CmsLatte;
import kr.dcos.cmslatte.exception.CmsLatteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteMatrixFunctionsTest {

private static Logger logger = LoggerFactory
		.getLogger(LatteMatrixFunctionsTest.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSort1()throws CmsLatteException, IOException {
		String s = "<@ t = {{1,2,3},{4,5,6}}; echo t; @>";
		CmsLatte cmsLatte = new CmsLatte();
		//logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		logger.debug(output);
		//assertEquals(output,"true");
	}

}
