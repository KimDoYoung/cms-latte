package kr.dcos.cmslatte.core;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import kr.dcos.cmslatte.core.CmsLatte;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.utils.TestItem;
import kr.dcos.cmslatte.utils.TestWithXml;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsLatteTest extends TestBase{
	
	private static Logger logger = LoggerFactory.getLogger(CmsLatteTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test0() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath + "1.template");
		assertNotNull(s);
	}
	
	@Test
	public void test() throws CmsLatteException {
		String src="abc<@ a=10+3; echo a;@>def";
		CmsLatte cmsLatte = new CmsLatte();
		String output = cmsLatte.createPage(src);
		assertEquals(output,"abc13def");
	}
	@Test
	public void test1() throws CmsLatteException {
		String s = "<@ "
				+  "a=\"A\";"
				+  "b=\"B\";"
				+  "c=\"C\";"
				+  "echo a+b+c+\"\n\";"
				+  " @>";
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s);
		assertEquals(output,"ABC\n");
	}
	@Test
	public void test2() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath+"1.template");
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s);
		assertEquals(output,"1abc2");
	}
	@Test
	public void testIfcontrol1() throws CmsLatteException, IOException {
		String s = "1<@ a=1;b=1; if a==b begin c=1; end elseif a==c; begin c=2; end else begin c=3; end;echo c;@>2";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s);
		assertEquals(output,"112");
	}
	@Test
	public void testIfController2() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath+"if1.template");
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"1B2");
	}
	@Test
	public void testIfController3() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath+"if2.template");
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"1C2");
	}
	@Test
	public void testIfController4() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath+"if3.template");
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"1M2");
	}
	@Test
	public void testFor0() throws CmsLatteException, IOException {
		String s = "0<@ for i=1 to 5 begin echo i;  end;@>4";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"0123454");
	}
	@Test
	public void testFor1() throws CmsLatteException, IOException {
		String s = "0<@ for i=1 to 5 begin echo i; if i== 3 begin break; end; end;@>4";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"01234");
	}
	@Test
	public void testFor2() throws CmsLatteException, IOException {
		String s = "0<@ i=1;for i= (1-1+2-3+1+1) to 5 step (1+1*2-1) begin echo i; end;@>4";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"01354");
	}
	@Test
	public void testFor3() throws CmsLatteException, IOException {
		String s = "<@ for i=1 to 10  begin if i%2 == 0 begin continue; end; echo i; end;@>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"13579");
	}
	@Test
	public void testFor4() throws CmsLatteException, IOException {
		String s = "<@ for i=0 to 0  begin echo i; end;@>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"0");
	}
	@Test
	public void testFor5() throws CmsLatteException, IOException {
		String s = "<@ for i=0 down 0  begin echo i; end;@>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"0");
	}	
	@Test
	public void testWhile1() throws CmsLatteException, IOException {
		String s = "[<@ i=0; while i<10 begin echo i; i=i+1; end;  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[0123456789]");
	}
	@Test
	public void testWhile2Break() throws CmsLatteException, IOException {
		String s = "[<@ i=0; while i<10 begin echo i; i=i+1; if i==5 begin break;end; end;  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[01234]");
	}
	@Test
	public void testWhile3Continue() throws CmsLatteException, IOException {
		String s = "[<@ i=0; while i<10 begin i=i+1; if i<5 begin continue;end; echo i; end;  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[5678910]");
	}
	@Test
	public void testArrayInit() throws CmsLatteException, IOException {
		String s = "[<@ a={1,2,3}; echo a[1];  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[2]");		
	}
	@Test
	public void testTableInit() throws CmsLatteException, IOException {
		String s = "[<@ t={{$$1,2,3}{$$4,5,6}}; echo t[0,1]+t[1,2];  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[26]");		
	}	
	@Test
	public void testForeach() throws CmsLatteException, IOException {
		String s = "[<@ a={$$a,b,c}; foreach s in a begin echo s; end;  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[abc]");		
	}
	@Test
	public void testForeach1() throws CmsLatteException, IOException {
		String s = "[<@ foreach s in {1,2,3} begin echo s; end;  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[123]");		
	}
	@Test
	public void testForeach2() throws CmsLatteException, IOException {
		String s = "[<@ foreach s in {} begin echo s; end;  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[]");		
	}
	@Test
	public void testForeach3() throws CmsLatteException, IOException {
		String s = "[<@ foreach s in {{1,2},{3,4}} begin echo s; end;  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[1234]");		
	}
	@Test
	public void testForeach4() throws CmsLatteException, IOException {
		String s = "[<@ a=\"123456789\"; foreach s in subStr(a,1,3) begin echo s; end;  @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[234]");		
	}
	@Test
	public void testForeach5() throws CmsLatteException, IOException {
		String s = "[<@ a={1,2,3,4,5}; foreach i in a begin echo i ; if i==3 begin break; end;  end;   @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[123]");
	}
	@Test
	public void testForeach6() throws CmsLatteException, IOException {
		String s = "[<@ a={1,2,3,4,5}; foreach i in a begin ; if i<3 begin continue; end; echo i;  end;   @>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[345]");
	}
	@Test
	public void testSwitch1() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath+"switch1.template");
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage("<@"+s.trim()+"@>");
		assertEquals(output,"3");
	}
	@Test
	public void testSwitch2() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath+"switch2.template");
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage("<@"+s.trim()+"@>");
		assertEquals(output,"45");
	}
	@Test
	public void testQuestionIf1() throws CmsLatteException, IOException {
		String s = "[<@ a= 1; a==2 ? echo 3 : echo 4;@>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[4]");
	}
	@Test
	public void testGoto1() throws CmsLatteException, IOException {
		String s = readStringFromResource(TemplatePath+"goto1.template");
		logger.debug(s.trim());
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output.trim(),"ABC");
	}
	@Test
	public void testExit() throws CmsLatteException, IOException {
		String s = "[<@ echo 1; exit; echo 3;@>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[1");
	}
	@Test
	public void testClear() throws CmsLatteException, IOException {
		String s = "[<@ echo 1; clear; echo 3;@>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"3]");
	}
	@Test
	public void testClear1() throws CmsLatteException, IOException {
		String s = "[<@ echo 1; clear output; echo 3;@>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"3]");
	}
	@Test
	public void testClear2() throws CmsLatteException, IOException {
		String s = "[<@ a=3; clear variables;a=4; echo a;@>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[4]");
	}
	@Test
	public void testField() throws CmsLatteException, IOException {
		String s = "[<@ a=4@>123<@a@>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[1234]");
	}
	@Test
	public void testSusik() throws CmsLatteException, IOException {
		String s = "[<@ a=4@>123<@a+1@>]";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"[1235]");
	}
	@Test
	public void testSaveToFile() throws CmsLatteException, IOException {
		String s = "1<@ fn=\"c:/1.txt\"; save output to file fn with \"utf-8\" override false  ;@>2";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"12");
	}
	@Test
	public void testSaveToVariable() throws CmsLatteException, IOException {
		String s = "1<@  save output to a; echo a;@>2";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"112");
	}
	@Test
	public void testPlusAssign() throws CmsLatteException, IOException {
		String s = "1<@  a=1; a+=2; b=10; b -= 5;  echo a+b;@>2";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"182");
	}
	@Test
	public void testPlusPlus() throws CmsLatteException, IOException {
		String s = "1<@  a=1; b = (a++) * 3; echo a+b;@>2";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"152");
	}
	@Test
	public void testPlusPlus2() throws CmsLatteException, IOException {
		String s = "1<@  a=1; b = (--a) * 3; echo a+b;@>2";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"102");
	}

	@Test
	public void testIndexOf() throws CmsLatteException, IOException {
		String s = "1<@ a={1,2,3,4,5}; echo indexOf(a,2); @>2";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"112");
	}
	@Test
	public void testLoadVariables() throws CmsLatteException, IOException {
		String varContext = this.readStringFromResource(TemplatePath + "var.xml");
		File tmpFileName = File.createTempFile("var", ".xml");
		
		PrintWriter out = new PrintWriter(tmpFileName);
		out.print(varContext);
		out.close();
		
		String s = "1<@ a=\""+tmpFileName.getAbsolutePath()+"\";load variables from a; echo b; @>2";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"1132");
	}
	@Test
	public void testXml() throws CmsLatteException {
		TestWithXml testXml = new TestWithXml();
		File file = getFileFromResource(TemplatePath+"testcode1.xml");
		assertNotNull(file);
		testXml.load(file);
		for (TestItem item : testXml.getList()) {
			CmsLatte cmsLatte = new CmsLatte();
			String output = cmsLatte.createPage(item.getCode().trim());
			assertEquals(output,item.getResult().trim());
		}
	}


	@Test
	public void testSplit() throws CmsLatteException, IOException {
		String s = "<@ s=\"abc,def,ghi\"; a = s.split(\",\");echo a[1]; @>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"def");
	}
	@Test
	public void testSplit2() throws CmsLatteException, IOException {
		String s = "<@ s=\"abc,def,ghi\"; a = s.split(\",\");echo a.length(); @>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"3");
	}
	@Test
	public void testCut1() throws CmsLatteException, IOException {
		String s = "<@ s=\"12345\"; echo cut(s,1); @>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"2345");
	}
	@Test
	public void testCut2() throws CmsLatteException, IOException {
		String s = "<@ s=\"12345\"; echo cut(s,-1); @>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"1234");
	}
	@Test
	public void testArray1() throws CmsLatteException, IOException {
		String s = "<@ a={1,2,3}; a=insert(a,4,5); a[3]=4; echo a.length(); @>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"5");
	}
	@Test
	public void testArraySub()throws CmsLatteException, IOException {
		String s = "<@ a={0,1,2,3,4,5};d = sub(a,4); echo d[1]; @>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"5");
	}
