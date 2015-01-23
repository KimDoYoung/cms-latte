package kr.dcos.cmslatte.scanner;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.scanner.FunctionScanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionScannerTest {

private static Logger logger = LoggerFactory
		.getLogger(FunctionScannerTest.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws CmsLatteFunctionException {
		String s = " substring(\"12345\",1,2)    ";
		FunctionScanner fs = new FunctionScanner();
		fs.scan(s);
		assertEquals(fs.getFunctionName(),"substring");
		assertEquals(fs.getArgStringList().size(),3);
		s = " trim()    ";
		fs = new FunctionScanner();
		fs.scan(s);
		assertEquals(fs.getFunctionName(),"trim");
		assertEquals(fs.getArgStringList().size(),0);
		
	}
	@Test
	public void test1() throws CmsLatteFunctionException {
		String[] ss = new String[] {
			" subStr(  substring(\"abcdefg\",   1, 2) , 3, 4) ",	
				" subStr( a.substring(\"aaaa\",1,2),99 ,88)"	
		};
		FunctionScanner fs = new FunctionScanner();
		for (String s : ss) {
			fs.scan(s);
			assertEquals(fs.getFunctionName(),"subStr");
			assertEquals(fs.getArgStringList().size(),3);
			logger.debug(fs.toString());
			
		}
	}
	@Test
	public void testTable() throws CmsLatteFunctionException {
		String[] ss = new String[] {
			" isEmpty(a)",
			" isEmpty(a[1,1]) ",	
		};
		FunctionScanner fs = new FunctionScanner();
		for (String s : ss) {
			fs.scan(s);
			assertEquals(fs.getFunctionName(),"isEmpty");
			assertEquals(fs.getArgStringList().size(),1);
			logger.debug(fs.toString());
			
		}
	}
}
