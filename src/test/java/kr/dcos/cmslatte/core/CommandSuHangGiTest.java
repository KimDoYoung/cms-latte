package kr.dcos.cmslatte.core;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.field.FieldChangGo;
import kr.dcos.cmslatte.field.FieldName;
import kr.dcos.cmslatte.field.FieldType;
import kr.dcos.cmslatte.scanner.FieldParser;
import kr.dcos.cmslatte.scanner.MunjangScanner;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommandSuHangGiTest {
	private static MunjangScanner ms ;
	private static CommandSuHangGi csg ;
	private static FieldChangGo changgo;
	private static FieldParser fieldParser;
	@Before
	public void setUp() throws Exception {
		ms = new MunjangScanner();
		changgo = new FieldChangGo();
		csg = new CommandSuHangGi(changgo);
		fieldParser = new FieldParser();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws CmsLatteException {
		
		TokenStack ts = ms.scan("a=b=c=9");
		
		csg.doAssign(ts);
		Token result = changgo.getValueToken(new FieldName("a"));
		assertEquals(result.getTokenType(),TokenType.Integer);
		assertTrue(result.getInteger()==9);
	}
	@Test
	public void test1() throws CmsLatteException {
		TokenStack ts = ms.scan("a=9");
		
		csg.doAssign(ts);
		Token result = changgo.getValueToken(new FieldName("a"));
		assertEquals(result.getTokenType(),TokenType.Integer);
		assertTrue(result.getInteger()==9);
	}
	@Test
	public void testArrayInit() throws CmsLatteException {
		TokenStack ts = ms.scan("a1={}");
		csg.doAssign(ts);
		assertNotNull(changgo.getField(new FieldName("a1")));
		assertEquals(changgo.getField(new FieldName("a1")).getType(),FieldType.Array);
		
	}
	@Test
	public void testArrayInit1() throws CmsLatteException {
		TokenStack ts = ms.scan("a1={1,2,3}");
		csg.doAssign(ts);
		assertNotNull(changgo.getField(new FieldName("a1")));
		assertEquals(changgo.getField(new FieldName("a1")).getType(),FieldType.Array);
		assertTrue(changgo.getValueToken(fieldParser.scan("a1[1]")).getInteger()==2);
		
	}
	@Test
	public void testTableInit() throws CmsLatteException {
		TokenStack ts = ms.scan("t1={{}}");
		csg.doAssign(ts);
		assertNotNull(changgo.getField(new FieldName("t1")));
		assertEquals(changgo.getField(new FieldName("t1")).getType(),FieldType.Matrix);
		
	}
	@Test
	public void testTableInit1() throws CmsLatteException {
		TokenStack ts = ms.scan("t1={{1,2,3},{\"a\",\"b\",\"c\"}}");
		csg.doAssign(ts);
		assertNotNull(changgo.getField(new FieldName("t1")));
		assertEquals(changgo.getField(new FieldName("t1")).getType(),FieldType.Matrix);
		assertTrue(changgo.getValueToken(fieldParser.scan("t1[0,0]")).getInteger()==1);
		assertTrue(changgo.getValueToken(fieldParser.scan("t1[1,1]")).getString().equals("b"));
		
	}
}
