package kr.dcos.cmslatte.scanner;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.functions.FunctionManager;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenTable;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

/**
 * 한문장을 스캔한다. 
 * 문장이란 ...; ';'로 끊어지는 cmsLatte program code의 1개의 문장을 의미함
 * ex) a = b+3; if a==3 ; begin; end; a[i,4] = b[4].substring(3,5);
 * [ ] 안에는 integer or integer constant field만 들어갈 수 있다.
 * 이때 수식에서 해석할 수 있는 단위로 끊어내야한다. 
 * a[1] = b[2] + c[3].substring(1,2) 일경우 토큰은 5개가 되어야한다.
 * a[1] , =, b[2] , + , c[3].substring(1,2) 와 같이 끊어져야한다.
 * @author Administrator
 *
 */
public class MunjangScanner extends ScannerBase {
	private String code;
	/**
	 * scan하면서 Integer,Function,Double,Boolean,Field를 판별한다.
	 * Token을 생성할때 tokenType을 결정한다.
	 */
	private TokenType tmpTokenType;  
	private TokenStack tokenStack;   
	
	public MunjangScanner(){
		super();
	}
	public TokenStack scan(String s) throws  CmsLatteException {
		code = s;
		if(code == null || code.length()< 1){
			return null;
		}
		tokenStack = new TokenStack();
		index = 0;
		srcArray = code.toCharArray();
		char nowChar=0,nextChar=0;
		String twoChar=null;
		tmpTokenType = TokenType.Unknown;
		
		while(index < srcArray.length){
			nowChar = srcArray[index];
			if(Character.isWhitespace(nowChar)){
				ifBufferNotEmptyCreateTokenAndPushToStack();
				index = skipWhitespace();
				continue;
			}
			twoChar = null;
			if (notScanLength()>=1) {  //남은 갯수가 2보다 크거나 같다면
				nextChar = srcArray[index + 1]; 
				twoChar = Character.toString(nowChar) + Character.toString(nextChar);
			}
			
			Token token = null ; 
			if(Character.isLetter(nowChar) == false){ //'in' 'to' 와 같은 글짜는 제외
				token = TokenTable.getInstance().findToken(twoChar); //2개로 된 예약키 
			}
			if( token != null){ //2글짜 예약어이다
				ifBufferNotEmptyCreateTokenAndPushToStack();
				tokenStack.push(getClone(token));
				index+=2;
				continue;
			}
			
			token = TokenTable.getInstance().findToken(nowChar);
			if(token != null){//1개짜리 예약어일경우
				if(charInString(nowChar,"+-*/%")){
					if(nextChar=='='){ //+=, *= 사칙연산 + = 일경우 풀어서넣는다.
						ifBufferNotEmptyCreateTokenAndPushToStack();
						Token lastToken = getLastToken();
						tokenStack.push(getClone(TokenTable.getInstance().findToken("=")));
						tokenStack.push(getClone(lastToken));
						tokenStack.push(getClone(TokenTable.getInstance().findToken(nowChar)));
						index+=2;
					}else{
						ifBufferNotEmptyCreateTokenAndPushToStack();
						tokenStack.push(getClone(token));
						index++;
					}
				}else if(charInString(nowChar,"=<>,?:!")){					
					ifBufferNotEmptyCreateTokenAndPushToStack();
					tokenStack.push(getClone(token));
					index++;					
				}else if(nowChar == '['){ //a[1] , a [1]->error
					tmpTokenType = tokenTypeOfBuffer();
					if(tmpTokenType==TokenType.Field){
						fillBufferUntilPair('[',']');
					}else{
						throw new CmsLatteException("syntax error");
					}
					tmpTokenType = TokenType.Field;
					
				}else if(nowChar == '('){//a.trim(), a[1].trim(), substring("12345",1,3);
					if(isBufferNotEmpty()){
						String name = buffer.toString(); //현재들어 있는 것이 functionName or field.funtionName
						fillBufferUntilPair('(',')');
						tmpTokenType = TokenType.Field;
						if(FunctionManager.getInstance().isCmsFunction(name)){
							tmpTokenType = TokenType.Function;
						}
					}else{
						token = TokenTable.getInstance().findToken(nowChar);
						//tokenStack.push((Token)token.clone());
						tokenStack.push(getClone(token));
						index++;
					}
				}else if(nowChar == ')'){
					ifBufferNotEmptyCreateTokenAndPushToStack();
					token = TokenTable.getInstance().findToken(nowChar);
					tokenStack.push(getClone(token));
					index++;					
				}else if(nowChar == '.'){ //a.trim(), a[1].trim(), 1.5 , .5->error
					buffer.append(nowChar);
					index++;
					//현재 버퍼가 숫자이고 뒤쪽이 숫자라면 double로
					if(tokenTypeOfBuffer()==TokenType.Integer && Character.isDigit(nextChar)){
						tmpTokenType = TokenType.Double;
					}
				}else if(nowChar == '{'){ //arrayInit or tableInit
					ifBufferNotEmptyCreateTokenAndPushToStack();
					fillBufferUntilPair('{','}',true);
					tmpTokenType = TokenType.ArrayInit;
					if(countOf('{')>1){
						tmpTokenType = TokenType.TableInit;
					}
					ifBufferNotEmptyCreateTokenAndPushToStack();
				}else{
					throw new CmsLatteException(nowChar + " near by syntax error");
				}
			}else{
				if(nowChar=='"'){
					ifBufferNotEmptyCreateTokenAndPushToStack();
					fillBufferWithStringPart(false);
					
					tokenStack.push(new Token(buffer.toString(),TokenType.String));
					bufferClear();
					tmpTokenType = TokenType.Unknown;
					//ifBufferNotEmptyCreateTokenAndPushToStack();
					continue;
				}
				buffer.append(nowChar);
				index++;
			}
		}
		ifBufferNotEmptyCreateTokenAndPushToStack();
		return tokenStack;
	}
	
