package kr.dcos.cmslatte.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.dcos.cmslatte.controls.ForController;
import kr.dcos.cmslatte.controls.ForeachController;
import kr.dcos.cmslatte.controls.SwitchController;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.Field;
import kr.dcos.cmslatte.field.FieldChangGo;
import kr.dcos.cmslatte.field.FieldName;
import kr.dcos.cmslatte.field.FieldType;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.functions.LatteCommonFunctions;
import kr.dcos.cmslatte.functions.LatteFileFunctions;
import kr.dcos.cmslatte.scanner.ArrayParser;
import kr.dcos.cmslatte.scanner.FieldParser;
import kr.dcos.cmslatte.scanner.MatrixParser;
import kr.dcos.cmslatte.scanner.MunjangPattern;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

/**
 * CmsLatte에서 사용하는 클래스
 * 각 Command에 따라서 세부적인 동작을 한다.
 * 
 * @author Administrator
 *
 */
public class CommandSuHangGi {
	
//	private static Logger logger = LoggerFactory
//			.getLogger(CommandSuHangGi.class);
	

	private FieldChangGo fieldChangGo=null;
	private FieldParser fieldParser;
	private Susik susik;
	
	public CommandSuHangGi(FieldChangGo fieldChangGo){
		this.fieldChangGo = fieldChangGo;
		if(this.fieldChangGo == null){
			this.fieldChangGo = new FieldChangGo();
		}
		fieldParser = new FieldParser();
		susik = new Susik(fieldChangGo);
	}
	/**
	 * assign 대입 수행
	 * @param tokenStack
	 * @return
	 * @throws CmsLatteException
	 */
	public FieldName doAssign(TokenStack tokenStack) throws CmsLatteException{
		TokenStack rightStack = tokenStack.subStack(2);
		FieldName leftFieldName = null;
		FieldName rightFieldName = null;
		if(LatteUtil.getMunjangPattern(rightStack) == MunjangPattern.Assign){ //a=b=c= 1+3;
			rightFieldName = doAssign(rightStack);
			leftFieldName = fieldParser.scan(tokenStack.get(0).getValue().toString());
			Token resultToken = fieldChangGo.getValueToken(rightFieldName);// susik.eval(rightStack);
			fieldChangGo.putValue(leftFieldName,resultToken);
			return leftFieldName;
		}

		leftFieldName =  fieldParser.scan(tokenStack.get(0).getValue().toString());
		if(tokenStack.get(2).getTokenType()==TokenType.ArrayInit){
			ArrayParser ap = new ArrayParser();
			ap.scan(tokenStack.get(2).getString());
			fieldChangGo.insertOrReplaceArrayInit(leftFieldName,ap.getArrayField(leftFieldName.getName()));
		}else if(tokenStack.get(2).getTokenType()==TokenType.TableInit){
			MatrixParser mp = new MatrixParser();
			mp.scan(tokenStack.get(2).getString());
			fieldChangGo.insertOrReplaceMatrix(leftFieldName,mp.getMatrixField(leftFieldName.getName()));
		}else{
			//수식으로 계산, 결과값을 창고에 leftFieldName변수에 해당하는 부분에 넣는다.
			Token resultToken = susik.eval(rightStack);
			//left필드의 타입은 resultToken에 의해서 결정된다.
			if(resultToken.getTokenType()==TokenType.ArrayField){
				leftFieldName.setFieldType(FieldType.Array);
			}else if(resultToken.getTokenType()==TokenType.MatrixField){
				leftFieldName.setFieldType(FieldType.Matrix);
			}else{
				leftFieldName.setFieldType(FieldType.Constant);
			}
			fieldChangGo.putValue(leftFieldName,resultToken);
		}
		return leftFieldName;
	}
	//echo
	public String echo(TokenStack stack) throws CmsLatteFunctionException, CmsLatteException {
		//stack.remove(0);
		Token resultToken = susik.eval(stack.subStack(1));
		if(resultToken.getTokenType()==TokenType.ArrayField ||
		   resultToken.getTokenType()==TokenType.MatrixField){
			return LatteCommonFunctions.toString(resultToken.getValue());
		}else if(resultToken.getTokenType()==TokenType.Date){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sd.format((Date)resultToken.getValue());
		}
		if(resultToken.getValue() == null) return "";
		if(resultToken.getTokenType()==TokenType.String){
			return removeSlash(resultToken.getValue().toString());
		}else{
			return resultToken.getValue().toString();
		}
		//return resultToken.getValue()==null ? "" : removeSlash(resultToken.getValue().toString());
	}
	// abc\"def 문제를 해결하기 위해서...흠.. 별로다
	private String removeSlash(String s) {
		if(s.indexOf('"')<0){
			return s;
		}else{
			StringBuilder sb = new StringBuilder();
			char[] arr = s.toCharArray();
			for(int i=0;i<arr.length;i++){
				if(arr[i] == '\\' && arr[i+1] =='"'){
					continue;
				}
				sb.append(arr[i]);
			}
			return sb.toString();
		}
	}
	/**
	 * conditionStack을 판별하여 true , false를 리턴해준다.
	 * 흐름제어 if, while,for등에서 사용
	 * @param conditionStack
	 * @return
	 * @throws CmsLatteException 
	 * @throws CmsLatteFunctionException 
	 */
	public boolean isLogicalTrue(TokenStack conditionStack) throws CmsLatteFunctionException, CmsLatteException {
		Token token = susik.eval(conditionStack);
		return token.getBoolean();
		
	}
	/**
	 * for 문장에서 field가 to 조건에 합당한지 체크한다.
	 * for문장을 나가야한다면 true를 아직 나갈 수 없다면 false를 리턴한다
	 * @param forController
	 * @return
	 * @throws CmsLatteException 
	 */
	public boolean forConditionFinished(ForController forController) throws CmsLatteException {
		FieldName indexField = fieldParser.scan(forController.getIndexField());
		Token t = fieldChangGo.getValueToken(indexField);
		if(t == null){
			throw new CmsLatteException(forController.getIndexField()+" is not defined");
		}
		int nowValue = t.getInteger();
		if(forController.getToOrDownToken() == TokenType.To){
			Token toToken = susik.eval(forController.getToOrDownStack());
			//if(nowValue > toToken.getInteger()){
			if(nowValue>=toToken.getInteger()){
				return true;
			}
			return false;
		}else { //down
			Token downToken = susik.eval(forController.getToOrDownStack());
			if(nowValue <= downToken.getInteger()){
				return true;
			}
			return false;
		}
	}
	//처음에 검사 for i=0 to 0 에러 때문에
	public boolean forConditionFinishedPre(ForController forController) throws CmsLatteException {
		FieldName indexField = fieldParser.scan(forController.getIndexField());
		Token t = fieldChangGo.getValueToken(indexField);
		if(t == null){
			throw new CmsLatteException(forController.getIndexField()+" is not defined");
		}
		int nowValue = t.getInteger();
		if(forController.getToOrDownToken() == TokenType.To){
			Token toToken = susik.eval(forController.getToOrDownStack());
			if(nowValue > toToken.getInteger()){
			//if(nowValue>=toToken.getInteger()){
				return true;
			}
			return false;
		}else { //down
			Token downToken = susik.eval(forController.getToOrDownStack());
			if(nowValue < downToken.getInteger()){
				return true;
			}
			return false;
		}
	}	
	/**
	 * for 문장의 값을 증가 또는 감소 시킨다.
	 * @param forController
	 * @throws CmsLatteException 
	 */
	public void forSetNewIndexValue(ForController forController) throws CmsLatteException {
		
		FieldName indexFieldName = fieldParser.scan(forController.getIndexField());
		Token token = fieldChangGo.getValueToken(indexFieldName);
		int stepValue = 1;
		if(forController.getStepStack()!=null){
			Token stepToken = susik.eval(forController.getStepStack());
			stepValue = stepToken.getInteger();
		}
		int newValue = 0;
		if(forController.getToOrDownToken() == TokenType.To){
			newValue = token.getInteger() + stepValue;
		}else{
			newValue = token.getInteger() - stepValue;
		}
		fieldChangGo.putValue(indexFieldName, new Token(TokenType.Integer,newValue));
	}
	/**
	 * variableName이 String, Array, Table인 경우
	 * @param tokenStack
	 * @return
	 * @throws CmsLatteException 
	 * @throws CmsLatteFunctionException 
	 */
	public int getSize(TokenStack tokenStack) throws CmsLatteFunctionException, CmsLatteException {
		
		Token token = susik.eval(tokenStack);
		if(token.getTokenType() == TokenType.String){
			return token.getString().length();
		}else{
			Field field =  fieldChangGo.getField(new FieldName(token.getName()));
			if(field==null){
				throw new CmsLatteException("getSize failed");
			}
			if(field.getType() == FieldType.Array){
				return ((ArrayField)field).size();
			}else if(field.getType() == FieldType.Matrix){
				return ((MatrixField)field).size();
			}else{
				throw new CmsLatteException("foreach statement failed");
			}
		}
	}
	/**
	 * foreach 동작을 한다.
	 * 1. maxSize 가 결정되지 않았다면 maxSize를 구한다.
	 * @param foreachController
	 * @throws CmsLatteException 
	 */
	public void foreachAction(ForeachController foreachController) throws CmsLatteException {
	  //maxSize를 구한다
//	  if(foreachController.getMaxIndex()<0){
//		  int maxSize = getSize(foreachController.getRightStack());
//		  foreachController.setMaxIndex(maxSize-1);
//	  }
	  //left쪽은 field이다. parsing
	  FieldName left = fieldParser.scan(foreachController.getLeftField());
	  //창고에서 찾는다. 있으면 허용하지 않는다.
	  Field leftField = fieldChangGo.getField(left);
	  if(foreachController.getNowIndex()<0 && leftField != null){ //처음한번만 체크
		  throw new CmsLatteException(left.getName()+" is already defined, cannot use in foreach");
	  }
	  
	  int nowIndex = foreachController.getNowIndex()+1;

	  foreachController.setNowIndex(nowIndex);
	  Token resultToken = susik.eval(foreachController.getRightStack());
	  
	  if(resultToken.getTokenType()==TokenType.String){
		  String target = resultToken.getString();
		  if(nowIndex<target.length()){ //범위 안에 있다면 
			  String v = Character.toString(target.charAt(nowIndex));
			  //left에 assign
			  fieldChangGo.putValue(left, new Token(v));
		  }else{
			  foreachController.setDone(true);
		  }
	  }else if(resultToken.getTokenType()==TokenType.ArrayField){
		  ArrayField arrayField = (ArrayField)resultToken.getValue(); 
		  if(nowIndex<arrayField.size()){
			  Object o = arrayField.getValue(nowIndex);
			  fieldChangGo.putValue(left, new Token(o));
		  }else{
			  foreachController.setDone(true);
		  }
	  }else if(resultToken.getTokenType()==TokenType.MatrixField){
		  MatrixField matrixField = (MatrixField)resultToken.getValue();
		  if(nowIndex<matrixField.size()){
			  Object o = matrixField.getValue(nowIndex);
			  fieldChangGo.putValue(left, new Token(o));
		  }else{
			  foreachController.setDone(true);
		  }
	  }else{
		  throw new CmsLatteException("foreach statement fail, right statement is not valid");
	  }
	 
	}
	/**
	 * switch의 case문을 만났을 때의 동작
	 * switchController에 포함되어 있는 switchSusikStack을 해석한 결과가
	 * 현재 case의 스택 과 값이 일치하는지 일치한다면 setDone한다
	 * @param switchController
	 * @param stack
	 * @throws CmsLatteException 
	 * @throws CmsLatteFunctionException 
	 */
	public void switchCaseAction(SwitchController switchController, TokenStack caseStack) throws CmsLatteFunctionException, CmsLatteException {
		TokenStack switchStack = switchController.getSwitchSusikStack();
		Token switchValue = susik.eval(switchStack);
		Token caseValue = susik.eval(caseStack.subStack(1));
		if(switchValue.getValue().equals(caseValue.getValue())){
			switchController.setDone(true);
		}
	}
	/**
	 * save variable to file c:/1.txt with utf-8 override false
	 * save문장을 수행한다
	 * 
	 * @param stack
	 * @param output
	 * @throws CmsLatteException
	 */
	private void saveToFile(TokenStack stack, String output) throws CmsLatteException {
		// save to c:/1.txt with utf-8 override false
		int posWith = stack.indexOf(TokenType.With);
		int posOverride = stack.indexOf(TokenType.Override);
		int posFile  = stack.indexOf(TokenType.File);
		TokenStack fileNameStack = null;
		if(posOverride > 0 && posWith > 0 && posOverride < posWith){
			throw new CmsLatteException("save statement fail");
		}

		//fileName Stack을 구한다
		if(posWith>=0){
			fileNameStack = stack.subStack(posFile+1,posWith-1); //with
		}else if(posOverride>=0){
			fileNameStack = stack.subStack(posFile+1,posOverride-1); //over
		}else{
			fileNameStack = stack.subStack(posFile+1); //with , over 없다
		}
		TokenStack withStack = null;
		//with를 구한다 field임
		if(posWith >= 0){
			if(posOverride>0){
				withStack = stack.subStack(posWith+1,posOverride-1);
			}else{
				withStack = stack.subStack(posWith+1);
			}
		}
		TokenStack overrideStack = null;
		if(posOverride>0){
			overrideStack = stack.subStack(posOverride+1);
		}
		try {
			String fileName = susik.eval(fileNameStack).getString();
			String encoding = "utf-8";
			boolean override = false;
			if (withStack != null) {
				encoding = susik.eval(withStack).getString();
			}
			if (posOverride > 0) {
				Token t  = susik.eval(overrideStack);
				override = t.getBoolean().booleanValue();
			}
			LatteFileFunctions.writeToFile(output, fileName,
					encoding, override);
		} catch (Exception e) {
			throw new CmsLatteException(e.getMessage());
		}
	}
	/**
	 * 현재의 output을 변수에 저장한다
	 * @param stack
	 * @param output
	 * @throws CmsLatteException 
	 */
	private void saveToVariable(TokenStack stack, String output) throws CmsLatteException {
		String name = stack.get(3).getString();
		FieldName fieldName = fieldParser.scan(name);
		fieldChangGo.putValue(fieldName,new Token(output,TokenType.String));
	}
	//TODO need to testing and debugging
	/**
	 * latte에서호출된다.
	 * 파일에 write하는 것인지 변수에 저장하는 것인지 판단 분기한다
	 * save output|variable to file "c:/1.txt" with "utf-8" override true
	 * save output|variable to aVar
	 * @param stack
	 * @param output
	 * @throws CmsLatteException
	 */
	public void saveToAction(TokenStack stack, StringBuilder output) throws CmsLatteException{
		if(stack.get(1).getTokenType() == TokenType.Output){
			if(stack.get(3).getTokenType() == TokenType.File && stack.size()>4){
				saveToFile(stack, output.toString());
			}else if (stack.size() == 4  && stack.get(3).getTokenType()==TokenType.Field){
				saveToVariable(stack,output.toString());
			}else{
				throw new CmsLatteException("save to statement fail"); 
			}
		}else if(stack.get(1).getTokenType() == TokenType.Variables){
			if(stack.get(3).getTokenType() == TokenType.File && stack.size()>4){
				String xml = fieldChangGo.getXmlString();
				saveToFile(stack,xml); // save variables to file "c:/1.xml" with "utf-8" override true
			}else {
				String xml = fieldChangGo.getXmlString();
				saveToVariable(stack,xml);
			}
		}else{
			throw new CmsLatteException("invalid save command");
		}
	}
	/**
	 * xml파일로부터 variable을 읽어서 창고에 넣는다
	 * command: load variables from "1.txt"
	 * @param stack
	 * @throws CmsLatteException 
	 */
	public void loadVariables(TokenStack stack) throws CmsLatteException {
		if(stack.get(1).getTokenType() == TokenType.Variables){
			TokenStack fnStack = stack.subStack(3);
			Token fnToken = susik.eval(fnStack);
			if(fnToken.getTokenType() == TokenType.String){
				fieldChangGo.loadVariableFromXml(fnToken.getString());
			}
		}else{
			throw new CmsLatteException("invalid load command");
		}
	}
	/**
	 * 
	 * clear;
	 * clear variables;
	 * clear output;
	 * @param stack
	 * @param output 
	 */
	public void clearAction(TokenStack stack, StringBuilder output) {
		if(stack.size()==1){
			output.setLength(0);
		}else if(stack.size()==2 && stack.get(1).getTokenType()==TokenType.Output){
			output.setLength(0);
		}else if(stack.size()==2 && stack.get(1).getTokenType()==TokenType.Variables){
			fieldChangGo.clear();
		}
		
	}

}
