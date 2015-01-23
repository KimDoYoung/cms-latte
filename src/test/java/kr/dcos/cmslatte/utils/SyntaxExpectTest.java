package kr.dcos.cmslatte.utils;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.token.TokenType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SyntaxExpectTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		SyntaxExpect syntaxExpect = new SyntaxExpect();
		syntaxExpect.add(TokenType.ArrayInit);
		assertTrue(syntaxExpect.match(TokenType.ArrayInit));
		

	}

}
