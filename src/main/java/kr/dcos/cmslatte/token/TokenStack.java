package kr.dcos.cmslatte.token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Administrator
 *
 */
public class TokenStack {
	private int index; //code sequence
	private List<Token> list;
	//
	//constructor
	//
	public TokenStack(){
		list = new ArrayList<Token>();
	}
	//
	//getter,setter
	//
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	
	public boolean isEmpty(){
		return (list.size() == 0);
	}
	public int size(){
		return list.size();
	}
	public void push(Token token){
		if(token == null)return;
		list.add(token);
	}
	
	public Token pop(){
		if(isEmpty())return null;
		Token token;
		try {
			token = (Token)list.get(list.size()-1).clone();
			list.remove(list.size()-1);
		} catch (CloneNotSupportedException e) {
			token = null;
		}
		
		return token;
//		if (_list.Count == 0) return null;
//          else
//          {
//              EmulToken token = (EmulToken) _list[Count - 1].Clone();
//
//              _list.RemoveAt(Count - 1);
//              return token;
//
//          }
	}
	public Iterator<Token> getIterator(){
		return list.iterator();
	}
	public TokenStack subStack(int startIndex, int endIndex)
    {
        TokenStack newstack = new TokenStack();
        newstack.setIndex(this.index);
        for(int i=startIndex;i<=endIndex;i++)
        {
            newstack.push(list.get(i));
        }
        return newstack;
    }
    public TokenStack subStack(int startIndex)
    {
        TokenStack newstack = new TokenStack();
        newstack.setIndex(this.index);
        for (int i = startIndex; i < list.size(); i++)
        {
            newstack.push(list.get(i));
        }

        return newstack;
    }
	public Token peek() {
		if(isEmpty())return null;
		return list.get(list.size()-1);
	}
    
	public Token get(int idx) {
		if(idx>=0&&idx< list.size()){
			return list.get(idx);
		}
		return null;
	}
	public void clear() {
		list.clear();
	}
	public boolean isContain(TokenType tokenType) {
		for (Token token : list) {
			if(token.getTokenType().equals(tokenType)){
				return true;
			}
		}
		return false;
	}
	@Override
    public String toString(){
		if(list.size() == 0) return "token stack empty";
    	StringBuilder sb = new StringBuilder();
    	for(int i=list.size()-1;i>=0;i--){
    		Token token = list.get(i);
    		sb.append(token.toString());
    		sb.append("\n");    		
    	}
    	return sb.toString();
    }
	/**
	 * index에 해당하는 토큰을 버린다
	 * @param index
	 */
	public void remove(int index) {
		if(index<0 || index>=size()){
			return;
		}
		list.remove(index);
	}
	/**
	 * tokenType을 찾아서 index를 리턴한다. 없으면 -1
	 * @param tokenType
	 * @return
	 */
	public int indexOf(TokenType tokenType) {
		for (int i=0;i<list.size();i++){
			if(list.get(i).getTokenType() == tokenType){
				return i;
			}
		}
		return -1;
	}
	/**
	 * stack을 imsi 또는 cache에 저장할 때의 key로 사용할 문자열
	 * @return
	 */
	public String getHashKey(){
		String key = (new Integer(this.index)).toString()+","+this.toString();
		int i =  key.hashCode();
		return ((new Integer(i)).toString());
	}
}
