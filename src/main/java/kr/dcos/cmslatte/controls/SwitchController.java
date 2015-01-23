package kr.dcos.cmslatte.controls;

import kr.dcos.cmslatte.token.TokenStack;

public class SwitchController extends FlowController {
	private TokenStack switchSusikStack;
	

	public TokenStack getSwitchSusikStack() {
		return switchSusikStack;
	}


	public void setSwitchSusikStack(TokenStack switchSusikStack) {
		this.switchSusikStack = switchSusikStack;
	}


	public SwitchController(){
		super();
	}
}
