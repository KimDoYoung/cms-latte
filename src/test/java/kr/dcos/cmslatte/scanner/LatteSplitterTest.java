package kr.dcos.cmslatte.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import kr.dcos.cmslatte.core.TestBase;
import kr.dcos.cmslatte.exception.CmsLatteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteSplitterTest extends TestBase {
	
	private static Logger logger = LoggerFactory
			.getLogger(LatteSplitterTest.class);
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws CmsLatteException {
		String s = "abc<@ a=10; echoln a;@>def";
	    LatteSplitter splitter = new LatteSplitter(s);
	    logger.debug(splitter.toString());
		assertNotNull(splitter.getList());
		
		assertEquals(splitter.getList().size(), 4);
	}
	@Test
	public void testIsEmpty() throws CmsLatteException {
		String s = "<@ "
				+  "a=\"A@>\";"
				+  "b=\"B\";"
				+  "echoln a+b;"
				+  " @>";
//		String s = "<@1@> ";
		LatteSplitter splitter = new LatteSplitter(s);
	    logger.debug(splitter.toString());
		assertNotNull(splitter.getList());
	
		assertEquals(splitter.getList().size(), 3);
	}
	@Test
	public void testTest1() throws CmsLatteException {
		String s = "1<@a=\"abc\";echo a;@>2";
	    LatteSplitter splitter = new LatteSplitter(s);
	    logger.debug(splitter.toString());
		assertNotNull(splitter.getList());
		
		assertEquals(splitter.getList().size(), 4);

	}
	@Test
	public void testIfControl() throws CmsLatteException {
		String s = "1<@ if a==b begin c=1; end elseif a==c; begin c=2; end else begin c=3; end@>2";
	    LatteSplitter splitter = new LatteSplitter(s);
	    logger.debug(splitter.toString());
		assertNotNull(splitter.getList());
		
		//tassertEquals(splitter.getList().size(), 4);

	}
	@Test
	public void testForControl() throws CmsLatteException {
		String s = "<@for i to (1+4) step (1+1) begin a=i; end;@>";
	    LatteSplitter splitter = new LatteSplitter(s);
	    logger.debug(splitter.toString());
		assertNotNull(splitter.getList());
		//assertEquals(splitter.getList().size(), 4);
	}
	@Test
	public void testGoto1() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath+"goto1.template");
		 LatteSplitter splitter = new LatteSplitter(s);
		logger.debug(splitter.toString());
		assertNotNull(splitter.getList());
		assertEquals(splitter.getList().size(),9);
	}

}
