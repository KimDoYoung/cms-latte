package kr.dcos.cmslatte.controls;

import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.utils.LatteUtil;

public class ForeachController extends FlowController{
	//foreach s in array1 => left : s, right : array1
	//foreach field in susik
	private String leftField=null; 
	private TokenStack rightStack=null;

	//private TokenStack ;
	private int nowIndex=-1;
	
	//
	//constructor
	//
	public ForeachController(){
		super();
	}
	//
	//getter,setter
	//
	public TokenStack getRightStack() {
		return rightStack;
	}
	public void setRightStack(TokenStack rightStack) {
		this.rightStack = rightStack;
	}
	public String getLeftField() {
		return leftField;
	}

	public void setLeftField(String leftField) {
		this.leftField = leftField;
	}


	public int getNowIndex() {
		return nowIndex;
	}

	public void setNowIndex(int nowIndex) {
		this.nowIndex = nowIndex;
	}


	//
	//public
	//
	@Override
	public boolean isValid(){
		if(super.isValid()){
			return (!LatteUtil.isNullOrEmpty(leftField) || rightStack==null); 
		}
		return true;
	}
	@Override public String toString(){
		String s = super.toString();
		return String.format("%s, left:%s,right:%s",s,leftField,rightStack.toString());
	}
	
}
