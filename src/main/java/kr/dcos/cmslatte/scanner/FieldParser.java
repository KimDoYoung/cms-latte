package kr.dcos.cmslatte.scanner;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.field.FieldName;
import kr.dcos.cmslatte.field.FieldType;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.utils.LatteUtil;

/**
 * Field문자열을 파싱한다.
 * a, a.trim(), a[1].substring(3,5)
 * a[1,a[3]].substring(b[4,5],b[5,6]) 
 * 
 * fieldDesc : a, a.trim(), a[1], a[1].trim(), a[1,2], a[1,3].substring(3,5);
 * fieldDesc = prefix_fieldName[susik,susik].funcDesc로 이루어진다.
 * 
 * fieldDesc 를 parsing해서 fieldName 클래스를 만들어서 리턴한다
 * fieldName클래스는 FieldChanggo에서 그 값을 찾기 위해서 이다.
 * 
 * scan함수에서 파싱해서 FieldName을 만들어서 리턴한다.
 * FieldDesc은 prefix_name[rowSusikString,colSusikString].subFuncDesc 
 * 으로 이루어지므로 각각 prefix, name rowSusuikString,colSusuikString, subFuncDesc을
 * 찾을 수 있도록 파싱한다
 * @author Administrator
 *
 */
public class FieldParser extends ScannerBase {
	
	//private static Logger logger = LoggerFactory.getLogger(FieldParser.class);

	private String fieldDesc;
	private TokenStack tokenStack=null;
	private FieldName fieldName=null;
	/**
	 * constructor
	 */
	public FieldParser(){
		super();
		tokenStack = new TokenStack();
	}
	public FieldName scan(String fieldDesc) throws CmsLatteException{
		
		init();
		this.fieldDesc = fieldDesc.trim();
		if(tokenStack != null) tokenStack = null;
		if(fieldName !=null) fieldName = null;
		tokenStack = new TokenStack();
		fieldName = new FieldName();

		if(LatteUtil.isNullOrEmpty(this.fieldDesc)) {
			return null;
		}
		//logger.debug("fieldScanner scan...");
		
		srcArray = this.fieldDesc.toCharArray();
		//prefix
		if(srcArray.length > 1 && srcArray[1]=='_'){
			fieldName.setPrefix(srcArray[0]);
			index = 2;
			if(index == srcArray.length){ //딱 2글짜이고 a_ 형태인 경우 허용되지 않는다.
				throw new CmsLatteException(this.fieldDesc +" is not allowed variable name");
			}
		}
		int leftBranketPos = indexOf('[');
		int jumPos  = -1;
		if(leftBranketPos > 0){ // [ 있다면
			setFieldName(getString(index,leftBranketPos-1));
			int commaPos = getPosOfUsefulChar(leftBranketPos+1,','); //index부터 ,가 있는지 찾는다.
			int rightBranketPos = -1; 
			if(commaPos<0){ // ,없다면 array
				rightBranketPos = getPosOfUsefulChar(leftBranketPos+1,']');
				if(rightBranketPos<0){
					throw new CmsLatteException(this.fieldDesc +" is not allowed variable name");
				}
				String rowSusikString = getString(leftBranketPos+1,rightBranketPos-1);
				if(rowSusikString.trim().length()<1){
					throw new CmsLatteException(this.fieldDesc +" parsing error");
				}
				fieldName.setRowSusikString(rowSusikString);
				index = rightBranketPos+1;
			}else{ // ,가 있다 table 
				rightBranketPos = getPosOfUsefulChar(commaPos+1,']');
				if(rightBranketPos<0){
					throw new CmsLatteException(this.fieldDesc +" is not allowed variable name");
				}
				//comma까지를 row로 comma 다음부터 ] 까지를 col으로 
				String rowSusikString = getString(leftBranketPos+1,commaPos-1);
				String colSusikString = getString(commaPos+1,rightBranketPos-1);
				if(rowSusikString.trim().length()<1 || colSusikString.trim().length()<1){
					throw new CmsLatteException(this.fieldDesc +" parsing error");
				}
				fieldName.setRowSusikString(rowSusikString);
				fieldName.setColSusikString(colSusikString);
				index = rightBranketPos+1;
			}
		}
		jumPos = getPosOfUsefulChar(index,'.');
		if(jumPos > 0){ // .이 있다면
			if(leftBranketPos<0){
				setFieldName(getString(index,jumPos-1));
			}
			String funcDesc = getString(jumPos+1,srcArray.length-1);
			if(funcDesc.trim().length()<1){
				throw new CmsLatteException(this.fieldDesc +" parsing error");
			}
			fieldName.setSubFuncDesc(funcDesc);
			
		}else{ // [ 도 없고 점도 없다면 constanttype 으로 funcDesc이 없다
			if(leftBranketPos<0){
				String tmpName = this.fieldDesc.substring(index);
				setFieldName(tmpName);
			}
		}
		if(fieldName.getRowSusikString() == null){
			fieldName.setFieldType(FieldType.Constant);
		}else if( fieldName.getRowSusikString() != null && fieldName.getColSusikString() == null){
			fieldName.setFieldType(FieldType.Array);
		}else{
			fieldName.setFieldType(FieldType.Matrix);
		}
				
		return fieldName;
	}

	private void setFieldName(String name) throws CmsLatteException {
		String tmpName = name.trim();
		if(LatteUtil.isValidFieldName(tmpName)==false){
			throw new CmsLatteException(this.fieldDesc +" is not allowed variable name");
		}else{
			fieldName.setName(tmpName);
		}
	}

	/**
	 * index에서 부터 srcArray를 쭉 훑어가면서 c를 찾는다.
	 * 이때 ( ) 사이나, " " 사이나, [ ] 사이에 c가 있는 경우는 무시한다
	 * index는 움직이지않는다.
	 * @param index
	 * @param c
	 * @return
	 */
	private int getPosOfUsefulChar(int index, char c) {
		int i = index;
		int countOfGalHo=0;
		int countOfBranket=0;
		boolean inStr = false;
		while(i<srcArray.length){
			char ch = srcArray[i];
			if(ch == '('){
				if(ch == c && (!inStr) && countOfBranket==0 && countOfGalHo==0){
					return i;
				}
				countOfGalHo++;
			}
			else if(ch == ')'){
				if(ch == c && (!inStr) && countOfBranket==0 && countOfGalHo==0){
					return i;
				}
				countOfGalHo--;
			}
			else if(ch == '['){
				if(ch == c && (!inStr) && countOfBranket==0 && countOfGalHo==0){
					return i;
				}				
				countOfBranket++;
			}
			else if(ch == ']'){
				if(ch == c && (!inStr) && countOfBranket==0 && countOfGalHo==0){
					return i;
				}				
				countOfBranket--;
			}
			else if(ch == '"') inStr = (!inStr);
			if(ch == c && (!inStr) && countOfBranket==0 && countOfGalHo==0){
				return i;
			}
			i++;
		}
		return -1;
	}
	
}
