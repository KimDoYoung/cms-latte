package kr.dcos.cmslatte.functions;

import java.util.ArrayList;
import java.util.List;

import kr.dcos.cmslatte.token.Token;

/**
 * predefined function을 FunctionExecuter가 수행하기 위해서
 * 
 * @author Administrator
 *
 */
public class FunctionArguments {
	private String functionName;

	private List<Object> argumentList;
	/**
	 * constructor of FunctionArguments
	 */
	public FunctionArguments(){
		this(null);
	}
	public FunctionArguments(String functionName){
		this.functionName = functionName;
		argumentList = new ArrayList<Object>();
	}
	//getter,setter
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	//public
	public void addArgument(Object object){
		argumentList.add(object);
	}
	public void addArgument(Token token) {
		addArgument(token.getValue());
	}
	public void insertArgument(int index,Object object){
		argumentList.add(index,object);
	}
	public void insertArgument(int index,Token token){
		insertArgument(index,token.getValue());
	}
	public String getFunctionName() {
		return functionName;
	}
	public Object[] getArgumentArray() {
		return argumentList.toArray();
	}
	
}
