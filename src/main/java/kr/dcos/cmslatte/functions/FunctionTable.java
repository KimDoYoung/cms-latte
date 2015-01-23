package kr.dcos.cmslatte.functions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * predefined functions들을 가지고 있어서
 * latte 소스에 사용된 function들을 수행하게한다
 * 리턴하는 타입이 array or 
 * @author Administrator
 *
 */
class FunctionTable {
	
	//private static Logger logger = LoggerFactory.getLogger(FunctionTable.class);

	private Map<String,ILatteFunction> map = null;
	private Set<String> arraySubFunctions=null;
	private Set<String> matrixSubFunctions=null;
	private Set<String> stringSubFunctions=null;
	private Set<String> integerSubFunctions=null;
	private Set<String> doubleSubFunctions=null;
	private Set<String> dateSubFunctions=null;
	private Set<String> booleanSubFunctions=null;
	//subfunction에서 자신이 뒷쪽에 위치해야하는 펑션 ex: format
	private Set<String> backArgumentFunctions = null;
	/**
	 * constructor
	 */
	public FunctionTable(){
		map = new HashMap<String,ILatteFunction>();
		
		arraySubFunctions= new HashSet<String>();
		matrixSubFunctions=new HashSet<String>();
		stringSubFunctions=new HashSet<String>();
		integerSubFunctions=new HashSet<String>();
		doubleSubFunctions=new HashSet<String>();
		dateSubFunctions=new HashSet<String>();
		booleanSubFunctions=new HashSet<String>();
		
		backArgumentFunctions = new HashSet<String>();
		
		loadFunctions();
	}
	/**
	 * 이미 있는 펑션이라면 삭제한 후에 넣고 , 없는 것이면  추가
	 */
	public void addOrReplace(String functionName,ILatteFunction handler,String subFunctions,boolean isBack){
		if(map.containsKey(functionName)){
			map.remove(functionName);
		}
		map.put(functionName, handler);
		if(subFunctions !=null && subFunctions.length()>0){
			addSubfunc(functionName,subFunctions);	
		}
		if(isBack){
			backArgumentFunctions.add(functionName);
		}
	}
	public void addOrReplace(String functionName,ILatteFunction handler,String subFunctions){
		addOrReplace(functionName, handler,subFunctions,false);
	}
	public void addOrReplace(String functionName,ILatteFunction handler){
		addOrReplace(functionName,handler,null);
	}
	public ILatteFunction get(String functionName){
		if(map.containsKey(functionName)){
			return map.get(functionName);
		}
		return null;
	}
	/**
	 * original 함수명외에 다른 이름으로 똑같은 함수를 호출할 수 있게 한다.
	 * @param orginalName
	 * @param additionalName
	 */
	public void addAliasFunctionName(String orginalName,String additionalName,String subfunc){
		if(map.containsKey(orginalName)){
			map.put(additionalName,get(orginalName));
		}
		if(subfunc != null && subfunc.length()>0){
			addSubfunc(additionalName, subfunc);
		}
	}
	public void addAliasFunctionName(String orginalName,String additionalName){
		addAliasFunctionName(orginalName, additionalName,null);
	}

	/**
	 * load predefined functions
	 */
	private void loadFunctions() {
		loadStringFunctions();
		loadCommonFunctions();
		loadDateFunctions();
		loadArrayFunctions();
		loadMatrixFunctions();
	}

	/**
	 * functionName이 subfunction에서  자기 자신이 뒷쪽에 위치하는 펑션임을 지정한다.
	 * 외부function에서 annotation BackArg에서 설정가능하다.
	 * @param functionName
	 */
	public void setBackArgumentFunction(String functionName){
		if(!backArgumentFunctions.contains(functionName)){
			backArgumentFunctions.add(functionName);
		}
	}
	
