package kr.dcos.cmslatte.token;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenTable {
	private static Logger logger = LoggerFactory.getLogger(TokenTable.class);
	private volatile static TokenTable instance;
	private Map<String,Token> map ;
	public static TokenTable getInstance() {
		if (instance == null) {
			synchronized (TokenTable.class) {
				if (instance == null) {
					instance = new TokenTable();
				}
			}
		}
		return instance;
	}
	public boolean isToken(String name){
		return map.containsKey(name);
	}
	public Token findToken(String name){
		if(name == null || name.length()<1)return null;
		if(map.containsKey(name)){
			return map.get(name);
		}
		return null;
	}
	public Token findToken(char ch){
		return findToken(Character.toString(ch));
	}
	private TokenTable() {
		map = new HashMap<String,Token>();
		init();
		logger.debug("-->TokenTable created");
	}
	private void init() {
		
		map.put("for", new Token("for", TokenType.For));
        map.put("to", new Token("to", TokenType.To));
        map.put("step", new Token("step", TokenType.Step));
        map.put("down", new Token("down", TokenType.Down));

        map.put("if", new Token("if", TokenType.If));
        map.put("elseif", new Token("elseif", TokenType.Elseif));
        map.put("else", new Token("else", TokenType.Else));
        map.put("while", new Token("while", TokenType.While));

        map.put("true", new Token(true, TokenType.Boolean));
        map.put("false", new Token(false, TokenType.Boolean));

       

        map.put("begin", new Token("begin", TokenType.Begin));
        map.put("end", new Token("end", TokenType.End));

        map.put("label", new Token("label", TokenType.Label));
        map.put("goto", new Token("goto", TokenType.Goto));

        map.put("switch", new Token("switch", TokenType.Switch));
        map.put("case", new Token("case", TokenType.Case));
        map.put("default", new Token("default", TokenType.Default));
        map.put("break", new Token("break", TokenType.Break));

        map.put("echo",new Token("echo",TokenType.Echo));
        map.put("echoln", new Token("echoln", TokenType.EchoLn));
        map.put("echono", new Token("echono", TokenType.EchoNo)); //아무것도 찍지 않는다
        
        map.put("load", new Token("load", TokenType.Load));
        map.put("from", new Token("from", TokenType.From));
        //map.put("of", new Token("of", TokenType.Of));
        map.put("save", new Token("save", TokenType.Save));
        map.put("clear", new Token("clear", TokenType.Clear));
        map.put("with", new Token("with", TokenType.With));
        map.put("override", new Token("override", TokenType.Override));
        map.put("file", new Token("file", TokenType.File));
        
        //save,load,clear,display
        map.put("output", new Token("output", TokenType.Output));
        map.put("variables", new Token("output", TokenType.Variables));
        map.put("functions", new Token("output", TokenType.Functions));
        
//        map.put("displayvar",new Token("displayvar",TokenType.DisplayVar));
//        map.put("clearvar", new Token("clearvar", TokenType.ClearVar));
//        map.put("savevar", new Token("savevar", TokenType.SaveVar));
 //       map.put("loadvar", new Token("loadvar", TokenType.LoadVar));

        map.put("exit", new Token("exit", TokenType.Exit));

//        map.put("sleep",new Token("sleep",TokenType.Sleep));

        map.put("foreach", new Token("foreach", TokenType.Foreach));
        map.put("in", new Token("in", TokenType.In));
        map.put("continue", new Token("continue", TokenType.Continue));
		
		
        map.put("and", new Token("and", TokenType.LogicalAnd,null,9,9));
        map.put("&&", new Token("&&", TokenType.LogicalAnd,null,9,9));
        map.put("or", new Token("or", TokenType.LogicalOr,null,9,9));
        map.put("||", new Token("||", TokenType.LogicalOr,null,9,9));
        map.put("!", new Token("!", TokenType.Not, null,14, 14));
        
		map.put("=", new Token("=", TokenType.Assign,null));
        map.put("+", new Token("+", TokenType.Plus, null,12, 12));
        map.put("-", new Token("-", TokenType.Minus,null, 12, 12));
        map.put("*", new Token("*", TokenType.Multiple,null,13,13));
        map.put("/", new Token("/", TokenType.Divide, null,13, 13));
        map.put("%", new Token("%", TokenType.Mod,null, 13, 13));
        

        map.put("==", new Token("==", TokenType.LogicalEqual,null, 11, 11));
        map.put(">=", new Token(">=", TokenType.GreatThanOrEqual, null,11, 11));
        map.put("<=", new Token("<=", TokenType.LessThanOrEqual, null, 11, 11));
        map.put("!=", new Token("!=", TokenType.LogicalNotEqual, null,11, 11));
        map.put(">", new Token(">", TokenType.GreatThan, null,11, 11));
        map.put("<", new Token("<", TokenType.LessThan, null, 11, 11));
        
        map.put("(", new Token("(", TokenType.LeftParen,null,20,0));
        map.put(")", new Token(")", TokenType.RightParen,null,19,19));

        map.put("[", new Token("[", TokenType.LeftBracket, null,20, 0));
        map.put("]", new Token("]", TokenType.RightBracket, null,19, 19));

        map.put(",", new Token(",", TokenType.Comma, null,19, 19));
        map.put("?", new Token("?", TokenType.Question, null,19, 19));
        map.put(":", new Token(":", TokenType.Colon, null,19, 19)); 
        
        map.put("++", new Token("++", TokenType.PlusPlus,null, 15, 15));
        map.put("--", new Token("--", TokenType.MinusMinus, null,15, 15));
        map.put(".", new Token(".", TokenType.Period, null,15, 15));
        
        map.put("{", new Token("{", TokenType.LeftBrace, null,0, 0));
        map.put("}", new Token("}", TokenType.RightBrace, null,0, 0));
        
        
		
	}
}
