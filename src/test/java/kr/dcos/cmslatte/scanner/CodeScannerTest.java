package kr.dcos.cmslatte.scanner;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import kr.dcos.cmslatte.core.TestBase;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.scanner.CodeScanner;
import kr.dcos.cmslatte.scanner.LatteItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeScannerTest  extends TestBase {

	private static Logger logger = LoggerFactory.getLogger(CodeScannerTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCommentSkip() throws CloneNotSupportedException, CmsLatteException {
		String[] ss= new String[]{"if i==0 //abc\n begin a=\"abc\";b=2; end",
				"if i==0 //abc\n begin a=\"a;//cmt\nb /****/ //c\";b=a[1].trim(); end",
				"if (i==0) begin a=1;b=2;;; ; end;"
		};
		for (String s : ss) {
			CodeScanner cs = new CodeScanner();
			List<LatteItem> list = cs.scan(s);
			assertNotNull(list);
			assertEquals(list.size(),5);
			logger.debug("\n--->"+s+"<-----");
			logger.debug("\n"+cs.toString());
			
		}
	}
	@Test
	public void testCommentSkip1() throws CloneNotSupportedException, CmsLatteException {
		String s = "a=\"abc\"";
		CodeScanner cs = new CodeScanner();
		List<LatteItem> list = cs.scan(s);
		assertNotNull(list);
		assertEquals(list.size(),1);
		logger.debug("\n--->"+s+"<-----");
		logger.debug("\n"+cs.toString());

	}
	@Test
	public void testSwitchControl() throws CmsLatteException, IOException, CloneNotSupportedException {
		String s = readStringFromResource(TemplatePath+"switch1.template");
		CodeScanner cs = new CodeScanner();
		List<LatteItem> list = cs.scan(s);
		assertNotNull(list);
		assertEquals(list.size(),15);
		logger.debug("\n--->"+s+"<-----");
		logger.debug("\n"+cs.toString());	
	}
	@Test
	public void testQuestionIf() throws CmsLatteException, IOException, CloneNotSupportedException {
		//String s = "a=true; switch a begin case 1: echo b; case 2: echo c; default : echo d; end;(1==1)? a : b;";
		String[] ss = new String[] { "(1==1) ? a : b;",
				" ( 1 == a) ? \"a?aa\":\" : ab+*&\";",
				"(a==b)?c=a:d=a;",
				"a?b:d;"
		};
		for (String s : ss) {
			CodeScanner cs = new CodeScanner();
			List<LatteItem> list = cs.scan(s);
			assertNotNull(list);
			assertEquals(list.size(),1);
			logger.debug("\n--->"+s+"<-----");
			logger.debug("\n"+cs.toString());
			
		}

	}
	@Test
	public void testLabel() throws CloneNotSupportedException, CmsLatteException {
		String s = "a=\"abc\";//label\n#flag1; goto flag1;";
		CodeScanner cs = new CodeScanner();
		List<LatteItem> list = cs.scan(s);
		assertNotNull(list);
		assertEquals(list.size(),3);
		logger.debug("\n--->"+s+"<-----");
		logger.debug("\n"+cs.toString());

	}
	
	@Test
	public void testLabel2() throws CloneNotSupportedException, CmsLatteException {
		String s = "a=1;if a==1 begin goto flag1; end;#flag3; #falg1;";
		CodeScanner cs = new CodeScanner();
		List<LatteItem> list = cs.scan(s);
		assertNotNull(list);
		assertEquals(list.size(),7);
		logger.debug("\n--->"+s+"<-----");
		logger.debug("\n"+cs.toString());

	}
}
