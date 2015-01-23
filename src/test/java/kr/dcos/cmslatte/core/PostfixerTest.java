package kr.dcos.cmslatte.core;

import java.util.Stack;

import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenTable;
import kr.dcos.cmslatte.token.TokenType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostfixerTest {

private static Logger logger = LoggerFactory.getLogger(PostfixerTest.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		//Postfixer postfixer = new Postfixer();
		TokenStack infix = new TokenStack();
		infix.push(new Token("a",TokenType.Integer)); 
		infix.push(TokenTable.getInstance().findToken("+")); 
		infix.push(new Token("b",TokenType.Integer));
		infix.push(TokenTable.getInstance().findToken("*"));
		infix.push(new Token("c",TokenType.Integer));
		infix.push(TokenTable.getInstance().findToken("-"));
		infix.push(new Token("d",TokenType.Integer));
		//TokenStack postfix = Postfixer.getPostfixStack(infix);
		Stack<Token> postfix = Postfixer.getPostfixStack(infix);
		logger.debug(postfix.toString());
	}
}