	/**
	 * tokenStack의 마지막 토큰을 리턴 토큰이 없으면 null리턴
	 * @return
	 * @throws CmsLatteException 
	 */
	private Token getLastToken() throws CmsLatteException {
		if(tokenStack.isEmpty()){
			return null;
		}
		return getClone(tokenStack.get(tokenStack.size()-1));
	}
	private Token getClone(Token token) throws CmsLatteException {
		try {
			return (Token)token.clone();
		} catch (CloneNotSupportedException e) {
			throw new CmsLatteException("create clone of token failed");
		}
	}
	/**
	 * 바로전 token이 무슨 타입인지 체크한다
	 * buffer가 비어 있지 않다면 버퍼로 type을 체크한다.
	 * 이때는 cmsutil에서 tokentype이 무엇인지 판정하는 함수(getTokenTypeWithString)를 통해서 한다
	 * 비어 있다면 stack에 쌓여있는 것으로 한다
	 * @return
	 */
	private TokenType tokenTypeOfBuffer(){
		if(isBufferNotEmpty()){
			return LatteUtil.getTokenTypeWithString(buffer.toString());
		}
		return TokenType.Unknown;
	}

	/**
	 * buffer의 내용을 token으로 만든다
	 * 이때 token의 타입은 tokentable에 없으면  tmpTokenType에 따라 넣는다. 
	 * buffer를 지운다
	 * @throws CmsLatteException 
	 * 
	 * @throws CloneNotSupportedException
	 */
	private void ifBufferNotEmptyCreateTokenAndPushToStack() throws CmsLatteException  {
		if(isBufferNotEmpty()){
			String data = buffer.toString();
			Token token = TokenTable.getInstance().findToken(data);
			if(token ==null){
				if(tmpTokenType == TokenType.Unknown){
					if(LatteUtil.isValidLabelName(data)){
						tmpTokenType = TokenType.Label;
					}else{
						tmpTokenType = LatteUtil.getTokenTypeWithString(data);
					}
				}
				Object object = LatteUtil.getObjectWithTokenType(buffer.toString(), tmpTokenType);
				token = new Token(object,tmpTokenType);
				tokenStack.push(token); //타입은 알아서 정한다
			}else{ //토큰테이블에 있다면 
				try{
					tokenStack.push((Token)token.clone());
				}catch(CloneNotSupportedException e){
					throw new CmsLatteException();
				}
			}
			bufferClear();
			tmpTokenType = TokenType.Unknown;
		}
	}
	
}