	public void addSubfunc(String functionName, String types) {
		String[] typeArray = types.split(",");
		
		for (String t : typeArray) {
			String s= t.toLowerCase().trim();
			if(s.equals("all")){
				addSubfuncAll(functionName);
				continue;
			}
			if(s.equals("disbd")){
				addSubfuncDISBD(functionName);
				continue;
			}
			if(s.equals("array")){
				arraySubFunctions.add(functionName);
			}else if(s.equals("matrix") || s.equals("table")){
				matrixSubFunctions.add(functionName);
			}else if(s.equals("string")){
				stringSubFunctions.add(functionName);
			}else if(s.equals("integer")){
				integerSubFunctions.add(functionName);
			}else if(s.equals("double")){
				doubleSubFunctions.add(functionName);
			}else if(s.equals("date")){
				dateSubFunctions.add(functionName);
			}else if(s.equals("boolean")){
				booleanSubFunctions.add(functionName);
			}
		}
		
	}
	private void addSubfuncDISBD(String functionName) {
		stringSubFunctions.add(functionName);
		integerSubFunctions.add(functionName);
		doubleSubFunctions.add(functionName);
		dateSubFunctions.add(functionName);
		booleanSubFunctions.add(functionName);
	}
	private void addSubfuncAll(String functionName) {
		arraySubFunctions.add(functionName);
		matrixSubFunctions.add(functionName);
		addSubfuncDISBD(functionName);
	}
	
