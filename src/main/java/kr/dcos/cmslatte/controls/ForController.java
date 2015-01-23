package kr.dcos.cmslatte.controls;

import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

/**
 * for 문을 수행하기 위한 Controller
 * for i=1 to 10 step 2  에서 추출한다
 * for FieldName or Assign to susik step susik 으로 구성되면
 * step susik은 생략가능하다.
 * @author Administrator
 *
 */
public class ForController extends FlowController {

	private String indexField=null; 
	private TokenStack indexStack = null;

	private TokenType toOrDownToken;
	private TokenStack toOrDownStack=null;
	private TokenStack stepStack = null;

	//
	//constructor
	//
	public ForController(){
		super();
	}
	//
	// getter,setter
	//
	public String getIndexField() {
		return indexField;
	}

	public void setIndexField(String indexField) {
		this.indexField = indexField;
	}

	public TokenType getToOrDownToken() {
		return toOrDownToken;
	}


	public void setToOrDownTokenType(TokenType toOrDownToken) {
		this.toOrDownToken = toOrDownToken;
	}


	public TokenStack getToOrDownStack() {
		return toOrDownStack;
	}


	public void setToOrDownStack(TokenStack toOrDownStack) {
		this.toOrDownStack = toOrDownStack;
	}
	public TokenStack getStepStack() {
		return stepStack;
	}
	public void setStepStack(TokenStack stepStack) {
		this.stepStack = stepStack;
	}
	public TokenStack getIndexStack() {
		return indexStack;
	}
	public void setIndexStack(TokenStack indexStack) {
		this.indexStack = indexStack;
	}
	//
	//public
	//
	@Override
	public boolean isValid(){
		if(super.isValid()){
			return (LatteUtil.isNullOrEmpty(indexField)==false); 
		}
		return true;
	}
	@Override public String toString(){
		String s = super.toString();
		String n="";
		if(indexField != null){
			n = indexField;
		}
		return String.format("%s, indexFieldName:%s",s,n);
	}
}
