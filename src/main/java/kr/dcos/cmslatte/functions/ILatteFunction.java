package kr.dcos.cmslatte.functions;

import kr.dcos.cmslatte.exception.CmsLatteFunctionException;

public interface ILatteFunction {
	Object invoke(Object... args) throws CmsLatteFunctionException;
	//String invoke(String s) throws CmsLatteFunctionException;
}
