package kr.dcos.cmslatte.functions;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.core.Susik;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.scanner.MunjangScanner;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PredefinedStringFunctionsTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(PredefinedStringFunctionsTest.class);
	


	private static Susik susik;
	private static MunjangScanner ms;
	@Before
	public void setUp() throws Exception {
		susik =new Susik(null);
		ms = new MunjangScanner();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFormat() throws CmsLatteException {
		
		TokenStack ts = ms.scan("format(\"[{0}]-{1,number,##.#}\",\"abc\",94.234)");
		logger.debug(ts.toString());
		Token r = susik.eval(ts);
		assertEquals(r.getString(),"[abc]-94.2");
	}
	@Test
	public void testIndexOf() throws CmsLatteException {
		
		TokenStack ts = ms.scan("indexOf(\"abc abc\",\"abc\",1)");
		logger.debug(ts.toString());
		Token r = susik.eval(ts);
		logger.debug(r.getInteger().toString());
		assertTrue(r.getInteger() == 4);
	}
	@Test
	public void testReplace() throws CmsLatteException {
		
		TokenStack ts = ms.scan("replace(\"[abc abc]\",\"abc\",\"xxx\")");
		logger.debug(ts.toString());
		Token r = susik.eval(ts);
		assertEquals(r.getString(),"[xxx xxx]");
	}
	@Test
	public void testSplit() throws CmsLatteException {
		
		TokenStack ts = ms.scan("split(\"abc,def,ghi\",\",\")");
		logger.debug(ts.toString());
		Token r = susik.eval(ts);
		assertEquals(r.getTokenType(),TokenType.ArrayField);
	}
	@Test
	public void testPropertyName() throws CmsLatteException {
		
		TokenStack ts = ms.scan("propertyName(\"BOARD_ID\")");
		logger.debug(ts.toString());
		Token r = susik.eval(ts);
		assertEquals(r.getTokenType(),TokenType.String);
		assertEquals(r.getString(),"boardId");
	}
}
