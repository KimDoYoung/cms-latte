package kr.dcos.cmslatte.functions;

import java.text.MessageFormat;

import org.hamcrest.core.IsNull;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.utils.LatteUtil;


public class LatteStringFunctions  extends LatteFunctionBase {
	
//	private static Logger logger = LoggerFactory
//			.getLogger(PredefinedStringFunctions.class);
	

	public static String trim(Object... args) throws CmsLatteFunctionException{
		String protoType = "string=trim(string)";
		String functionName = "trim";
		//
		// argument length check
		//
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		//
		// argument type check
		//
		if(isString(args[0])){
			String s = args[0].toString();
			return s.trim();
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	public static String leftTrim(Object... args) throws CmsLatteFunctionException{
		String protoType = "string=leftTrim(string)";
		String functionName = "leftTrim";
		//
		// argument length check
		//
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		//
		// argument type check
		//
		if(isString(args[0])){
			String s = args[0].toString();
			return s.replaceAll("^\\s+", "");
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	public static String rightTrim(Object... args) throws CmsLatteFunctionException{
		String protoType = "string=leftTrim(string)";
		String functionName = "leftTrim";
		//
		// argument length check
		//
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		//
		// argument type check
		//
		if(isString(args[0])){
			String s = args[0].toString();
			return s.replaceAll("\\s+$", "");
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	
	/**
	 * java String의 substring과 같다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static String substring(Object... args) throws CmsLatteFunctionException {
		String protoType = "string=substring(string,beginIndex[,endIndex])";
		String functionName = "substring";
		//
		//args -> local variables
		//
		String s;
		int beginIndex=0,endIndex=Integer.MAX_VALUE;
		//
		//check argument length
		//
		if(argsLength(args.length, 2,3)==false){ //2 또는 3 이 아니면
			throw new CmsLatteFunctionException(functionName, protoType,ERROR_ARGS_LENGTH);
		}
		
		if (args[0] instanceof String ) {
			 s = args[0].toString();
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		//
		// arg갯수에 따라서 각 인자들의 값을 정한다.
		//
		if (args.length == 2 ) {
			if(isNotInteger(args[1])){
				throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
			}
			beginIndex = (Integer)args[1];
			endIndex = s.length();
		}else if (args.length == 3) {
			if(isNotInteger(args[1]) || isNotInteger(args[2]) ){
				throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
			}
			beginIndex = (Integer)args[1];
			endIndex = (Integer)args[2];
		}

		return s.substring(beginIndex,endIndex);
	}
	public static String toUpperCase(Object... args) throws CmsLatteFunctionException{
		String protoType = "string=toUpperCase(string)";
		String functionName = "toUpperCase";
		//
		// argument length check
		//
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		//
		// argument type check
		//
		if(isString(args[0])){
			String s = args[0].toString();
			return s.toUpperCase();
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	public static String toLowerCase(Object... args) throws CmsLatteFunctionException{
		String protoType = "string=toLowerCase(string)";
		String functionName = "toLowerCase";
		//
		// argument length check
		//
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		//
		// argument type check
		//
		if(isString(args[0])){
			String s = args[0].toString();
			return s.toLowerCase();
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	/** 
	 * 앞쪽에 뒷쪽에 문자를 붙인다.
	 * pad("abc","[","]"); => [abc]를 리턴한다
	 * 
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static String pad(Object... args) throws CmsLatteFunctionException{
		String functionName = "pad";
		String protoType = String.format("string=%s(string,startPadStr,endPadStr)",functionName);
		// argument length check
		if(argsLength(args.length,3)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		// argument type check
		if(isString(args[0]) && isString(args[1]) && isString(args[2])){
			String s = args[0].toString();
			String start  = args[1].toString();
			String end = args[2].toString();
			return start + s + end;
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	/**
	 * 문자열의 capital 문자열을 보낸다
	 * "abc" => Abc, "abc def ghi" -> Abc Def Ghi 를 리턴한다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException 
	 */
	public static String capital(Object... args) throws CmsLatteFunctionException {
		String functionName = "capital";
		String protoType = String.format("string=%s(string)",functionName);
		// argument length check
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		// argument type check
		if(isString(args[0])){
			char[] s = args[0].toString().toCharArray();
			StringBuilder sb = new StringBuilder();
			boolean isFirstChar = false;
			int i=0;
			for (char c : s) {
				char ch = c;
				if(Character.isLetter(c)){
					ch = Character.toLowerCase(c);
					if(isFirstChar || i++==0){
						ch = Character.toUpperCase(c);
						isFirstChar = false;
					}
				}else if(Character.isWhitespace(c)){
					ch = c;
					isFirstChar=true;
				}
				sb.append(ch);
			}
			return sb.toString();	
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	/**
	 * java MessageFormat을 이용해서 format을 구현한다
	 * format("{0}-{1,number,##.#}",a,23.5);
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static String format(Object... args) throws CmsLatteFunctionException {
		String functionName = "format";
		String protoType = String.format("string=%s(formatstring,arg1,arg2...)",functionName);
		if(args.length<=1){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		try {
			Object[] argsArray = new Object[args.length - 1];
			for (int i = 0, j = 1; j < args.length; j++, i++) {
				argsArray[i] = args[j];
			}
			String formatString = args[0].toString();
			MessageFormat mf = new MessageFormat(formatString);
			return mf.format(argsArray);
		} catch (Exception e) {
			throw new CmsLatteFunctionException(functionName, protoType,
					e.getMessage());
		}
	}
	/**
	 * string과 array에서 찾아서 index를 리턴한다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static Integer indexOf(Object... args) throws CmsLatteFunctionException{
		String functionName = "indexOf";
		String protoType = String.format("integer=%s(string|array,object)",functionName);
		if(argsLength(args.length,2,3)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		Object key = null;
		int startIndex = 0;
		
		if (args.length == 3){
			startIndex = Integer.parseInt(args[2].toString());
		}
		if(isString(args[0])){
			key = args[1];
			return (args[0]).toString().indexOf((String)key,startIndex);
		}else if(isArray(args[0])){
			try {
				key = args[1];
				ArrayField af = (ArrayField) args[0];
				return af.indexOf(key, startIndex);
			} catch (CmsLatteException e) {
				throw new CmsLatteFunctionException(functionName, protoType,
						ERROR_ARGTYPE_MISMATCH);
			}
		}else{
			throw new CmsLatteFunctionException(functionName, protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
	}
	/**
	 * 문자열대치를 한다, java의 replaceAll이 아니다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static String replace(Object... args) throws CmsLatteFunctionException{
		String functionName = "replace";
		String protoType = String.format("string=%s(string,oldStr,newStr)",functionName);
		if(argsLength(args.length,3,4)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])&&isString(args[1])&&isString(args[2])){
			String org = (String)args[0];
			String oldStr = (String)args[1];
			String newStr = (String)args[2];
			if(args.length == 4 && isBoolean(args[3])){
				return org.replaceAll(oldStr,newStr);
			}else{
				return org.replace(oldStr,newStr);
			}
		}else{
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGTYPE_MISMATCH);
		}
	}
	/*
	 * split
	 * 
	 */
	public static ArrayField split(Object... args) throws CmsLatteFunctionException{
		String functionName = "split";
		String protoType = String.format("string=%s(string,regex)",functionName);
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])&&isString(args[1])){
			ArrayField af = new ArrayField(LatteUtil.getUUID());
			String org = args[0].toString();
			String regex = args[1].toString();
			String[] tmp = org.split(regex);
			try{
				af.append(tmp);
			}catch(CmsLatteException e){
				throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
			}
			return af;
		}else{
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGTYPE_MISMATCH);
		}
	}
	/**
	 * java string startWith
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static Boolean startsWith(Object... args) throws CmsLatteFunctionException{
		String functionName = "startWith";
		String protoType = String.format("string=%s(string,regex)",functionName);
		if(argsLength(args.length,2,3)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])&&isString(args[1])){
			String s= (String)args[0];
			String k= (String)args[1];
			int offset = 0;
			if(args.length==3 && isInteger(args[2])){
				offset = (Integer)args[2];
			}
			return s.startsWith(k,offset);
		}else{
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGTYPE_MISMATCH);
		}
	}
	/**
	 * java string endWith
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static Boolean endsWith(Object... args) throws CmsLatteFunctionException{
		String functionName = "endsWith";
		String protoType = String.format("string=%s(string,regex)",functionName);
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])&&isString(args[1])){
			String s= (String)args[0];
			String k= (String)args[1];
			return s.endsWith(k);
		}else{
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGTYPE_MISMATCH);
		}
	}
	/**
	 * propertyName : BOARD_ID와 같은 문자열을 boardId로 바꾼다
	 * 바꾸는 규칙은 첫글자는 소문자 , 언더바는 지우고 언더바 다음글짜만 대문자
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static String propertyName(Object... args) throws CmsLatteFunctionException{
		String functionName = "propertyName";
		String protoType = String.format("string=%s(string)",functionName);
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])){
			String name= (String)args[0];
			String ss = name.toUpperCase();
			String[] tmp = ss.split("_");
			String property = "";
			for (String s : tmp) {
				property += s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase();
			}
			return property.substring(0,1).toLowerCase()+property.substring(1);			
			
		}else{
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGTYPE_MISMATCH);
		}
	}

	public static String concat(Object... args) throws CmsLatteFunctionException{
		String functionName = "concat";
		String protoType = String.format("string=%s(object...)",functionName);
		StringBuilder sb = new StringBuilder();
		for (Object object : args) {
			sb.append(LatteCommonFunctions.toString(object));
		}
		return sb.toString();
	}
}
