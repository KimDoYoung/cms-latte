package kr.dcos.cmslatte.exception;

public class CmsLatteFunctionException extends CmsLatteException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3819289920088254573L;
	public CmsLatteFunctionException(String functionName,String prototype,String message){
		super(functionName + " fail , error message: " + message + " prototype:" + prototype);
	}
	public CmsLatteFunctionException(String functionName,String message){
		super(functionName + " fail , error message: " + message);
	}
	public CmsLatteFunctionException(String message){
		super(message);
	}

}
