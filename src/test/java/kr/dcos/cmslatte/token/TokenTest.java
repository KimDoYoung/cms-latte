package kr.dcos.cmslatte.token;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenTest {

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Token t = new Token(TokenType.Integer,10);
		assertEquals(t.getString(),"10");
		
	}
	@Test
	public void test1() {
		Token t = new Token(10,null);
		assertEquals(t.getTokenType(),TokenType.Integer);
		
		t = new Token(-10,null);assertEquals(t.getTokenType(),TokenType.Integer);
		t = new Token(0,null);assertEquals(t.getTokenType(),TokenType.Integer);
		t = new Token(-0,null);assertEquals(t.getTokenType(),TokenType.Integer);
		t = new Token(00,null);assertEquals(t.getTokenType(),TokenType.Integer);
		
		t = new Token(1.0,null);assertEquals(t.getTokenType(),TokenType.Double);
		t = new Token(0.0,null);assertEquals(t.getTokenType(),TokenType.Double);
		t = new Token(-1.2,null);assertEquals(t.getTokenType(),TokenType.Double);
		t = new Token(1.345,null);assertEquals(t.getTokenType(),TokenType.Double);
		
		t = new Token(true,null);assertEquals(t.getTokenType(),TokenType.Boolean);
		t = new Token(false,null);assertEquals(t.getTokenType(),TokenType.Boolean);

		t = new Token(1);assertEquals(t.getTokenType(),TokenType.Integer);
		t = new Token(1.0);assertEquals(t.getTokenType(),TokenType.Double);
		t = new Token(false);assertEquals(t.getTokenType(),TokenType.Boolean);
		t = new Token("abc");assertEquals(t.getTokenType(),TokenType.String);
		t = new Token("!");assertEquals(t.getTokenType(),TokenType.String);
	  
	}
}
