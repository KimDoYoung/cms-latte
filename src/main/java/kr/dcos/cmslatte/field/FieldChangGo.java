package kr.dcos.cmslatte.field;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kr.dcos.cmslatte.core.Susik;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.functions.FunctionArguments;
import kr.dcos.cmslatte.functions.FunctionManager;
import kr.dcos.cmslatte.scanner.FunctionScanner;
import kr.dcos.cmslatte.scanner.MunjangScanner;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this class contain all fields
 * @author Administrator
 *
 */
public class FieldChangGo {
	
	private static Logger logger = LoggerFactory.getLogger(FieldChangGo.class);

	private Map<String,Field> map;
	//
	//constructor
	//
	public FieldChangGo(){
		map = new HashMap<String, Field>();
	}
	/**
	 * return field by name
	 * @param fieldName
	 * @return
	 */
	public Field getField(FieldName fieldName){
		return this.getField(fieldName.getName());
	}
	public Field getField(String key){
		if(map.containsKey(key)){
			return map.get(key);
		}
		return null;		
	}
	/**
	 * if exist, delete it, and put
	 * @param field
	 */
	public void putField(Field field){
		String key = field.getName();
		if(map.containsKey(key)){
			map.remove(key);
		}
		map.put(key, field);
	}
	/**
	 * Class t 로 부터 각 property명을 변수명으로 그 내용을 값으로 하는 field를 만든다
	 * serialVersionUID property는 제외한다
	 * @param t
	 */
	public  <T> void putFieldsWithObject(T t){
		java.lang.reflect.Field[] arrayField = t.getClass().getDeclaredFields();
		for (java.lang.reflect.Field field : arrayField) {
			try {
				String propertyName = field.getName();
				if (propertyName.equals("serialVersionUID"))
					continue;

				java.lang.reflect.Field afield;
				afield = t.getClass().getDeclaredField(propertyName);
				afield.setAccessible(true);
				Object value = afield.get(t);
				putField(new ConstantField(propertyName, LatteUtil.ifEmpty(value, "")));
				logger.debug(propertyName + " set with value ["
						+ LatteUtil.ifEmpty(value, "") + "]");
			} catch (NoSuchFieldException e) {
				logger.error("", e);
			} catch (SecurityException e) {
				logger.error("", e);
			} catch (IllegalArgumentException e) {
				logger.error("", e);
			} catch (IllegalAccessException e) {
				logger.error("", e);
			}
		}
	}
	/**
	 * prefix를 propertyName앞에 붙여서 constant field를 만든다.
	 * Class t 로 부터 각 property명을 변수명으로 그 내용을 값으로 하는 field를 만든다
	 * serialVersionUID property는 제외한다
	 * @param t
	 */
	public  <T> void putFieldsWithObject(String prefix,T t){
		java.lang.reflect.Field[] arrayField = t.getClass().getDeclaredFields();
		for (java.lang.reflect.Field field : arrayField) {
			try {
				String propertyName = field.getName();
				if (propertyName.equals("serialVersionUID"))
					continue;

				java.lang.reflect.Field afield;
				afield = t.getClass().getDeclaredField(propertyName);
				afield.setAccessible(true);
				Object value = afield.get(t);
				String newPropertyName = propertyName;
				if(prefix.length()>0){
					newPropertyName = prefix + propertyName.toUpperCase().substring(0,1).toUpperCase()+propertyName.substring(1);
				}
				putField(new ConstantField(newPropertyName, LatteUtil.ifEmpty(value, "")));
				logger.debug(propertyName + " set with value ["
						+ LatteUtil.ifEmpty(value, "") + "]");
			} catch (NoSuchFieldException e) {
				logger.error("", e);
			} catch (SecurityException e) {
				logger.error("", e);
			} catch (IllegalArgumentException e) {
				logger.error("", e);
			} catch (IllegalAccessException e) {
				logger.error("", e);
			}
		}
	}
	public int size(){
		return map.size();
	}
	/**
	 * fieldName에 따라서 값을 찾아서 리턴한다.
	 * 모든 값들은 Object형태로 들어 있으므로 Object에 따라서 TokenType을 맞춰준다
	 * 찾지 못하면 null을 리턴한다
	 * @param fieldName
	 * @return
	 * @throws CmsLatteFunctionException 
	 * @throws CmsLatteException 
	 * @throws CloneNotSupportedException 
	 */
	public Token getValueToken(FieldName fieldName) throws  CmsLatteException, CmsLatteFunctionException {
		
		
		TokenType tokenType = TokenType.Unknown;
		Object value = null;
		
		//값을 가져와서
		Field field = getField(fieldName);
		//만약 선언되지 않고 사용되는 field가 있다면 "" 비어 있는 문자로 선언된다
		if(field == null){
			//throw new CmsLatteException(fieldName.getName() + " is not defined");
			//발견하지 못했다면
			field = newConstantField(fieldName.getName());
			field.setValue("");
			putField(field);
			logger.warn(fieldName.getName() + " is not defined, setted with empty string");
			
		}
		if( field.type == FieldType.Constant){
			//tokenType = LatteUtil.getTokenTypeWithObject(field.value);
			value = field.value;
		}else if(field.type == FieldType.Array){
			ArrayField arrayField = (ArrayField)field;
			evalRowCol(fieldName);
			if(fieldName.getRowIndex()>=0){
				value = arrayField.getValue(fieldName.getRowIndex());
				//tokenType = LatteUtil.getTokenTypeWithObject(value);
			}

		}else if(field.type == FieldType.Matrix){
			MatrixField matrixField = (MatrixField)field;
			evalRowCol(fieldName);
			if(fieldName.getRowIndex()>=0 && fieldName.getColIndex()>=0){
				value = matrixField.getValue(fieldName.getRowIndex(),fieldName.getColIndex());
				//tokenType = LatteUtil.getTokenTypeWithObject(value);
			}
		}else{
			throw new CmsLatteException(fieldName.getName() + " is unknown field type");
		}
		//function과 prefix를 적용한다.
		if(LatteUtil.isNullOrEmpty(fieldName.getSubFuncDesc()) == false){
			if(value == null){ //a.length();
				value = field;
			}
			value = applySubfunc(value,fieldName.getSubFuncDesc());
			
		}
		if(fieldName.getPrefix() != 0x00){
			value = applyPrefix(value,fieldName.getPrefix());
		}

		tokenType = LatteUtil.getTokenTypeWithObject(value);
		return new Token(value,tokenType);
	}
	
