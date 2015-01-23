package kr.dcos.cmslatte.utils;

import java.util.HashSet;

import kr.dcos.cmslatte.token.TokenType;

/**
 * syntax check를 위해서 값을 저장해 두고 비교한다
 * 다음에 나와야할 TokenType들을 저장해 두고 비교한다
 * @author Administrator
 *
 */
public class SyntaxExpect {
	private HashSet<TokenType> set;
	//
	//constructor
	//
	public SyntaxExpect(){
		set = new HashSet<TokenType>();
	}
	public boolean match(TokenType tokenType){
		return set.contains(tokenType);
	}
	public void add(TokenType t){
		set.add(t);
	}
	public void clear(){
		set.clear();
	}
//	public void clearAddAll(Collection<? extends TokenType> tokenTypes) {
//		set.clear();
//		set.addAll(tokenTypes);
//		
//	}
//	public void clearAddArray(TokenType[] tokenTypes) {
//		set.clear();
//		for (TokenType tokenType : tokenTypes) {
//			set.add(tokenType);
//		}
//	}
}
