package kr.dcos.cmslatte.controls;

import kr.dcos.cmslatte.token.TokenStack;

public class WhileController extends FlowController {
	private TokenStack conditionStack;
	
	public TokenStack getConditionStack() {
		return conditionStack;
	}

	public void setConditionStack(TokenStack conditionStack) {
		this.conditionStack = conditionStack;
	}

	public WhileController(){
		super();
	}
}
