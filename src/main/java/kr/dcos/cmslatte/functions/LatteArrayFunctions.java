package kr.dcos.cmslatte.functions;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.utils.LatteUtil;

public class LatteArrayFunctions extends LatteFunctionBase{
	
	/**
	 * 2개의 ArrayField를 더한다 
	 * cArray = aArray + bArray; 
	 * 새로운 cArray가 만들어진다.
	 * @param args
	 * @return
	 * @throws CmsLatteException 
	 */
	public static ArrayField arrayPlus(Object... args) throws CmsLatteException{
		String functionName = "arrayPlus";
		String protoType = String.format("array=%s(array1,array2)",functionName);
		
		//
		// argument length check
		//
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		String key = args[0].toString()+args[1].toString();
		ArrayField newArrayField = new ArrayField(LatteUtil.getUniqueKey(key));

		newArrayField.append(args[0]);
		newArrayField.append(args[1]);
		return newArrayField;
	}
	/**
	 * 배열의 마지막 index를 리턴한다
	 * @param args
	 * @return
	 * @throws CmsLatteException
	 */
	public static Integer lastIndex(Object... args) throws CmsLatteFunctionException{
		String functionName = "lastIndex";
		String protoType = String.format("int=%s(array|table)",functionName);
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isArray(args[0])){
			ArrayField af = (ArrayField)args[0];
			return af.getLastIndex();
		}else if(isMatrix(args[0])){
			MatrixField mf = (MatrixField)args[0];
			return mf.getLastIndex();
		}else{
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);
		}
		
	}
	/**
	 * array에 object를 추가한다. 
	 * 새로운 array가 만들어지는 것이 아니라 args[0]에 추가하고 args[0]을 리턴한다
	 * @param args
	 * @return
	 * @throws CmsLatteException
	 */
	public static ArrayField append(Object... args) throws CmsLatteFunctionException{
		String functionName = "append";
		String protoType = String.format("array=%s(array,object)",functionName);
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isArray(args[0])){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);	
		}
		ArrayField af = (ArrayField)args[0];
		try {
			af.append(args[1]);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
		return af;
	}
	/**
	 * array 의 특정 index에 추가한다
	 * insert(array,2,"abc");
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static ArrayField insert(Object... args) throws CmsLatteFunctionException{
		String functionName = "insert";
		String protoType = String.format("array=%s(array,index,disbd)",functionName);
		if(argsLength(args.length,3)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isArray(args[0])){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);	
		}
		ArrayField af = (ArrayField) args[0];
		int index = (Integer)args[1];
		if(!isDISBD(args[2])){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);
		}
		try {
			af.insert(index,args[2]);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
		return af;
	}
	/**
	 * index 요소를 삭제한다.
	 * 1개씩 pack된다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static ArrayField remove(Object... args) throws CmsLatteFunctionException{
		String functionName = "remove";
		String protoType = String.format("array=%s(array,index)",functionName);
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isArray(args[0])){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);	
		}
		ArrayField af = (ArrayField) args[0];
		if(!isInteger(args[1])){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);
		}
		try {
			int index = (Integer)args[1];
			af.remove(index);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
		return af;
	}
	/**
	 * 부분 Array를 리턴한다.
	 * sub(array, beginIndex[,endIndex]);
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static ArrayField sub(Object... args) throws CmsLatteFunctionException{
		String functionName = "sub";
		String protoType = String.format("array=%s(array, beginIndex[,endIndex])",functionName);
		if(argsLength(args.length,2,3)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isArray(args[0])){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);	
		}
		ArrayField af = (ArrayField) args[0];
		if(!isInteger(args[1])){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);
		}
		int beginIndex = (Integer)args[1];
		int endIndex = af.getLastIndex();
		if(args.length==3){
			if(!isInteger(args[2])){
				throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);
			}
			endIndex = (Integer)args[2];
		}
		try {
			return af.sub(beginIndex,endIndex);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
	}
	/**
	 * ArrayField에 사용하며 [aaa,bbb,ccc] = > 'aaa','bbb','ccc'의 형태로 편하게 바꾸기 위해서 사용한다.
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static String joinToString(Object... args) throws CmsLatteFunctionException{
		String functionName = "join";
		String protoType = String.format("string=%s(array,[seperator=',',left_str,right_str])",functionName);
		if(argsLength(args.length,1,2,3,4)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isArray(args[0])){
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);	
		}
		ArrayField af = (ArrayField) args[0];
		String seperator = ",";
		String left = "";
		String right = "";
		if(args.length == 2){
			seperator = (String)args[1];
		}
		if(args.length == 3){
			seperator = (String)args[1];
			left = (String)args[2];
			right = (String)args[2];
		}
		if(args.length==4){
			seperator = (String)args[1];
			left = (String)args[2];
			right = (String)args[3];
		}
		StringBuilder sb = new StringBuilder();
		try {

			for (int i = 0; i < af.size(); i++) {
				Object o = af.getValue(i);
				if (o == null) {
					o = "";
				}
				sb.append(left).append(o.toString()).append(right).append(seperator);
			}
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(e.getMessage());
		}
		String resultString = sb.toString();
		if(resultString.length()>0 && resultString.endsWith(seperator)){
			return resultString.substring(0,resultString.length()-seperator.length());
		}
		return resultString;
	}
}
