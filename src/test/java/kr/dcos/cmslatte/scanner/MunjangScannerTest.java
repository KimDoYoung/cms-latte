package kr.dcos.cmslatte.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import kr.dcos.cmslatte.core.Susik;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MunjangScannerTest {

private static Logger logger = LoggerFactory.getLogger(MunjangScannerTest.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void test0() throws CloneNotSupportedException, CmsLatteException {
		String s = " \"!\" 10 00 \"abc\" \"a; \t\n//sss /*sosos*/\" false true .5 1.5 substring(a,1,2) a.trim() true ";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		
		Token t = tokenStack.pop();
		assertEquals(t.getTokenType(),TokenType.Boolean);
		assertEquals(t.getBoolean(),true);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.Field);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.Function);
		
		assertEquals(tokenStack.pop().getTokenType(),TokenType.Double);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.Double);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.Boolean);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.Boolean);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.String);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.String);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.Integer);
		assertEquals(tokenStack.pop().getTokenType(),TokenType.Integer);
		
		assertEquals(tokenStack.pop().getTokenType(),TokenType.String);
	}
	@Test
	public void test1() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s = "10 < 20";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		
		assertEquals(tokenStack.size(),3);
		assertEquals(tokenStack.get(0).getTokenType(),TokenType.Integer);
		assertTrue(tokenStack.get(0).getInteger()==10);
		
		Susik ss = new Susik(null);
		Token result = ss.eval(tokenStack);
		logger.debug("11:"+result.toString());
		assertTrue(result.getBoolean());
		
	}
	@Test
	public void test2() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s = "10 > 20";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		assertEquals(tokenStack.size(),3);
		assertEquals(tokenStack.get(0).getTokenType(),TokenType.Integer);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		Susik ss = new Susik(null);
		Token result = ss.eval(tokenStack);
		logger.debug("aa:" + result.toString());
		assertFalse(result.getBoolean());
		
	}
	@Test
	public void test3() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s = "10 == .2";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		assertEquals(tokenStack.size(),3);
		assertEquals(tokenStack.get(0).getTokenType(),TokenType.Integer);
		
		Susik ss = new Susik(null);
		Token result = ss.eval(tokenStack);
		logger.debug("aa:" + result.toString());
		assertFalse(result.getBoolean());
	}
	@Test
	public void test4() throws CloneNotSupportedException, CmsLatteException {
		String[] ss = new String[]{
				"(1==1)",
				"( 1 == b[3].trim() )",
				"(1== b) "
		};
		for (String s : ss) {
			MunjangScanner cs = new MunjangScanner();
			TokenStack tokenStack = cs.scan(s);
			assertEquals(tokenStack.size(),5);
			logger.debug("\n---->"+s+"<----");
			logger.debug("\n"+tokenStack.toString());
		}
	}
	@Test
	public void test5() throws CloneNotSupportedException, CmsLatteException {
		String[] ss = new String[]{
				"a[1,3] = 10",
				"a[ 1 , 3]=10",
				"a[ 1, 3] = 10",
				"a[ b[1], 3] = 10",
				"_abc[b.length(),4]=10",
				"a = substring(d[1],1,3)"
		};
		for (String s : ss) {
			MunjangScanner cs = new MunjangScanner();
			TokenStack tokenStack = cs.scan(s);
			assertEquals(tokenStack.size(),3);
			logger.debug("\n---->"+s+"<----");
			logger.debug("\n"+tokenStack.toString());
			
		}
	}
	@Test
	public void test6() throws CloneNotSupportedException, CmsLatteException {
		String[] ss = new String[]{
				  "a=\"abc\"+substring(\"12345\",1,2)",
				  "a=1+substring(d,1,2)"
		};
		for (String s : ss) {
			MunjangScanner cs = new MunjangScanner();
			TokenStack tokenStack = cs.scan(s);
			assertEquals(tokenStack.size(),5);
			assertEquals(tokenStack.peek().getTokenType(),TokenType.Function);
			logger.debug("\n---->"+s+"<----");
			logger.debug("\n"+tokenStack.toString());
			
		}
		
	}
	@Test
	public void test7() throws CloneNotSupportedException, CmsLatteException {
		String[] ss = new String[]{
				  "a={}",
				  "t={{}}",
				  "a = {}",
				  "t = {{}}",
				  "t = { { } } ",
				  "a = { 1, 2, 3}",
				  "t = { {1,2,3},{4,5,y}}",
				  "a = { 1, 2 , \"abc\" } "
				  
		};
		for (String s : ss) {
			MunjangScanner cs = new MunjangScanner();
			TokenStack tokenStack = cs.scan(s);
			assertEquals(tokenStack.size(),3);
			if(tokenStack.get(0).getValue().equals("a")){
				assertEquals(tokenStack.peek().getTokenType(),TokenType.ArrayInit);
			}else {
				assertEquals(tokenStack.peek().getTokenType(),TokenType.TableInit);
			}
			String k = s.substring(s.indexOf('{')).trim();
			assertEquals(tokenStack.peek().getString(),k);
			logger.debug("\n---->"+s+"<----");
			logger.debug("\n"+tokenStack.toString());
			
		}		
	}
	@Test
	public void testIf() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s = "if a==1 begin b=1; else begin b=2; end;";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
	}
	@Test
	public void testFor() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s = "for i to (1+4) begin a=i; end;";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
	}
	//(1+1)
	@Test
	public void testFor2() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s = "(1+1)";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		assertEquals(tokenStack.size(),5);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
	}
	//foreach s in a begin echo s; end;
	@Test
	public void testForeach() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s = "foreach s in a begin echo s; end;";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		//assertEquals(tokenStack.size(),3);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
	}
	//label
	@Test
	public void testLabel() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s = "#flag1";
		MunjangScanner cs = new MunjangScanner();
		TokenStack tokenStack = cs.scan(s);
		//assertEquals(tokenStack.size(),3);
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		assertEquals(tokenStack.get(0).getTokenType(),TokenType.Label);
	}
	@Test
	public void testSave() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s="save to file fileName with encoding override true";
		MunjangScanner ms = new MunjangScanner();
		TokenStack tokenStack = ms.scan(s);
		
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		assertEquals(tokenStack.get(2).getTokenType(),TokenType.File);
	}
	@Test
	public void testPlusAssign() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s="a += 3";
		MunjangScanner ms = new MunjangScanner();
		TokenStack tokenStack = ms.scan(s);
		
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		assertEquals(tokenStack.size(),5);
	}
	@Test
	public void testMinusAssign() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s="a -= 3";
		MunjangScanner ms = new MunjangScanner();
		TokenStack tokenStack = ms.scan(s);
		
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		assertEquals(tokenStack.size(),5);
	}
	@Test
	public void testPlusPlus() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s="a = b--* i++";
		//String s="i++";
		MunjangScanner ms = new MunjangScanner();
		TokenStack tokenStack = ms.scan(s);
		
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		assertEquals(tokenStack.size(),7);
	}
	@Test
	public void testMinusInteger() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException {
		String s="3+-3";
		//String s="i++";
		MunjangScanner ms = new MunjangScanner();
		TokenStack tokenStack = ms.scan(s);
		
		logger.debug("\n---->"+s+"<----");
		logger.debug("\n"+tokenStack.toString());
		assertEquals(tokenStack.size(),4);
	}

}
