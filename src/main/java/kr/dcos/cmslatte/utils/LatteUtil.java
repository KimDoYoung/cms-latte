package kr.dcos.cmslatte.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.functions.FunctionManager;
import kr.dcos.cmslatte.scanner.MunjangPattern;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenTable;
import kr.dcos.cmslatte.token.TokenType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteUtil {
	
	private static Logger logger = LoggerFactory.getLogger(LatteUtil.class);
	

	public static String EmptyString = "";
	public static String shortDateFormat = "yyyy-MM-dd";
	public static String longDateFormat = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat shortSimpleDateFormat = new SimpleDateFormat(shortDateFormat);
	public static SimpleDateFormat longSimpleDateFormat = new SimpleDateFormat(longDateFormat);
	

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	/**
	 * s에 기반해서 Unique한 hashCode를 만들어서 문자열로 리턴한다
	 * @param s
	 * @return
	 */
	public static String getUniqueKey(String s){
		String uuid = getUUID()+s;
		return (new Integer(uuid.hashCode()).toString());
	}
	
	public static boolean isNullOrEmpty(String s){
		if(s == null || s.length()<1){
			return true;
		}
		return false;
	}
	public static boolean isBlankString(String s){
		if(isNullOrEmpty(s.trim()))return true;
		else return false;
	}
	public static boolean isValidDate(String string) {

	    if (isNullOrEmpty(string)){
	      return false;
	    }
	    //set the format to use as a constructor argument
	    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    
	    int len = string.trim().length();
	    SimpleDateFormat dateFormat = null;
	    if ( len == shortDateFormat.length()){
	    	dateFormat = shortSimpleDateFormat;
	    }else if(len == longDateFormat.length()){
	    	dateFormat = longSimpleDateFormat;
	    }else{
	    	return false;
	    }

	    dateFormat.setLenient(false);
	    
	    try {
	      dateFormat.parse(string.trim());
	    }
	    catch (ParseException pe) {
	      return false;
	    }
	    return true;
	  }
	public static boolean isValidDouble(String value) {
		
		if(value == null || value.trim().length()<1) {
			return false;
		}
		try{
			Double.parseDouble(value);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	public static boolean isValidInteger(String value) {
		if(value == null || value.trim().length()<1) {
			return false;
		}
		try{
			Integer.parseInt(value);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	public static boolean isValidBoolean(String value) {
		if(value == null || value.trim().length()<1) {
			return false;
		}
		
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
		    return true;
		} 
		return false;
	}


	public static boolean isControlToken(TokenType tokenType) {
        //  For| To | Step|  If | Elseif | Else| End | Begin
        if (
            tokenType == TokenType.For ||
            tokenType == TokenType.To ||
            tokenType == TokenType.Step ||
            tokenType == TokenType.Down ||
            tokenType == TokenType.If ||
            tokenType == TokenType.Elseif ||
            tokenType == TokenType.Else ||
            tokenType == TokenType.While ||
            tokenType == TokenType.End ||
            tokenType == TokenType.Begin ||
            tokenType == TokenType.Goto ||
            tokenType == TokenType.Switch ||
            tokenType == TokenType.Case ||
            tokenType == TokenType.Default ||
            tokenType == TokenType.Break ||
            tokenType == TokenType.Foreach ||
            tokenType == TokenType.Continue
            ) return true;
        else return false;
	}
    /**
     * value에 따라서 tokentype을 정한다
     * @param value
     * @return
     */
    public static TokenType getTokenTypeWithString(String string){
    	//date->double->integer->boolean->string
    	if( isValidInteger(string)){
    		return TokenType.Integer;
    	}else if(isValidDouble(string)){
    		return TokenType.Double;
    	}else if(isValidDate(string)){
    		return TokenType.Date;
    	}else if(isValidBoolean(string)){
    		return TokenType.Boolean;
    	}else if(isValidFunctionName(string)){
    		return TokenType.Function;
    	}else if(isValidFieldName(string)){
    		return TokenType.Field;
    	}else{
    		return TokenType.String;
    	}
    }


    /**
     * String을 Object형으로 TokenType에 따라서 Object형으로 바꾼다 
     * @param value
     * @param tokenType
     * @return
     */
    public static Object getObjectWithTokenType(String value,TokenType tokenType){
    	Object object = null ;
		switch (tokenType) {
		case Integer:
			object = Integer.parseInt(value);
			break;
		case Double:
			object = Double.parseDouble(value);
			break;
		case String:
		case Field:	
		case Function:
		case ArrayInit:
		case TableInit:
		case Label:
			object = value;
			break;
		case Boolean:
			object = Boolean.parseBoolean(value);
			break;
		case Date:
			DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				object = sdFormat.parse(value);
			} catch (ParseException e) {
				object = null;
			}
			break;
		default:
			break;
		}
		return object;
    }
    public static TokenType getTokenTypeWithObject(Object object){
    	if(object instanceof String){
    		return TokenType.String;
    	}else if(object instanceof Integer){
    		return TokenType.Integer;
    	}else if(object instanceof Boolean){
    		return TokenType.Boolean;
    	}else if(object instanceof Date){
    		return TokenType.Date;
    	}else if(object instanceof Double){
    		return TokenType.Double;
    	}else if(object instanceof ArrayField){
    		return TokenType.ArrayField;
    	}else if(object instanceof MatrixField){
    		return TokenType.MatrixField;
    	}else {
    		return TokenType.Unknown;
    	}
    }
    public static boolean isDISBD(Object object){
    	TokenType t = getTokenTypeWithObject(object);
    	return isDISBD(t);
    }
    public static boolean isDISBD(TokenType tokenType){
    	if(tokenType== TokenType.Integer 
   			 || tokenType == TokenType.String 
      			 || tokenType == TokenType.Double 
      			 || tokenType == TokenType.Boolean 
      			 || tokenType== TokenType.Date){
   			 return true;
   		 }
   		 return false;
    }
	public static boolean isArrayField(Object object) {
		if(object instanceof ArrayField){
			return true;
		}
		return false;
	}
	public static boolean isMatrixField(Object object) {
		if(object instanceof MatrixField){
			return true;
		}
		return false;
	}
	public static boolean isDate(Object object) {
		if(object instanceof Date){
			return true;
		}
		return false;
	}
	public static boolean isInteger(Object object) {
		if(object instanceof Integer){
			return true;
		}
		return false;
	}
	public static boolean isString(Object object) {
		if(object instanceof String){
			return true;
		}
		return false;
	}
	public static boolean isBoolean(Object object) {
		if(object instanceof Boolean){
			return true;
		}
		return false;
	}
	public static boolean isDouble(Object object) {
		if(object instanceof Double){
			return true;
		}
		return false;
	}

    /**
     * 
     * predefined function 인지 체크한다
     * @param functionName
     * @return
     */
    public static boolean isValidFunctionName(String functionName) {
    	if(FunctionManager.getInstance().isCmsFunction(functionName)){
    		return true;
    	}
    	return false;
	}
	/**
     * 허용된 fieldname 인지 체크한다.
     * @param name
     * @return
     */
	public static boolean isValidFieldName(String name) {
        //비어 있다면 
        if (isNullOrEmpty(name)) return false;

        //100글자 이상이라면
        if (name.length() > 100) return false;

        //첫글자는 알파벳 또는 _로 시작
        char[] array = name.toCharArray();
        char ch = Character.toUpperCase(array[0]);
        if (!((ch >= 'A' && ch <= 'Z') || ch == '_'))
        {
            return false;
        }
        //모두 알파벳 또는 숫자로 되어 있어야한다
        for (char c : array) {
			if(Character.isLetter(c)||Character.isDigit(c) || c == '_'){
				; //ok
			}else{
				return false;
			}
		}
        //키워드가 아니어야한다
        Token token = TokenTable.getInstance().findToken(name);
        if(token != null){
        	return false; 
        }
        //함수명이 아니어야한다
        if(FunctionManager.getInstance().isCmsFunction(name)){
        	return false;
        }
        return true;
	}
	/**
	 * name이 label명인지 체크한다
	 * 첫글자가 #로 시작되고 나머지 부분이 fieldName의 규칙에 의해서 만들어져야한다
	 * @param name
	 * @return
	 */
	public static boolean isValidLabelName(String name) {
		if(name.startsWith("#")	&& isValidFieldName(name.substring(1))){
			return true;
		}
		return false;
	}

	public static Integer toInteger(String s) {
		return Integer.parseInt(s);
	}
	/**
	 * stack을 살펴서 어떤 문장에 해당하는지 판별 한다
	 * @param stack
	 */
	public static MunjangPattern getMunjangPattern(TokenStack stack) {
		 MunjangPattern retval = MunjangPattern.Unknown;
		 TokenType tokenType = stack.get(0).getTokenType();
         if(stack.size()==1){
        	 if(isDISBD(tokenType)) { return MunjangPattern.JustValue;}
        	 else if(tokenType == TokenType.Field) { return MunjangPattern.JustField;}
        	 else if(tokenType == TokenType.Label) { return MunjangPattern.Label;}
        	 //else if(tokenType == TokenType.Clear) { return MunjangPattern.Clear;}
        	 else if(tokenType == TokenType.Exit) { return MunjangPattern.Exit;}
        	 else if(tokenType == TokenType.Continue) { return MunjangPattern.Continue;}       	 
         }
    	 if(tokenType == TokenType.Echo) { return MunjangPattern.Echo;}
    	 else if(tokenType == TokenType.EchoLn) { return MunjangPattern.EchoLn;}
    	 else if(tokenType == TokenType.EchoNo) { return MunjangPattern.EchoNo;}
    	 else if(tokenType == TokenType.Save && stack.get(2).getTokenType() == TokenType.To){
    		 return MunjangPattern.Save;
    	 }else if(tokenType == TokenType.Load && stack.get(2).getTokenType()==TokenType.From){
    		 return MunjangPattern.Load;
    	 }else if(tokenType == TokenType.Clear){
    		 return MunjangPattern.Clear;
    	 }
         //콘트롤이라면
         if (LatteUtil.isControlToken(tokenType))
         {
             switch (tokenType)
             {
                 case For:
                     retval = MunjangPattern.For;
                     break;
                 case If:
                     retval = MunjangPattern.If;
                     break;
                 case Begin:
                     retval = MunjangPattern.Begin;
                     break;
                 case End:
                     retval = MunjangPattern.End;
                     break;
                 case Elseif:
                     retval = MunjangPattern.ElseIf;
                     break;
                 case Else:
                     retval = MunjangPattern.Else;
                     break;
                 case While:
                     retval = MunjangPattern.While;
                     break;
                 case Goto:
                     retval = MunjangPattern.Goto;
                     break;
                 case Switch:
                     retval = MunjangPattern.Switch;
                     break;
                 case Case:
                     retval = MunjangPattern.Case;
                     break;
                 case Default:
                     retval = MunjangPattern.Default;
                     break;
                 case Break:
                     retval = MunjangPattern.Break;
                     break;
                 case Foreach:
                     retval = MunjangPattern.Foreach;
                     break;
             }
             return retval;
         }
         else
         {
             //두번째가 assign이면 assing으로 
             if (stack.size() > 1 && stack.get(1).getTokenType() == TokenType.Assign)
             {
                 return MunjangPattern.Assign;
             }
             else
             {
            	 if(stack.isContain(TokenType.Question)){
                         return MunjangPattern.QuestionIf;
                 }
                 return MunjangPattern.Susik;
             }
         }

	}
	/**
	 * 문자열에 해당하는 SimpleDateFormat을 리턴한다
	 * 
	 * @param dateString
	 * @return
	 */
	public static SimpleDateFormat dateFormat(String dateString) {
		if(dateString.trim().length() == shortDateFormat.length()){
			return shortSimpleDateFormat;
		}else if(dateString.trim().length() == longDateFormat.length()){
			return longSimpleDateFormat;
		}else{
			return null;
		}
	}
	public static boolean isPrimitiveType(Object o){
		if(o instanceof String ||
		   o instanceof Double ||
		   o instanceof Boolean ||
		   o instanceof Character ||
		   o instanceof Integer ) {
			return true;
		}
		return false;
	}
	
	public static String typeString(Object object) {
		if(object instanceof String){
			return "String";
		}else if(object instanceof Integer){
			return "Integer";
		}else if(object instanceof Double ){
			return "Double";
		}else if(object instanceof Boolean){
			return "Boolean";
		}else if(object instanceof Date){
			return "Date";
		}else if(object instanceof ArrayField){
			return "Array";
		}else if(object instanceof MatrixField	){
			return "Table";
		}else {
			return null;
		}
	}
	public static int longToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	public static Object ifEmpty(Object value, Object defaultValue) {
		
		if(value instanceof String){
			if(isNullOrEmpty(value.toString())){
				return defaultValue;
			}else{
				return value;
			}
		}else if(value == null){
			return defaultValue;
		}
		return value; 
	}
	/**
	 * Class T의 Property명을 리스트에 담아서 리턴한다
	 * @param <T>
	 * @param t
	 * @return
	 */
	private static <T> List<String> propertyNamesOfClass(T t){
		List<String> list = new ArrayList<String>();
		java.lang.reflect.Field[] arrayField = t.getClass().getDeclaredFields();
		for (java.lang.reflect.Field field : arrayField) {
		
			String propertyName = field.getName();
			if (propertyName.equals("serialVersionUID")
					|| propertyName.startsWith("pk")) {
				continue;
			}
			list.add(propertyName);
		
		}
		return list;
	}
	/**
	 * 
	 * Class의 각 Property를 CmsLatte의 Array 로 만들어서 리턴한다
	 * property값이 null이면 "" 으로 
	 * 모든 property는 는 타입에 관계없이 문자열로 저장된다
	 * @param variableName
	 * @param t
	 * @return
	 */
	public static <T> ArrayField getArrayFieldWithObject(String variableName,T t){

		ArrayField cmsArray = new ArrayField(variableName);

		List<String> properties = propertyNamesOfClass(t);
		for (String propertyName: properties) {
			if(propertyName.equals("this$0")) continue;
			try {
				java.lang.reflect.Field afield;
				afield = t.getClass().getDeclaredField(propertyName);
				afield.setAccessible(true);
				Object value = afield.get(t);
				if(value == null){
					cmsArray.append("");
					logger.debug(propertyName + " is null");;
				}else{
					cmsArray.append(ifEmpty(value.toString(), ""));
					logger.debug(propertyName + " set with value ["
							+ ifEmpty(value, "") + "]-" + value.getClass().toString());
				}
			} catch (NoSuchFieldException e) {
				logger.error("", e);
			} catch (SecurityException e) {
				logger.error("", e);
			} catch (IllegalArgumentException e) {
				logger.error("", e);
			} catch (IllegalAccessException e) {
				logger.error("", e);
			} catch (CmsLatteException e) {
				logger.error("", e);			}
		}
		
		return cmsArray;
	}

	/**
	 * List<T> 의 내용을 CmsLatte의 Matrix로 만들어서 리턴한다
	 * @param list
	 * @return
	 */
	public static <T> MatrixField getMatrixFieldWithList(String variableName,List<T> list) {
		MatrixField matrixField = new MatrixField(variableName);
		int row = 0;
		for (T t : list) {
			ArrayField arrayField = getArrayFieldWithObject("tmp",t);
			try {
				matrixField.insertRow(row, arrayField);
			} catch (CmsLatteException e) {
				logger.error("",e);
			}
			row++;
		}
		return matrixField;
	}
	public static boolean isNumberString(String str) {
		try  
		  {  
		    Double.parseDouble(str);  
		  }  
		  catch(NumberFormatException nfe)  
		  {  
		    return false;  
		  }  
		  return true;  
	}
}