	/**
	 * u,l,s,d,c,z 
	 * value가 String형일때만 적용한다
	 * @param value
	 * @param prefix
	 * @return
	 * @throws CmsLatteFunctionException 
	 */
	private Object applyPrefix(Object value, char prefix) throws CmsLatteFunctionException {
		
		if(value instanceof String){
			FunctionArguments funcArgs = new FunctionArguments();
			switch (prefix) {
			case 'u':
				funcArgs.setFunctionName("toUpperCase");
				break;
			case 'l':
				funcArgs.setFunctionName("toLowerCase");
				break;
			case 's':
				funcArgs.setFunctionName("singleQuotation");
				break;
			case 'd':
				funcArgs.setFunctionName("doubleQuotation");
				break;
			case 'z':
				funcArgs.setFunctionName("firstCharLower");
				break;
			case 'a':
				funcArgs.setFunctionName("firstCharUpper");
				break;
			case 'c':
				funcArgs.setFunctionName("capital");
				break;
			case 'p':
				funcArgs.setFunctionName("propertyString");
				break;				
			default:
				break;
			}
			if(!LatteUtil.isNullOrEmpty(funcArgs.getFunctionName())){
				funcArgs.addArgument(value);
				return FunctionManager.getInstance().execute(funcArgs);
			}else{
				return value;
			}
		}
		return value;
	}
	private Object applySubfunc(Object value,String funcDesc) throws CmsLatteFunctionException, CmsLatteException {
		
		MunjangScanner ms = new MunjangScanner();
    	FunctionScanner fs = new FunctionScanner();
    	fs.scan(funcDesc);

    	//허용된 subfunction인지 체크한다
    	TokenType tokenType = LatteUtil.getTokenTypeWithObject(value);
    	boolean b = FunctionManager.getInstance().isAllowedSubfunction(tokenType,fs.getFunctionName());
    	if(b==false){
    		throw new CmsLatteException(fs.getFunctionName()+ " is not allowd subfunction of " + tokenType.toString());
    	}
    	//argument를 만든다
    	FunctionArguments funcArgs = new FunctionArguments(fs.getFunctionName());
    	Susik susik = new Susik(this);
    	
    	for (String s : fs.getArgStringList()) {
			TokenStack argTokenStack = ms.scan(s);
			funcArgs.addArgument(susik.eval(argTokenStack));
		}
    	//자기 자신을 argument의 어디에 위치할 것인지 , 대부분 앞에 넣고, 특별한 경우 (format) 뒤에
    	if(FunctionManager.getInstance().isBackArgumentLocation(fs.getFunctionName()) ==false ){ 
    		funcArgs.insertArgument(0,value); //현재의 값을 1번 argument로 추가한다 대개의 경우	
    	}else{
    		funcArgs.addArgument(value);
    	}
    	return FunctionManager.getInstance().execute(funcArgs);
	}
	/**
	 * fieldName의 row,col의 수식을 해석해서 integer로 만든다
	 * @param fieldName
	 * @return
	 * @throws CmsLatteException 
	 * @throws CloneNotSupportedException 
	 * @throws CmsLatteFunctionException 
	 */
	private FieldType evalRowCol(FieldName fieldName) throws  CmsLatteException, CmsLatteFunctionException {
		
		String  rowString = fieldName.getRowSusikString();
		
		if(LatteUtil.isNullOrEmpty(rowString)){
			fieldName.setRowIndex(-1);
			fieldName.setColIndex(-1);
			return FieldType.Constant;
		}
		//row
		MunjangScanner ms = new MunjangScanner();
		Susik susik = new Susik(this);
		
		TokenStack ts = ms.scan(rowString);
		Token token = susik.eval(ts);
		fieldName.setRowIndex((Integer)token.getValue());
		
		//col
		String  colString = fieldName.getColSusikString();
		if(LatteUtil.isNullOrEmpty(colString)){
			fieldName.setColIndex(-1);
			return FieldType.Array;
		}
		ts = ms.scan(colString);
		token = susik.eval(ts);
		fieldName.setColIndex((Integer)token.getValue());
		return FieldType.Matrix;
	}


