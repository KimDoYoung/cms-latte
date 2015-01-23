package kr.dcos.cmslatte.functions;

import kr.dcos.cmslatte.annotation.CmsLatteFunction;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;

public class Reservation1 extends LatteFunctionBase{
	

	@CmsLatteFunction(anotherName="hide4Char",desc="hide 4 char of name",subApply="string",backArg="false")
	public static String hideName(Object... args) throws CmsLatteFunctionException{
		String functionName = "hideName";
		String protoType = String.format("string=%s(string[,markstring])",functionName);
		// argument length check
		if (argsLength(args.length, 1,2) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if(!isString(args[0]) || (args.length==2 && !isString(args[1]))){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);
		}
		String name = (String)args[0];
		String hideChar = "*";
		if(args.length==2){
			hideChar = (String)args[1]; 
		}
		return new String(new char[4]).replace("\0", hideChar) + name.substring(4);
	}
}