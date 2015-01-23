package kr.dcos.cmslatte.core;

import java.util.Iterator;
import java.util.Stack;

import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;

public class Postfixer {

	public Postfixer(){
	}
	
	public static Stack<Token> getPostfixStack(TokenStack infix){
//	public static TokenStack getPostfixStack(TokenStack infix){
		if (infix == null) return null;
		TokenStack postfix = new TokenStack();
		TokenStack tmp = new TokenStack();
		
		Iterator<Token> it = infix.getIterator();
	
         while(it.hasNext())
         {
        	 Token token = it.next();
             switch (token.getTokenType())
             {
                 case LeftParen:
                     tmp.push(token);
                     break;
                 case Integer:
                 case String:
                 case Double:
                     postfix.push(token);
                     break;
                 case Divide:
                 case Multiple:
                 case Minus:
                 case Plus:
                 case Mod:
                 case LogicalEqual:
                 case LogicalNotEqual:
                 case LessThan:
                 case GreatThan:
                 case GreatThanOrEqual:
                 case LessThanOrEqual:
                 case LogicalAnd: //check
                 case LogicalOr:
                 case Not:
                     if (tmp.isEmpty())
                     {
                         tmp.push(token);
                     }
                     else
                     {
                         if (token.getIcp() > tmp.peek().getIsp())
                         {
                             tmp.push(token);
                         }
                         else
                         {
                             while (!tmp.isEmpty())
                             {
                                 Token t1 = tmp.pop();
                                 if (token.getIcp() <= t1.getIsp())
                                 {
                                     postfix.push(t1);
                                     tmp.push(token);
                                     break;
                                 }
                                 else
                                 {
                                     tmp.push(t1);
                                     tmp.push(token);
                                     break;
                                 }
                             }
                         }
                     }
                     break;
                 case RightParen:
                     while (!tmp.isEmpty())
                     {
                         Token t = tmp.pop();
                         if (t.getTokenType().equals(TokenType.LeftParen))
                         {
                             break;//무시
                         }
                         else
                         {
                             postfix.push(t);
                         }
                     }
                     break;
                 default:
                     postfix.push(token);
                     break;
             }
         }
         while (!tmp.isEmpty())
         {
             postfix.push(tmp.pop());
         }
         //역으로 해서 리턴한다
//         TokenStack stack = new TokenStack();
//         for(int i=postfix.size()-1;i>=0;i--){
//         	stack.push(postfix.get(i));
//         }
//         return stack;
         Stack<Token> stack = new Stack<Token>();
         for(int i=postfix.size()-1;i>=0;i--){
        	 stack.push(postfix.get(i));
         }
         return stack;
	}
	
}