	/**
	 * fieldName으로 새로운 필드를 만든다
	 * constant,array,matrix 구분을 fieldName의 인자를 보고 한다
	 * @param fieldName
	 * @return
	 * @throws CmsLatteException 
	 * @throws CmsLatteFunctionException 
	 */
//	private Field newField(FieldName fieldName) throws CmsLatteFunctionException, CmsLatteException {
//
//		FieldType fieldType = fieldName.getFieldType();
//		if(fieldType==FieldType.Unknown){
//			fieldType = evalRowCol(fieldName);
//		}		
//		if(fieldType==FieldType.Constant){
//			return newConstantField(fieldName.getName());
//		}else if(fieldType==FieldType.Array){
//			return newArrayField(fieldName.getName());
//		}else if(fieldType==FieldType.Matrix){
//			return newMatrixField(fieldName.getName());
//		}
//		return null;
//	}
	/**
	 * tokenType은 오른쪽을 의미한다. 
	 * tokenType로 판단하여 constant,array,matrix를 결정하고 이름을 name으로 붙여서 Field를 만들어 리턴한다
	 * @param name
	 * @param tokenType
	 * @return
	 * @throws CmsLatteFunctionException
	 * @throws CmsLatteException
	 */
	private Field newField(String name,TokenType tokenType) throws CmsLatteFunctionException, CmsLatteException{
		if(tokenType == TokenType.ArrayField){
			return newArrayField(name);
		}else if(tokenType==TokenType.MatrixField){
			return newMatrixField(name);
		}else{
			return newConstantField(name);
		}
	}

