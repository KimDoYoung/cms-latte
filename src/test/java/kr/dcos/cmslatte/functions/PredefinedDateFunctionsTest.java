package kr.dcos.cmslatte.functions;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.core.CmsLatte;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.utils.LatteUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PredefinedDateFunctionsTest {

	private static Logger logger = LoggerFactory
		.getLogger(PredefinedDateFunctionsTest.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNow() throws CmsLatteException {
		String s = "<@  echo now(); @>";
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output.length(), LatteUtil.longDateFormat.length());
		logger.debug(output);
		//assertEquals(output,"1234");
	}
	@Test
	public void testDayOfWeek() throws CmsLatteException {
		String s = "<@  echo dayOfWeek(date(\"2012-12-05\")); @>";
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		logger.debug(output);
		assertEquals(output,"3");
	}
}
