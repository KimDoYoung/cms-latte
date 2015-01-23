package kr.dcos.cmslatte.core;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.core.Susik;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.ConstantField;
import kr.dcos.cmslatte.field.FieldChangGo;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.functions.LatteCommonFunctions;
import kr.dcos.cmslatte.scanner.MunjangScanner;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;

import org.apache.log4j.TTCCLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SusikTest {
	
	private static Logger logger = LoggerFactory.getLogger(SusikTest.class);
	

	private static MunjangScanner ms;
	@Before
	public void setUp() throws Exception {
		 ms = new MunjangScanner();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testPlus() throws CmsLatteException, CloneNotSupportedException, CmsLatteFunctionException {
		TokenStack infix = new TokenStack();
		infix.push(new Token(TokenType.String,"A"));
		infix.push(new Token(TokenType.Plus));
		infix.push(new Token(TokenType.Integer,"20"));
		Susik susik = new Susik(null);
		Token result = susik.eval(infix);
		assertEquals(result.getValue(),"A20");
	}
	@Test
	public void testDivide() throws CmsLatteException, CloneNotSupportedException, CmsLatteFunctionException {
		TokenStack infix = new TokenStack();
		infix.push(new Token(10));
		infix.push(new Token(TokenType.Divide));
		infix.push(new Token(5));

		Susik susik = new Susik(null);
		Token result = susik.eval(infix);
		assertTrue(result.getInteger()==2);
		assertEquals(result.getTokenType(),TokenType.Integer);
		
	}
	@Test
	public void testMod() throws CmsLatteException, CloneNotSupportedException, CmsLatteFunctionException {
		TokenStack infix = new TokenStack();
		infix.push(new Token(5));
		infix.push(new Token(TokenType.Mod));
		infix.push(new Token(3));

		Susik susik = new Susik(null);
		Token result = susik.eval(infix);
		assertTrue(result.getInteger()==2);
		assertEquals(result.getTokenType(),TokenType.Integer);
		
	}
	@Test
	public void test1() throws CmsLatteException, CloneNotSupportedException, CmsLatteFunctionException {
		String s = "substring(\"ABCDE\",1,3)+\"!\"";
		TokenStack ts = ms.scan(s);
		logger.debug(ts.toString());
		assertEquals(ts.size(), 3);
		Susik susik = new Susik(null);
		Token token = susik.eval(ts);
		assertEquals(token.getString(),"BC!");
	}
	/**
	 * 창고에 constant변수가 있고 그것을 사용하는 식을 해석한다.
	 * @throws CmsLatteException 
	 * @throws CloneNotSupportedException 
	 * @throws CmsLatteFunctionException 
	 */
	@Test
	public void testUsingChanggo1() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		FieldChangGo fc = new FieldChangGo();
		fc.putField(new ConstantField("a", 1));
		String sik = "10+a";
		TokenStack ts = ms.scan(sik);
		
		Susik susik = new Susik(fc);
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.Integer);
		assertTrue(token.getInteger()==11);
	}
	/**
	 * array
	 * index = 1;
	 * a = {10,20,30};
	 * echoln a[index] + 5;
	 */
	@Test
	public void testUsingChanggo2() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		FieldChangGo fc = new FieldChangGo();
		ArrayField af = new ArrayField("a");
		af.append(10);
		af.append(20);
		af.append(30);
		fc.putField(af);
		fc.putField(new ConstantField("index", 1));
		String sik = "5+a[index]";
		TokenStack ts = ms.scan(sik);
		
		Susik susik = new Susik(fc);
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.Integer);
		assertTrue(token.getInteger()==25);
	}
	/**
	 * 필드에 적용된 prefix와 subfunction을  해석하는가?
	 * 
	 */
	@Test
	public void testUsingChanggo3() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		FieldChangGo fc = new FieldChangGo();
		fc.putField(new ConstantField("s", "abcdefg"));
		String sik = "u_s.subStr(1,3)";
		TokenStack ts = ms.scan(sik);
		
		Susik susik = new Susik(fc);
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.String);
		assertEquals(token.getString(),"BCD");
	}
	@Test
	public void testUsingChanggo4() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		FieldChangGo fc = new FieldChangGo();
		fc.putField(new ConstantField("s", " abc  "));
		String sik = "u_s.trim() + \"DEF\"";
		TokenStack ts = ms.scan(sik);
		
		Susik susik = new Susik(fc);
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.String);
		assertEquals(token.getString(),"ABCDEF");
	}
	@Test
	public void testUsingChanggo5() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		FieldChangGo fc = new FieldChangGo();
		fc.putField(new ConstantField("s", "123"));
		String sik = "s.length() + 3";
		TokenStack ts = ms.scan(sik);
		
		Susik susik = new Susik(fc);
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.Integer);
		assertTrue(token.getInteger()==6);
	}
	@Test
	public void testB1() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("1+(1*2)");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		logger.debug("Value::::"+token.getInteger());
		assertTrue(token.getInteger()==3);
		
		ts = ms.scan("1+(4/2)");
		logger.debug(ts.toString());
		token = susik.eval(ts);
		logger.debug("Value::::"+token.getInteger());
		assertTrue(token.getInteger()==3);

	}	
	// arrayInit + arrayInit
	
	@Test
	public void testArrayInit() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("{1,2,3}+{4,5,6}");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.ArrayField);
		logger.debug("Value::::"+token.getTokenType());
		assertTrue(token.getValue() instanceof ArrayField);
		ArrayField af = (ArrayField)token.getValue();
		assertEquals(af.getValue(3),4);
		assertEquals(af.size(),6);
	}
	@Test
	public void testArrayPlus1() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("{1,2,3}+4");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.ArrayField);
		logger.debug("Value::::"+token.getTokenType());
		ArrayField af = token.getValue(ArrayField.class);
		assertEquals(af.size(),4);
		assertEquals(af.getValue(3),4);
	}
	// object + array + object
	@Test
	public void testArrayPlus2() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("\"4\"+{1,2,3}+4");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.ArrayField);
		logger.debug("Value::::"+token.getTokenType());
		ArrayField af = token.getValue(ArrayField.class);
		assertEquals(af.size(),5);
		assertEquals(af.getValue(0),"4");
		assertEquals(af.getValue(4),4);
	}
	/**
	 * table + table
	 * @throws CloneNotSupportedException
	 * @throws CmsLatteException
	 * @throws CmsLatteFunctionException
	 */
	@Test
	public void testTableInitPlus() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("{{1,2,3}{4,5,6}}+{{7,8,9},{10,11,12}}");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.MatrixField);
		MatrixField mf = token.getValue(MatrixField.class);
		assertEquals(mf.size(),12);
	}
	//table + array 
	@Test
	public void testTableInitPlus2() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("{{1,2,3}{4,5,6}}+{7,8,9,10}");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.MatrixField);
		MatrixField mf = token.getValue(MatrixField.class);
		assertEquals(mf.size(),12);
		assertNull(mf.getValue(0,3));
		assertEquals(mf.getValue(2,3),10);
	}	
	//array + table
	@Test
	public void testTableInitPlus3() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("{7,8,9,10}+{{1,2,3}{4,5,6}}");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.MatrixField);
		MatrixField mf = token.getValue(MatrixField.class);
		assertEquals(mf.size(),12);
		assertNull(mf.getValue(1,3));
		assertEquals(mf.getValue(0,3),10);
	}
	//table + object
	@Test
	public void testTableInitPlus4() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("{{1,2,3}{4,5,6}}+7");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.MatrixField);
		MatrixField mf = token.getValue(MatrixField.class);
		assertEquals(mf.size(),9);
		assertNull(mf.getValue(2,1));
		assertEquals(mf.getValue(2,0),7);
	}
	//table + object
	@Test
	public void testTableInitPlus5() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		TokenStack ts  = ms.scan("7+{{1,2,3}{4,5,6}}");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.MatrixField);
		MatrixField mf = token.getValue(MatrixField.class);
		assertEquals(mf.size(),9);
		assertNull(mf.getValue(0,1));
		assertEquals(mf.getValue(0,0),7);
	}

	@Test
	public void testBoolean1() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		String[] ss = new String[]{
				"!false && true ",
				"(1==1)&&(2==2)",
				"true",
				"(2>1 || 3<4)",
				"(2>1 or 3<4)",
				"((1<2) and (2<3) && 4<5)",
				"((2<=3) && (3<=3)",
				"!(2>3) && !(2>3)&&(3<5)",
				"true==!false",
				"now()==now()"
		};
		
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		for (String s : ss) {
			TokenStack ts  = ms.scan(s);
			logger.debug(ts.toString());
			Token token = susik.eval(ts);
			assertEquals(token.getTokenType(),TokenType.Boolean);
			Boolean b = token.getBoolean();
			assertTrue(b);			
		}
	}
	@Test
	public void testCalc1() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		String[] ss = new String[]{
				"2*3+1",
				"3*2+1",
				"(3*2)+1",
				"10/3+4",
				"10%3+2*3"
		};
		
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		for (String s : ss) {
			TokenStack ts  = ms.scan(s);
			logger.debug(ts.toString());
			Token token = susik.eval(ts);
			assertEquals(token.getTokenType(),TokenType.Integer);
			assertTrue(token.getInteger().equals(7));
			
		}
	}
	@Test
	public void testEquals() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		String[] ss = new String[]{
				"{{1,2,3}{4,5,6}}=={{1,2,3}{4,5,6}}",
				"1==1",
				"2.5==2.5",
				"true==!false",
				"{1,2,3}=={1,2,3}"
		};
		for (String s : ss) {
			TokenStack ts  = ms.scan(s);
			logger.debug(ts.toString());
			Token token = susik.eval(ts);
			assertEquals(token.getTokenType(),TokenType.Boolean);
			assertTrue(token.getBoolean());
		}
	}
	@Test
	public void testEquals2() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		String[] ss = new String[]{
				"{{1,2,3}{4,5,6}}=={{1,2,3}{4,5,\"6\"}}",
				"1==\"1\"",
				"2.5==2.51",
				"true==false",
				"{1,2,3}=={1,2,3,4}"
		};
		for (String s : ss) {
			TokenStack ts  = ms.scan(s);
			logger.debug(ts.toString());
			Token token = susik.eval(ts);
			assertEquals(token.getTokenType(),TokenType.Boolean);
			assertFalse(token.getBoolean());
		}
	}
	@Test
	public void testMinusInteger() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		//TokenStack ts  = ms.scan("-1");
		TokenStack ts  = ms.scan("-1");
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.Integer);
		assertTrue(token.getValue(Integer.class)==-1);
		
	}
	@Test
	public void testMinusInteger2() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();
		//TokenStack ts  = ms.scan("-1");
		//TokenStack ts  = ms.scan("0+(-1)");
		//TokenStack ts  = ms.scan("(1-1+2-3+1+1)");
		TokenStack ts  = ms.scan("3+1*2-1)");
		
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.Integer);
		logger.debug("------------->"+token.getValue(Integer.class));
		assertTrue(token.getValue(Integer.class)==4);
	}
	/**
	 * date 비교 > <
	 * @throws CloneNotSupportedException
	 * @throws CmsLatteException
	 * @throws CmsLatteFunctionException
	 */
	@Test
	public void testDateCompare() throws CloneNotSupportedException, CmsLatteException, CmsLatteFunctionException{
		Susik susik = new Susik(null);
		MunjangScanner ms = new MunjangScanner();

		TokenStack ts  = ms.scan("date(\"2012-12-05\") < date(\"2012-12-06\")  ");
		
		logger.debug(ts.toString());
		Token token = susik.eval(ts);
		assertEquals(token.getTokenType(),TokenType.Boolean);
		logger.debug("------------->"+token.getValue(Integer.class));
		assertTrue(token.getValue(Boolean.class));
		
		ts = ms.scan("date(\"2012-12-05\") + 3");
		token = susik.eval(ts);
		assertEquals(LatteCommonFunctions.toString(token.getDate(),"yyyy-MM-dd"),"2012-12-08");
		
		ts = ms.scan("date(\"2012-12-05\") - 3");
		token = susik.eval(ts);
		assertEquals(LatteCommonFunctions.toString(token.getDate(),"yyyy-MM-dd"),"2012-12-02");
		
		ts = ms.scan("date(\"2012-12-05\") - date(\"2012-12-04\")");
		token = susik.eval(ts);
		assertTrue(token.getInteger()==1);
		ts = ms.scan("date(\"2012-12-04\") - date(\"2012-12-05\")");
		token = susik.eval(ts);
		assertTrue(token.getInteger()==-1);
		ts = ms.scan("date(\"2012-12-04\") - date(\"2012-12-04\")");
		token = susik.eval(ts);
		assertTrue(token.getInteger()==0);
	}
	
}