	private ConstantField newConstantField(String name) throws CmsLatteFunctionException, CmsLatteException{
		ConstantField field = new ConstantField(name, null);
		field.setName(name);
		field.setType(FieldType.Constant);
		return field;
	}
	private ArrayField newArrayField(String name) throws CmsLatteFunctionException, CmsLatteException{
		ArrayField field = new ArrayField(name);
		field.setName(name);
		field.setType(FieldType.Array);
		return field;
	}
	private MatrixField newMatrixField(String name) throws CmsLatteFunctionException, CmsLatteException{
		MatrixField field = new MatrixField(name);
		field.setName(name);
		field.setType(FieldType.Matrix);
		return field;

	}

	/**
	 * field가 이미 다른 type으로 존재하면 지운다.
	 * field가 없으면 resultToken의 타입으로  따라서 생성한다.
	 * fieldName은 왼쪽, resultToken은 오른쪽을 의미한다
	 *  
	 * @return
	 * @throws CmsLatteException 
	 * @throws CmsLatteFunctionException 
	 */
	private Field prepareField(FieldName fieldName,Token resultToken) throws CmsLatteFunctionException, CmsLatteException {
		Field field = getField(fieldName);
		if(field != null){ //이미 존재한다
//			if(field.getType() != fieldName.getFieldType()){
//				map.remove(field);
//				field = newField(fieldName);
//			}
			TokenType rightTokenType =resultToken.getTokenType();
			if(field.getType()==FieldType.Constant){ //왼쪽이 constant,constant가 아니면지운다
				if( rightTokenType== TokenType.ArrayField ||
					rightTokenType== TokenType.MatrixField ||
					rightTokenType== TokenType.ArrayInit ||
					rightTokenType== TokenType.TableInit 
					){
					map.remove(field);
					field = newField(fieldName.getName(),rightTokenType); 
				}
			}else if(field.getType()==FieldType.Array){ //왼쪽dl array
				//a={1,2,3} ; a="abc" 인경우 지운다, a={1,2,3}; a[1]="abc" 인경우 지울 수 없다
				//양쪽 이름이 같으면 지울 필요가 없다
				if(fieldName.getRowIndex()<0 && !fieldName.getName().equals(field.getName())){ 
					map.remove(field);
					field = newField(fieldName.getName(),rightTokenType);
				}
			}else if(field.getType()==FieldType.Matrix){
				if(fieldName.getRowIndex()<0 && fieldName.getColIndex()<0 && !fieldName.getName().equals(field.getName())){
					map.remove(field);
					field = newField(fieldName.getName(),rightTokenType);					
				}
			}
		}else{ //존재하지 않는다면  resultToken에 따라서 새로 만든다
			//새로 만든다 만들어지는 것은 항상 constant 
			//a=1, a={}, t={{}}; 만 지원 a[1]= 3; 과 같이 먼저 인자를 쓴
			return newField(fieldName.getName(),resultToken.getTokenType());
		}
		return field;
	}

