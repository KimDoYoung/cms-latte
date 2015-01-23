package kr.dcos.cmslatte.token;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenStackTest {

private static Logger logger = LoggerFactory.getLogger(TokenStackTest.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		TokenStack stack = new TokenStack();
		stack.push(new Token("10",TokenType.String));
		stack.push(new Token("20",TokenType.String));
		stack.push(new Token("30",TokenType.String));
		stack.push(new Token(40,TokenType.Integer));
		stack.push(new Token(50,TokenType.Integer));
		Token token = stack.pop();
		assertTrue(token.getInteger()==50);
		assertEquals(stack.size(),4);
		
	}
	@Test
	public void testGetImsiKey() {
		TokenStack stack1 = new TokenStack();
		stack1.push(new Token("10",TokenType.String));
		
		logger.debug(stack1.getHashKey());
		stack1.push(new Token("11",TokenType.String));
		logger.debug(stack1.getHashKey());
		
	}

}