//	t = {{1,2,3},{4,5,6}};
//	a = {a,b,c};
//	t1 = insertRow(t,1,a);
//	echo t.length();
	@Test
	public void testInsertRow()throws CmsLatteException, IOException {
		String s = "<@ t = {{1,2,3},{4,5,6}};a = {$$ a,b,c};t = insertRow(t,1,a); echo t.length(); @>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"9");
	}
//	t = {{1,2,3},{4,5,6}};
//	a = {};
//	t = t.insertRow(2,a);
//	echo t.length();
//	echo isEmpty(t[2,0]);
	@Test
	public void testInsertRow1()throws CmsLatteException, IOException {
		String s = "<@ t = {{1,2,3},{4,5,6}};a = {};t=t.insertRow(2,a); echo isEmpty(t[2,0]); @>";
		logger.debug(s);
		CmsLatte cmsLatte = new CmsLatte();
		logger.debug(cmsLatte.toString());
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"true");
	}
	
	@Test
	public void testIsEmpty()throws CmsLatteException, IOException {
		String s = "<@  echo isEmpty(t); @>";
		CmsLatte cmsLatte = new CmsLatte();
		String output = cmsLatte.createPage(s.trim());
		assertEquals(output,"true");
		
		s = "<@ echo isEmpty(t[10]);  @>";
		output = cmsLatte.createPage(s.trim());
		assertEquals(output,"true");
		
	}

}
