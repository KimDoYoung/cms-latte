package kr.dcos.cmslatte.core;

import java.util.Date;
import java.util.Stack;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.Field;
import kr.dcos.cmslatte.field.FieldChangGo;
import kr.dcos.cmslatte.field.FieldName;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.functions.FunctionArguments;
import kr.dcos.cmslatte.functions.FunctionManager;
import kr.dcos.cmslatte.functions.LatteArrayFunctions;
import kr.dcos.cmslatte.functions.LatteDateFunctions;
import kr.dcos.cmslatte.functions.LatteMatrixFunctions;
import kr.dcos.cmslatte.scanner.ArrayParser;
import kr.dcos.cmslatte.scanner.FieldParser;
import kr.dcos.cmslatte.scanner.FunctionScanner;
import kr.dcos.cmslatte.scanner.MatrixParser;
import kr.dcos.cmslatte.scanner.MunjangScanner;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

public class Susik {
	private FieldChangGo fieldChangGo;
	/**
	 * constructor of Susik
	 */
	public Susik(FieldChangGo fieldChangGo){
		this.fieldChangGo = fieldChangGo;
	}
	private boolean isNumberType(Token t){
		if(t.getTokenType() == TokenType.Integer || t.getTokenType() == TokenType.Double){
			return true;
		}
		return false;
	}
	private Token greatThanOrEqual(Token t1, Token t2) throws CmsLatteException {
		Token r1 = logicalGreatThan(t1, t2);
		Token r2 = logicalEqual(t1, t2);
        if (r1.getBoolean() || r2.getBoolean())
        {
            return new Token(TokenType.Boolean, true);
        }
        return new Token(TokenType.Boolean, false);
	}

	private Token lessThanOrEqual(Token t1, Token t2) throws CmsLatteException {
		  Token r1 = logicalLessThan(t1, t2);
          Token r2 = logicalEqual(t1, t2);
          if (r1.getBoolean() || r2.getBoolean())
          {
              return new Token(TokenType.Boolean, true);
          }
          return new Token(TokenType.Boolean, false);
	}

	private Token logicalGreatThan(Token t1, Token t2) throws CmsLatteException {  // t1 > t2
		if(isNumberType(t1) && isNumberType(t2)){
			if(t1.getDouble()>t2.getDouble()){
				return new Token(TokenType.Boolean,true);
			}
			return new Token(TokenType.Boolean,false);
		}
		else if(t1.getTokenType() == TokenType.String && t2.getTokenType() == TokenType.String){
			if(t1.getString().compareTo(t2.getString())>0) {
				return new Token(TokenType.Boolean,true);
			}
			return new Token(TokenType.Boolean,false);
		}else if(t1.getTokenType() == TokenType.Date && t2.getTokenType() == TokenType.Date){
			if(t1.getDate().after(t2.getDate())){
				return new Token(TokenType.Boolean,true);
			}
			return new Token(TokenType.Boolean,false);
		}else{
			throw new CmsLatteException(t1.getTokenType() +" , " + t2.getTokenType() + " logical operation is not allowed");
		}
	}

	private Token logicalLessThan(Token t1, Token t2) throws CmsLatteException {
		return logicalGreatThan(t2,t1); 
	}

	private Token logicalNotEqual(Token t1, Token t2) {
		  Token t = logicalEqual(t1,t2);
		  if(t.getBoolean() == true){
			  return new Token(TokenType.Boolean,false);
		  }else{
			  return new Token(TokenType.Boolean,true);
		  }
	}
	private  Token logicalEqual(Token t1, Token t2) //==
    {
        if (t1.getTokenType().equals(t2.getTokenType()) && 
        		t1.getValue().equals(t2.getValue())){
        		return new Token(TokenType.Boolean,true);
        }
       	return new Token(TokenType.Boolean,false);
    }
	private Token logicalAnd(Token t1, Token t2) { //&&
        if (t1.getBoolean() && t2.getBoolean()){
            return new Token(TokenType.Boolean, true);
        } 
        return new Token(TokenType.Boolean, false);
	}

    private Token logicalOr(Token t1, Token t2) //||
    {
        if (t1.getBoolean() || t2.getBoolean()){
            return new Token(TokenType.Boolean, true);
        }
        return new Token(TokenType.Boolean, false);
    }