	/**
	 * leftFieldName에 해당하는 부분에 resultToken의 value를 넣는다.
	 * resultToken의 타입이 무엇인지 판별해야한다.
	 * 1. constant token인데, array나, table에 있다면 그것을 지운다.
	 * 2. 즉 다은 타입의 것을 지운다.
	 * 3. a[3]=1 이 있고 a[3]=2를 넣으려고 한다.
	 * 4. a = s.split(",");
	 * @param leftFieldName
	 * @param resultToken
	 * @throws CmsLatteException 
	 */
	public void putValue(FieldName fieldName, Token resultToken) throws CmsLatteException {
		
		Field field = prepareField(fieldName,resultToken);
				
		if(field.getType() == FieldType.Constant){
			field.setValue(resultToken.getValue());
		}else if(field.getType() == FieldType.Array){
			evalRowCol(fieldName);
			if(fieldName.getRowIndex()>=0){
				((ArrayField)field).appendOrReplace(fieldName.getRowIndex(), resultToken.getValue());
			}else if(resultToken.getTokenType() == TokenType.ArrayField){
				field = null;
				field = (ArrayField)resultToken.getValue();
				field.setName(fieldName.getName());
			}
		}else if(field.getType() == FieldType.Matrix){
			evalRowCol(fieldName);
			if(fieldName.getRowIndex()>=0 && fieldName.getColIndex()>=0){
				((MatrixField)field).appendOrReplace(fieldName.getRowIndex(), fieldName.getColIndex(), resultToken.getValue());
			}else if(resultToken.getTokenType()==TokenType.MatrixField){
				field = null;
				field = (MatrixField)resultToken.getValue();
				field.setName(fieldName.getName());
			}
		}
		putField(field);
		
	}	
	/**
	 * ArrayInit를 ArrayParser가 parsing한 후에 그 값들을 배열에 추가한다.
	 * @param mp
	 * @throws CmsLatteException 
	 * @throws CmsLatteFunctionException 
	 */
	public void insertOrReplaceMatrix(FieldName fieldName,MatrixField matrixField) throws CmsLatteFunctionException, CmsLatteException {
		//완전히 지운다.
		Field field = getField(fieldName);
		if(field!=null){
			map.remove(field.getName());
		}
		putField(matrixField);
		
	}
	/**
	 * 배열의 초기화 임으로 완전히 지워야한다. 타입이 틀리다고 지우지 않는 것은 안된다
	 * a = {1,2,3};
	 * a = {1,2};
	 * @param fieldName
	 * @param arrayField
	 * @throws CmsLatteFunctionException
	 * @throws CmsLatteException
	 */
	public void insertOrReplaceArrayInit(FieldName fieldName, ArrayField arrayField) 
			throws CmsLatteFunctionException, CmsLatteException {
		
		//완전히 지운다.
		Field field = getField(fieldName);
		if(field!=null){
			map.remove(field.getName());
		}
		
		putField(arrayField);
	}

	public void loadVariableFromXml(File file) throws CmsLatteException {
		FieldLoader fieldLoader = new FieldLoader();
		fieldLoader.loadFromXml(file);
		Iterator<Field> it = fieldLoader.getIterator();
		while(it.hasNext()){
			Field f = it.next();
			putField(f);
		}
	}
	/**
	 * xml파일로부터 변수들을 읽어서 창고에 추가한다
	 * @param path
	 * @throws CmsLatteException 
	 */
	public void loadVariableFromXml(String path) throws CmsLatteException{
		FieldLoader fieldLoader = new FieldLoader();
		fieldLoader.loadFromXml(path);
		logger.debug("load "+fieldLoader.size()+" datas from " +path);
		Iterator<Field> it = fieldLoader.getIterator();
		while(it.hasNext()){
			Field f = it.next();
			putField(f);
		}
	}
	/**
	 * 창고에 저장되어 있는 필드를 모두 xml에 저장한다.
	 * @param path
	 * @throws CmsLatteException 
	 */
	public void saveVariableToXml(String path,String encoding,boolean override) throws CmsLatteException {
		File file = new File(path);
		saveVariableToXml(file, encoding, override);
	}
	public void saveVariableToXml(File file,String encoding,boolean override) throws CmsLatteException {
		if(file.exists() && !override){
			return;
		}
		FieldSaver fieldSaver = new FieldSaver(this);
		fieldSaver.saveToXml(file.getAbsolutePath(),encoding);
	}

	/**
	 * 창고에 저장된 모든 variable을 나타내는 xml 문자열을 리턴한다
	 * @return
	 * @throws CmsLatteException 
	 */
	public String getXmlString() throws CmsLatteException {
		FieldSaver fieldSaver = new FieldSaver(this);
		return fieldSaver.getXmlString("utf-8");
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Field>entry : map.entrySet()) {
			sb.append(entry.getKey());
			sb.append("\n");
			Object o = entry.getValue();
			if(o !=null){
				sb.append(entry.getValue().toString());
			}else{
				sb.append("error");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	/**
	 * 모든 Field를 삭제한다
	 */
	public void clear() {
		map.clear();
	}
	/**
	 * fieldList를 리턴한다.
	 * fieldType이 null이면 모든 필드를 리턴한다
	 * @return
	 */
	public List<Field> getFieldList(FieldType fieldType){
		List<Field> list =new ArrayList<Field>();
		for (Entry<String, Field> entry : map.entrySet()) {
			Field field = entry.getValue();
			if(fieldType==null){
				list.add(field);
			}else{
				if(field.getType() == fieldType){
					list.add(field);
				}
			}
		}
		return list;
	}


}
