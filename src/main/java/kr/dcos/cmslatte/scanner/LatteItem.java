package kr.dcos.cmslatte.scanner;

import kr.dcos.cmslatte.token.TokenStack;

/**
 * Template의 code나 text부분을 가지고 있는 item
 * @author Administrator
 *
 */
public class LatteItem {
	public enum LatteItemType { JustString, LatteCode };
	
	private LatteItemType type;
	private String content;
	private TokenStack tokenStack;
	//
	//constructor
	//
	public  LatteItem(LatteItemType type,String content){
		this(type,content,null);
	}
	public  LatteItem(LatteItemType type,String content,TokenStack tokenStack){
		this.type = type;
		this.content = content;
		this.tokenStack = tokenStack;
	}
	//
	//getter,sette
	//
	public LatteItemType getType() {
		return type;
	}
	public void setType(LatteItemType type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TokenStack getTokenStack() {
		return tokenStack;
	}

	@Override
	public String toString(){
		String tokenStackString = "";
		if(tokenStack != null){
			tokenStackString = tokenStack.toString();
		}
		return String.format("type:%s%ncontent:[%s]%n%s",type.toString(),content.toString(),tokenStackString);
	}

}