	private Token multiple(Token t1, Token t2) throws CmsLatteException
    {
        if (t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Integer)
        {
            int sum =  t1.getInteger()*t2.getInteger();
            return new Token(TokenType.Integer, sum);
        }
        else if (t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Double)
        {
            double sum = t1.getInteger()*t2.getDouble();
            return new Token(TokenType.Double, sum);
        }
        else if (t1.getTokenType() == TokenType.Double && t2.getTokenType() == TokenType.Integer)
        {
            double sum =  t2.getInteger() * t1.getDouble(); 
            return new Token(TokenType.Double, sum);
        }
        else if (t1.getTokenType() == TokenType.Double && t2.getTokenType() == TokenType.Double)
        {
            double sum = t1.getDouble()* t2.getDouble(); 
            return new Token(TokenType.Double,sum);
        }
        else
        {
            throw new CmsLatteException(t1.getTokenType() +" * " + t2.getTokenType() + " operation is not allowed");
        }
    }	
	private Token divide(Token t1, Token t2) throws CmsLatteException
    {
        if (t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Integer)
        {
            int sum = t2.getInteger() / t1.getInteger();
            return new Token(TokenType.Integer, sum);
        }
        else if (t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Double)
        {
            double sum = t2.getDouble() / t1.getInteger();
            return new Token(TokenType.Double, sum);
        }
        else if (t1.getTokenType() == TokenType.Double && t2.getTokenType() == TokenType.Integer)
        {
            double sum = t2.getInteger() / t1.getDouble(); 
            return new Token(TokenType.Double, sum);
        }
        else if (t1.getTokenType() == TokenType.Double && t2.getTokenType() == TokenType.Double)
        {
            double sum =  t2.getDouble() / t1.getDouble(); 
            return new Token(TokenType.Double, sum);
        }
        else
        {
        	throw new CmsLatteException(t1.getTokenType() +" / " + t2.getTokenType() + " operation is not allowed");
        }
    }	
	private Token mod(Token t1, Token t2) throws CmsLatteException
    {
        if (t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Integer)
        {
            int sum =  t2.getInteger() % t1.getInteger();
            return new Token(TokenType.Integer, sum);
        }
        else
        {
        	throw new CmsLatteException(t1.getTokenType() +" % " + t2.getTokenType() + " operation is not allowed");
        }
    }
	private Token minus(Token t1, Token t2) throws CmsLatteException
    {
        if (t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Integer)
        {
            int sum = t2.getInteger() - t1.getInteger();
            return new Token(TokenType.Integer, sum);
        }
        else if (t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Double)
        {
            double sum = t2.getInteger() - t1.getDouble();
            return new Token(TokenType.Double, Double.toString(sum));
        }
        else if (t1.getTokenType() == TokenType.Double && t2.getTokenType() == TokenType.Integer)
        {
            double sum = t2.getInteger()-t1.getDouble();
            return new Token(TokenType.Double, sum);
        }
        else if (t1.getTokenType() == TokenType.Double && t2.getTokenType() == TokenType.Double)
        {
            double sum = t2.getDouble()-t1.getDouble();
            return new Token(TokenType.Double, sum);
        }
        else if (t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Date){
        	int day = t1.getInteger()*(-1);
        	Date d = LatteDateFunctions.dayAdd(t2.getDate(),day);
        	return new Token(d,TokenType.Date);
        }
        else if (t1.getTokenType() == TokenType.Date && t2.getTokenType() == TokenType.Date){
        	int days =0;
        	Date d1 = t1.getDate();
        	Date d2 = t2.getDate();
        	days = LatteDateFunctions.daysBetween(d1,d2);
        	if (d1.after(d2)){
        		days = days*(-1);
        	}
        	return new Token(days,TokenType.Integer);
        }
        else
        {
        	throw new CmsLatteException(t1.getTokenType() +" - " + t2.getTokenType() + " operation is not allowed");
        }
    }
	private Token plus(Token t1, Token t2) throws CmsLatteException {
		if(t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Integer){ // 1+2
			int sum =  t1.getInteger()+t2.getInteger(); // Integer.parseInt(t1.getValue()) + Integer.parseInt(t2.getValue());
			return new Token(TokenType.Integer,sum);
		}else if(t1.getTokenType() == TokenType.String && t2.getTokenType() == TokenType.String){ //"abc" + "abc"
			String sum = t2.getString() + t1.getString();
			return new Token(TokenType.String,sum);			
		}else if(t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Double){ //1 + 2.3
			double sum = t1.getInteger() + t2.getDouble();
			return new Token(TokenType.Double,sum);
		}else if(t1.getTokenType() == TokenType.Double && t2.getTokenType() == TokenType.Integer){ // 2.3 + 1
			double sum = t1.getDouble()+t2.getInteger();
			return new Token(TokenType.Double,sum);
		}else if(t1.getTokenType() == TokenType.Double && t2.getTokenType() == TokenType.Double){ //2.3 + 3.4
			double sum = t1.getDouble() + t2.getDouble(); 
			return new Token(TokenType.Double,sum);
		}else if(t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.String){ // 1 + "abc";
			String sum = t2.getString() + t1.getString();
			return new Token(TokenType.String,sum);
		}else if(t1.getTokenType() == TokenType.String && t2.getTokenType() == TokenType.Integer){ //"abc" + 1
			String sum = t2.getString() + t1.getString();
			return new Token(TokenType.String,sum);
			
		}else if(t1.getTokenType()==TokenType.ArrayField && t2.getTokenType() == TokenType.ArrayField){ //{1,2} + {3,4}
			ArrayField aT2 = t2.getValue(ArrayField.class); 
			ArrayField aT1 = t1.getValue(ArrayField.class); 

			ArrayField newArrayField = LatteArrayFunctions.arrayPlus(aT2,aT1);
			return new Token( newArrayField.getName(),TokenType.ArrayField,newArrayField);
			
		}else if(LatteUtil.isDISBD(t1.getTokenType()) && t2.getTokenType() == TokenType.ArrayField){ //{1,2} + 3=>{1,2,3}
			ArrayField aT2 = t2.getValue(ArrayField.class);
			Object o = t1.getValue();
			ArrayField newArrayField = LatteArrayFunctions.arrayPlus(aT2,o);
			return new Token(newArrayField,TokenType.ArrayField);
			
		}else if(t1.getTokenType() == TokenType.ArrayField && LatteUtil.isDISBD(t2.getTokenType()) ){ //3+{1,2} =>{3,1,2}
			ArrayField aT1 = t1.getValue(ArrayField.class);
			Object o = t2.getValue();
			ArrayField newArrayField = LatteArrayFunctions.arrayPlus(o,aT1);
			return new Token(newArrayField,TokenType.ArrayField);
			
		}else if(t1.getTokenType()==TokenType.MatrixField && t2.getTokenType() == TokenType.MatrixField){ //{{1,2}} + {{3,4},{5,6}} table + table
			MatrixField mT2 = t2.getValue(MatrixField.class);
			MatrixField mT1 = t1.getValue(MatrixField.class);
			
			MatrixField newMatrixField = LatteMatrixFunctions.matrixPlus(mT2,mT1);
			return new Token(newMatrixField,TokenType.MatrixField);
		}else if(t1.getTokenType()==TokenType.ArrayField && t2.getTokenType() == TokenType.MatrixField){ //{{1,2}} + {3,4,5} table + array
			ArrayField aT1 = t1.getValue(ArrayField.class);
			MatrixField mT2 = t2.getValue(MatrixField.class);
			
			MatrixField newMatrixField = LatteMatrixFunctions.matrixPlus(mT2,aT1);
			return new Token(newMatrixField,TokenType.MatrixField);
		}else if(t1.getTokenType()==TokenType.MatrixField && t2.getTokenType() == TokenType.ArrayField){ // {3,4,5} + {{1,2}} array + table
			MatrixField mT1 = t1.getValue(MatrixField.class);
			ArrayField aT2 = t2.getValue(ArrayField.class);
			MatrixField newMatrixField = LatteMatrixFunctions.matrixPlus(aT2,mT1);
			return new Token(newMatrixField,TokenType.MatrixField);			
			
		}else if(LatteUtil.isDISBD(t1.getTokenType()) && t2.getTokenType() == TokenType.MatrixField){ //{{1,2}} + 3=>{{1,2},{3,null}}
			Object o = t1.getValue();
			MatrixField mT2 = t2.getValue(MatrixField.class);
			MatrixField newMatrixField = LatteMatrixFunctions.matrixPlus(mT2,o);
			return new Token(newMatrixField,TokenType.MatrixField);	
		}else if( t1.getTokenType() == TokenType.MatrixField && LatteUtil.isDISBD(t2.getTokenType()) ){ //3+{1,2} =>{3,1,2}
			MatrixField mT1 = t1.getValue(MatrixField.class);
			Object o = t2.getValue();
			MatrixField newMatrixField = LatteMatrixFunctions.matrixPlus(o,mT1);
			return new Token(newMatrixField,TokenType.MatrixField);	
		}else if( t1.getTokenType() == TokenType.Date && t2.getTokenType() == TokenType.Integer ){ //3+{1,2} =>{3,1,2}
			Date d = LatteDateFunctions.dayAdd(t1.getValue(Date.class),t2.getInteger());
			return new Token(d,TokenType.Date);
		}else if( t1.getTokenType() == TokenType.Integer && t2.getTokenType() == TokenType.Date ){ 
			Date d = LatteDateFunctions.dayAdd(t2.getValue(Date.class),t1.getInteger());
			return new Token(d,TokenType.Date);
		}else{
			throw new CmsLatteException(t1.getTokenType() +" + " + t2.getTokenType() + " operation is not allowed");
		}
	}	
	/**
	 * tokenStack을 받아서 결과값을 가지는 Token을 생성해서 리턴한다
	 * @param infixStack
	 * @return
	 * @throws CmsLatteException
	 * @throws CloneNotSupportedException 
	 * @throws CmsLatteFunctionException 
	 */
	public Token eval(TokenStack infixStack) throws CmsLatteException, CmsLatteFunctionException{
		Stack<Token> postfix = Postfixer.getPostfixStack(infixStack);
		//TokenStack postfix = Postfixer.getPostfixStack(infixStack);
        Stack<Token> tmpStack = new Stack<Token>();
        Token t1, t2;
        //for (int idx = 0; idx < postfix.size(); idx++)
        while(postfix.isEmpty()==false)
        {
            //Token token =  postfix.get(idx); // poststack[idx] as EmulToken;
        	Token token = postfix.pop();
            switch (token.getTokenType())
            {
                case Plus:
                    if (tmpStack.size() < 2) { 
                   	 throw new CmsLatteException("plus operation fail"); 
                    }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();
                    tmpStack.push(plus(t1, t2));
                    break;
                case Multiple:
                    if (tmpStack.size() < 2) { 
                   	 throw new CmsLatteException("multiple operation fail"); 
                    }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();

                    tmpStack.push(multiple(t1, t2));
                    break;

                case Minus:{
                	
                	if(!postfix.isEmpty() && isOperator(postfix.peek()) && tmpStack.size()==2 ){
                		Token tmp1 = tmpStack.pop();
                		if(tmp1.getTokenType() == TokenType.Integer){
                			tmpStack.push(new Token(tmp1.getValue(Integer.class)*(-1),TokenType.Integer));
                		}else if(tmp1.getTokenType()==TokenType.Double){
                			tmpStack.push(new Token(tmp1.getValue(Double.class)*(-1),TokenType.Double));
                		}
                	}else if(postfix.isEmpty() && tmpStack.size()==1 && isNumberType(tmpStack.peek())){
                		Token tmp1 = tmpStack.pop();
                		if(tmp1.getTokenType() == TokenType.Integer){
                			tmpStack.push(new Token(tmp1.getValue(Integer.class)*(-1),TokenType.Integer));
                		}else if(tmp1.getTokenType()==TokenType.Double){
                			tmpStack.push(new Token(tmp1.getValue(Double.class)*(-1),TokenType.Double));
                		}
                		
                	}else if (tmpStack.size() >= 2) { 
                        t1 = tmpStack.pop();
                        t2 = tmpStack.pop();

                        tmpStack.push(minus(t1, t2));
                    }else{
                    	throw new CmsLatteException("minus operation fail");
                    }
               	}
                    break;
                case Divide:
                    if (tmpStack.size() < 2) { 
                   	 throw new CmsLatteException("divide operation fail");
                    }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();

                    tmpStack.push(divide(t1, t2));
                    break;
                case Mod:
                    if (tmpStack.size() < 2)  { 
                   	 throw new CmsLatteException("mod operation fail");
                    }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();

                    tmpStack.push(mod(t1, t2));
                    break;
                case LogicalEqual:
					 if (tmpStack.size() < 2) {
						throw new CmsLatteException("LogicalEqual equal operation fail");
					 }
                    tmpStack.push(logicalEqual(tmpStack.pop(), tmpStack.pop()));
                    break;                     
                case LogicalAnd:
                    if (tmpStack.size() < 2) { throw new CmsLatteException("logical and operation fail"); }
                    tmpStack.push(logicalAnd(tmpStack.pop(), tmpStack.pop()));
                    break;                     
                case LogicalOr:
                    if (tmpStack.size() < 2) { throw new CmsLatteException("logical or operation fail"); }
                    tmpStack.push(logicalOr(tmpStack.pop(),tmpStack.pop()));
                    break;
                case LogicalNotEqual: // !=
                    if (tmpStack.size() < 2) { throw new CmsLatteException("logical != operation fail"); }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();
                    tmpStack.push(logicalNotEqual(t2, t1));
                    break;
                case LessThan: // <
                    if (tmpStack.size() < 2) { throw new CmsLatteException("logical less than(<) operation fail"); }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();
                    tmpStack.push(logicalLessThan(t2, t1));
                    break;
                case GreatThan: //>
                    if (tmpStack.size() < 2) { throw new CmsLatteException("logical great than(>) operation fail"); }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();
                    tmpStack.push(logicalGreatThan(t2, t1));
                    break;

                case LessThanOrEqual:// <=
                    if (tmpStack.size() < 2) { throw new CmsLatteException("logical less than or equal (<=) operation fail"); }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();
                    tmpStack.push(lessThanOrEqual(t2, t1));

                    break;
                case GreatThanOrEqual://>=
                    if (tmpStack.size() < 2) { throw new CmsLatteException("logical great than or equal(>=) operation fail"); }
                    t1 = tmpStack.pop();
                    t2 = tmpStack.pop();
                    tmpStack.push(greatThanOrEqual(t2, t1));
                    break;
                case Not:
                	if (tmpStack.size() < 1) { throw new CmsLatteException("logical not(!)  operation fail"); }
                	t1 = tmpStack.pop(); 
                	tmpStack.push(new Token(!t1.getBoolean(),TokenType.Boolean));
                	break;
                case Field:
                {
                	String fieldDesc = token.getString(); //a, a[1].trim();
                	Field field = fieldChangGo.getField(fieldDesc);
                	if (field !=null && field instanceof ArrayField){ //a
                    	ArrayField arrayField = (ArrayField)field;
                		tmpStack.push(new Token(arrayField,TokenType.ArrayField));
                	}else if(field !=null && field instanceof MatrixField){//t
                    	MatrixField matrixField = (MatrixField)field;
                		tmpStack.push(new Token(matrixField,TokenType.MatrixField));
                	}else{
	                	FieldParser fieldParser = new FieldParser();
	                	FieldName fieldName = fieldParser.scan(fieldDesc);
	                	Token valueToken = fieldChangGo.getValueToken(fieldName);
		                if (valueToken == null)
		                {
		                    throw new CmsLatteException(fieldName.getName() + " is not defined");
		                }
		                tmpStack.push(valueToken);
                	}
                }
                    break;
                case Function:
                {
                	MunjangScanner ms = new MunjangScanner();
                	FunctionScanner fs = new FunctionScanner();
                	fs.scan(token.getString());
                	FunctionArguments funcArgs = new FunctionArguments(fs.getFunctionName());
                	for (String s : fs.getArgStringList()) {
						TokenStack argTokenStack = ms.scan(s);
						funcArgs.addArgument(eval(argTokenStack));
					}
                	Object o = FunctionManager.getInstance().execute(funcArgs);
                	if(LatteUtil.isDISBD(o)){
                		tmpStack.push(new Token(o));
                	}else if(LatteUtil.isArrayField(o)){
                		tmpStack.push(new Token(o,TokenType.ArrayField));
                	}else if(LatteUtil.isMatrixField(o)){
                    	tmpStack.push(new Token(o,TokenType.MatrixField));
                	}
                }
                	break;
                case ArrayInit:{ //ArrayField로 전환해서 imsiChangGo에 넣는다
                	ArrayParser ap = new ArrayParser();
                	ap.scan(token.getValue().toString());
                	String key = token.getHashKey();
                	ArrayField arrayField = ap.getArrayField(key);
                	tmpStack.push(new Token(arrayField,TokenType.ArrayField)); //토큰을 만들어 push
                }
                	break;
                case TableInit:{ //MatrixField로 전환해서 imsiChangGo에 넣는다.
                	MatrixParser mp = new MatrixParser();
                	mp.scan(token.getValue().toString());
                	String key = token.getHashKey();
                	MatrixField matrixField = mp.getMatrixField(key);
                	tmpStack.push(new Token(matrixField,TokenType.MatrixField));
                }
                	break;
                case String:
                case Double:
                case Integer:
                case Boolean:
                case Date:
               	 	tmpStack.push(token);
                    break;  
            }
        }
        if (tmpStack.size() < 1){
        	throw new CmsLatteException("susik stack operation fail");
        }
        return tmpStack.pop();
	}
	/**
	 * 4칙연산자인지 판별 
	 * @param t
	 * @return
	 */
	private boolean isOperator(Token t) {
		if(t.getTokenType()==TokenType.Plus ||
				t.getTokenType()==TokenType.Minus ||
				t.getTokenType()==TokenType.Multiple ||
				t.getTokenType()==TokenType.Divide 
		){
			return true;
		}
		return false;
	}
	
}
