package kr.dcos.cmslatte.functions;

import kr.dcos.cmslatte.utils.LatteUtil;

/**
 * predefined function 클래스를  만들 때 유용한 몇가지 함수와 에러 메세지를 가지고 있는 추상클래스
 * 이 클래스에서 상속받아서 만든다.
 * @author Administrator
 *
 */
public abstract class LatteFunctionBase {
	
	public static final String ERROR_ARGS_LENGTH = "invalid argument count";
	public static final String ERROR_ARGTYPE_MISMATCH = "argument type mismatch";
	public static final String ERROR_INVALID_DATEFORMAT = "date format invalid";
	//
	//private
	//
	/**
	 * checkArgsLength 는  argument length가 적당한지 체크한다.
	 * 
	 * @param argsLength
	 * @param integers
	 * @return
	 */
	public static boolean argsLength(int argsLength,Integer...integers){
		for (Integer integer : integers) {
			if(argsLength == integer){
				return true;
			}
		}
		return false;
	}
	public static boolean isArray(Object o){
		return LatteUtil.isArrayField(o);
	}
	public static boolean isMatrix(Object o){
		return LatteUtil.isMatrixField(o);
	}
	public static boolean isString(Object o){
		return LatteUtil.isString(o);
	}
	public static boolean isNotString(Object o){
		return !isString(o);
	}
	public static boolean isInteger(Object o){
		return LatteUtil.isInteger(o);
	}
	public static boolean isNotInteger(Object o){
		return !isInteger(o);
	}
	public static boolean isDouble(Object o) {
		return LatteUtil.isDouble(o);
	}
	public static boolean isBoolean(Object o) {
		return LatteUtil.isBoolean(o);
	}
	public static boolean isDate(Object o) {
		return LatteUtil.isDate(o);
	}
	/**
	 * Date,integer,string,double,boolean 타입중 하나이면 true
	 * @param object
	 * @return
	 */
	public static boolean isDISBD(Object o) {
		return LatteUtil.isDISBD(o);
	}
}