	/**
	 * Date Functions
	 */
	private void loadDateFunctions() {
		addOrReplace("now", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteDateFunctions.now(args);
			}
		});
		addOrReplace("date", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteDateFunctions.date(args);
			}
		});
		addOrReplace("dayOfWeek", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteDateFunctions.dayOfWeek(args);
			}
		},"Date");
		addOrReplace("dateToArray", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteDateFunctions.dateToArray(args);
			}
		},"Date");
		addOrReplace("dayAdd", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteDateFunctions.dayAdd(args);
			}
		},"Date");
		addOrReplace("monthAdd", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteDateFunctions.monthAdd(args);
			}
		},"Date");	
		addOrReplace("daysBetween", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteDateFunctions.daysBetween(args);
			}
		},"Date");			
	}
	/**
	 * matrix functions
	 */
	private void loadMatrixFunctions() {
		addOrReplace("lastRowIndex", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.lastRowIndex(args);
			}
		},"Matrix");
		
		addOrReplace("lastColIndex", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.lastColIndex(args);
			}
		},"Matrix");
		
		addOrReplace("rowCount", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.rowCount(args);
			}
		},"Matrix");
		addAliasFunctionName("rowCount", "rowLength","Matrix");
		addAliasFunctionName("rowCount", "rowSize","Matrix");
		
		addOrReplace("colCount", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.colCount(args);
			}
		},"Matrix");
		addAliasFunctionName("colCount", "colLength","Matrix");
		addAliasFunctionName("colCount", "colSize","Matrix");
		
		addOrReplace("row", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.row(args);
			}
		},"Matrix");
		addOrReplace("col", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.col(args);
			}
		},"Matrix");
		addAliasFunctionName("col", "column","Matrix");
		
		addOrReplace("insertRow", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.insertRow(args);
			}
		},"Matrix");
		addOrReplace("insertCol", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.insertCol(args);
			}
		},"Matrix");
		addOrReplace("removeRow", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.removeRow(args);
			}
		},"Matrix");
		addOrReplace("removeCol", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.removeCol(args);
			}
		},"Matrix");
		addOrReplace("sortMatrix", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteMatrixFunctions.sortMatrix(args);
			}
		},"Matrix");
		addAliasFunctionName("sortMatrix", "sortTable","Matrix");
	}
	/**
	 * Array Functions
	 */
	private void loadArrayFunctions() {
		addOrReplace("lastIndex", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteArrayFunctions.lastIndex(args);
			}
		},"Array,Table");
		addOrReplace("append", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteArrayFunctions.append(args);
			}
		},"Array");
		addOrReplace("insert", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteArrayFunctions.insert(args);
			}
		},"Array");
		addOrReplace("remove", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteArrayFunctions.remove(args);
			}
		},"Array");
		addOrReplace("sub", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteArrayFunctions.sub(args);
			}
		},"Array");	
		addOrReplace("join", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteArrayFunctions.joinToString(args);
			}
		},"Array");	
		addAliasFunctionName("join", "joinToString");
	}
	/**
	 * Common Functions
	 */
	private void loadCommonFunctions() {
		//------------------------------------------------------------
		// Common
		//------------------------------------------------------------
		addOrReplace("length", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.length(args);
			}
		});
		addAliasFunctionName("length", "size");
		addSubfunc("length","Array,Matrix,String");
		addSubfunc("size","Array,Matrix,String");
		
		addOrReplace("typeOf", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.typeOf(args);
			}
		},"All");
		addOrReplace("toString", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.toString(args);
			}
		},"All");
		addOrReplace("toBoolean", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.toBoolean(args);
			}
		},"Boolean,String,Integer");
		addOrReplace("toInteger", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.toInteger(args);
			}
		},"Boolean,String,Double,Integer");
		addOrReplace("toDouble", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.toDouble(args);
			}
		},"Double,Integer,Date,String");
		addOrReplace("toDate", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.toDate(args);
			}
		},"Date,String,Double");
		addOrReplace("toArray", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.toArray(args);
			}
		},"All");
		addOrReplace("toTable", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.toTable(args);
			}
		},"All");
		addAliasFunctionName("toTable","toMatrix","All");
		
		addOrReplace("functionExist", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.functionExist(args);
			}
		});
		addOrReplace("isEmpty", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.isEmpty(args);
			}
		},"All");
		addOrReplace("ifEmpty", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length != 2){
					throw new CmsLatteFunctionException("ifEmpty function need 2 arguments");
				}
				boolean b = LatteCommonFunctions.isEmpty(args[0]);
				return (b) ? args[1] : args[0];
			}
		},"All");		
		addOrReplace("isContain", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteCommonFunctions.isContain(args);
			}
		},"String,Array,Matrix");
	}
	/**
	 * StringFunctions
	 * 
	 */
	private void loadStringFunctions() {
		addOrReplace("trim", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.trim(args);
			}
		},"String");
		addOrReplace("leftTrim", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.leftTrim(args);
			}
		},"String");	
		addAliasFunctionName("leftTrim","ltrim");
		
		addOrReplace("rightTrim", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.rightTrim(args);
			}
		},"String");		
		addAliasFunctionName("rightTrim","rtrim");
		//------------------------------------------------------------
		addOrReplace("substring", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.substring(args);
			}
		},"String");
		addOrReplace("subStr", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length !=3){
					throw new CmsLatteFunctionException("subStr argument count is not valid");
				}
				try{
					int startIndex = (Integer)args[1];
					int count = (Integer)args[2];
					int endIndex = startIndex + count ;
					args[2] = endIndex;
				}catch(Exception e){
					throw new CmsLatteFunctionException("subStr argument type mismatch");
				}
				return LatteStringFunctions.substring(args);
			}
		},"String");		
		//------------------------------------------------------------
		addOrReplace("toUpperCase", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.toUpperCase(args);
			}
		});
		addOrReplace("toLowerCase", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.toLowerCase(args);
			}
		},"String");
	
		addOrReplace("singleQuotation", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length != 1){
					throw new CmsLatteFunctionException("singleQuotation argument number mismatch");
				}
				return LatteStringFunctions.pad(new Object[]{args[0],"'","'"});
			}
		},"String");
		addOrReplace("doubleQuotation", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length != 1){
					throw new CmsLatteFunctionException("doubleQuotation argument number mismatch");
				}
				return LatteStringFunctions.pad(new Object[]{args[0],"\"","\""});
			}
		},"String");
		addOrReplace("firstCharLower", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length==1 && args[0] instanceof String){
					String s = args[0].toString();
					char ch = s.charAt(0);
					if(Character.isLetter(ch)){
						return Character.toLowerCase(ch)+s.substring(1);
					}
					return  s;
				}else{
					throw new CmsLatteFunctionException("firstCharLower argument type mismatch");
				}
			}
		},"String");

		addOrReplace("firstCharUpper", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length==1 && args[0] instanceof String){
					String s = args[0].toString();
					char ch = s.charAt(0);
					if(Character.isLetter(ch)){
						return Character.toUpperCase(ch)+s.substring(1);
					}
					return  s;
				}else{
					throw new CmsLatteFunctionException("firstCharLower argument type mismatch");
				}
			}
		},"String");
		addOrReplace("capital", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.capital(args);
			}
		},"String");
		addOrReplace("propertyString", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				String s = args[0].toString().replaceAll("[-=_]"," ");
				String c = LatteStringFunctions.capital(s).replaceAll("\\s", "");
				if(c.length()>0){
					char ch = c.charAt(0);
					if(Character.isLetter(ch)){
						return Character.toLowerCase(ch)+c.substring(1);
					}
					return  c;
				}
				return "";
			}
		},"String");
		addOrReplace("format", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.format(args);
			}
		},"String",true); //back argument
		addOrReplace("indexOf", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.indexOf(args);
			}
		},"String");
		addOrReplace("replace", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.replace(args);
			}
		},"String");
		addOrReplace("split", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.split(args);
			}
		},"String");
		addOrReplace("startsWith", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.startsWith(args);
			}
		},"String");
		addOrReplace("endsWith", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				return LatteStringFunctions.endsWith(args);
			}
		},"String");
		addOrReplace("left", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length==2 && LatteUtil.isString(args[0]) && LatteUtil.isInteger(args[1])){
					Object[] newArgs = new Object[3];
					newArgs[0]=args[0];
					newArgs[1]=0;
					newArgs[2]= (Integer)args[1];
					return LatteStringFunctions.substring(newArgs);
				}
				throw new CmsLatteFunctionException("left", "string=left(string,length)","invalid argument");
			}
		},"String");
		addOrReplace("right", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length==2 && LatteUtil.isString(args[0]) && LatteUtil.isInteger(args[1])){
					Object[] newArgs = new Object[3];
					newArgs[0]=args[0];
					newArgs[1]= ((String)args[0]).length()-(Integer)args[1];
					newArgs[2]= ((String)args[0]).length();
					return LatteStringFunctions.substring(newArgs);
				}
				throw new CmsLatteFunctionException("right", "string=right(string,length)","invalid argument");

			}
		},"String");
		addOrReplace("cut", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
				if(args.length==2 && LatteUtil.isString(args[0]) && LatteUtil.isInteger(args[1])){
					Object[] newArgs = new Object[3];
					newArgs[0]=args[0];
					int i = (Integer)args[1];
					if(i>0){
						newArgs[1]= i;
						newArgs[2]= ((String)args[0]).length();
					}else if(i<0){
						newArgs[1]= 0;
						newArgs[2]= ((String)args[0]).length()+i;						
					}else{
						return args[0].toString();
					}
					return LatteStringFunctions.substring(newArgs);
				}
				throw new CmsLatteFunctionException("cut", "string=cut(string,length)","invalid argument");

			}
		},"String");
		addOrReplace("concat", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
					return LatteStringFunctions.concat(args);
			}
		},"String");
		addOrReplace("propertyName", new ILatteFunction() {
			public Object invoke(Object... args) throws CmsLatteFunctionException {
					return LatteStringFunctions.propertyName(args);
			}
		},"String");
	}
	/**
	 * 펑션명으로 지원가능한 타입을 문자열로 만들어서 리턴해 준다
	 * @param functionName
	 * @return
	 */
	private String jiwonTypes(String functionName){
		StringBuilder sb = new StringBuilder();
		if(arraySubFunctions.contains(functionName)){
			sb.append("Array,");
		}		
		if(matrixSubFunctions.contains(functionName)){
			sb.append("Matrix,");
		}
		if(stringSubFunctions.contains(functionName)){
			sb.append("String,");
		}
		if(integerSubFunctions.contains(functionName)){
			sb.append("Integer,");
		}
		if(doubleSubFunctions.contains(functionName)){
			sb.append("Double,");
		}
		if(dateSubFunctions.contains(functionName)){
			sb.append("Date,");
		}
		if(booleanSubFunctions.contains(functionName)){
			sb.append("Boolean,");
		}
		String result = sb.toString();
		if(result.length()>0){
			return result.substring(0,result.length()-1);
		}
		return "";
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (Entry<String, ILatteFunction> entry : map.entrySet()) {
			sb.append(entry.getKey()+" : subfunc in type ("+jiwonTypes(entry.getKey())+")");
			sb.append("\n");
		}
		return sb.toString();
	}
	/**
	 * tokenType에 따라서 허용되는 sub function인지체크
	 * @param tokenType
	 * @param functionName
	 * @return
	 */
	public boolean isAllowSubfunction(TokenType tokenType, String functionName) {
		if(tokenType == TokenType.ArrayField){
			return arraySubFunctions.contains(functionName);
		}else if(tokenType == TokenType.MatrixField){
			return matrixSubFunctions.contains(functionName);
		}else if(tokenType == TokenType.String){
			return stringSubFunctions.contains(functionName);
		}else if(tokenType == TokenType.Integer){
			return integerSubFunctions.contains(functionName);
		}else if(tokenType == TokenType.Double){
			return doubleSubFunctions.contains(functionName);
		}else if(tokenType == TokenType.Date){
			return dateSubFunctions.contains(functionName);
		}else if(tokenType == TokenType.Boolean){
			return booleanSubFunctions.contains(functionName);
		}else{
			return false;
		}
	}
	/**
	 * backArgumentFunctions 에 있으면 true 리턴
	 * @param functionName
	 * @return
	 */
	public boolean isBackArgumentFunction(String functionName) {
		return (backArgumentFunctions.contains(functionName));
	}
}
