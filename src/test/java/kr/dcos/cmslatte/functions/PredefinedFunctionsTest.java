package kr.dcos.cmslatte.functions;

import static org.junit.Assert.assertEquals;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PredefinedFunctionsTest {

	private static Logger logger = LoggerFactory
		.getLogger(PredefinedFunctionsTest.class);


	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testTrim() throws CmsLatteFunctionException{
		ILatteFunction trim = new ILatteFunction() {
			
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.trim(args);
			}
		};
		logger.debug("hoho ["+trim.invoke(" ABC ")+"]");
	}
	@Test
	public void testSubstring() throws CmsLatteFunctionException{
		ILatteFunction substring = new ILatteFunction() {
			
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.substring(args);
			}
		};
		String s = (String)substring.invoke("unhappy",2);
		assertEquals(s, "happy");
		s = (String)substring.invoke("smiles",1,5);
		assertEquals(s, "mile");
	}
}
