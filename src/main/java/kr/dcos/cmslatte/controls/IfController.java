package kr.dcos.cmslatte.controls;

import kr.dcos.cmslatte.token.TokenStack;

/**
 * if 문을 콘트롤하기 위한 정보를 담고 있는 클래스
 * if a==b begin
 * ...
 * elseif a==c begin
 * .. break;
 * else begin
 * ..
 * end;
 * @author Administrator
 *
 */
public class IfController extends FlowController {

	private int finalEndIndex;
	private TokenStack conditionStack;
	//
	//생성자
	//
	public IfController(int beginIndex,int endIndex,int finalEndIndex,TokenStack conditionStack){
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.finalEndIndex = finalEndIndex;
		this.conditionStack = conditionStack;
	}
	public IfController() {
		this(-1,-1,-1,null);
	}
	//
	// getter,setter
	//
	public int getFinalEndIndex() {
		return finalEndIndex;
	}

	public void setFinalEndIndex(int finalEndIndex) {
		this.finalEndIndex = finalEndIndex;
	}

	public TokenStack getConditionStack() {
		return conditionStack;
	}

	public void setConditionStack(TokenStack conditionStack) {
		this.conditionStack = conditionStack;
	}

	//
	// public functions
	//
	@Override
	public boolean isValid(){
		if(super.isValid()){
			if(finalEndIndex<0){
				return false;
			}
			if(endIndex>finalEndIndex){
				return false;
			}
		}
		return true;
	}
	@Override
	public String toString(){
		String s = super.toString();
		return String.format("%s, finalEndIndex:%d",s,finalEndIndex);
	}
	
}
