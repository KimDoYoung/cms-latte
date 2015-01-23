package kr.dcos.cmslatte.scanner;

import java.util.ArrayList;
import java.util.List;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.functions.LatteCommonFunctions;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

/**
 * list에  다 담아 두지 않고, lastIndex로 관리를 한다.
 * 값을 꺼내기 위해서는 
 * @author Administrator
 *
 */
public class ArrayParser extends ScannerBase{
	private List<Object> list;
	private boolean optionAllString = false;
	//private int optionMaxSize= -1;
	
	private int lastIndex=-1;
	

	public ArrayParser(){
		super();
		list = new ArrayList<Object>();
	}
	// 
	// public functions
	//
	public Object get(int index) throws CmsLatteException{
		if(index < 0 || index >=list.size()){
			throw new CmsLatteException("index out of bound");
		}
		return list.get(index);
	}
	public int lastIndex(){
		return lastIndex;
	}
	public int size(){
		return lastIndex + 1;
	}
	/**
	 * arrayInitDesc 
	 * {$:1,2,3} {1,"$2","2:3","3,4"}, { 1,2,3}, {$4:}, { $: 1,2,3}
	 * @param arrayInitDesc
	 * @throws CmsLatteException 
	 */
	public void scan(String arrayInitDesc) throws CmsLatteException{
		init();
		if(LatteUtil.isNullOrEmpty(arrayInitDesc)){
			return ;
		}
		int optionMaxSize= -1;
		
		srcArray = arrayInitDesc.trim().toCharArray();
		char ch = srcArray[index];
		if(ch != '{'){
			throw new CmsLatteException(arrayInitDesc +"  is not array init");
		}
		
		index++;
		index = skipWhitespace();
		ch = srcArray[index];
		if(ch == '$'){ //첫번째 문자가 $이면
			int posColon1=index;
			index++;
			int posColon2 = indexOf('$');
			if(posColon2<0){
				throw new CmsLatteException(arrayInitDesc +"  is not array init description");
			}
			
			if(posColon1+1 == posColon2){
				optionAllString = true;
			}else{
				String s = getString(posColon1+1,posColon2-1);
				if(s.trim().length()<1 || s.trim().equals("s")){
					optionAllString = true;
				}else if(LatteUtil.isValidInteger(s)){
					optionMaxSize = Integer.parseInt(s);
				}else{
					throw new CmsLatteException(arrayInitDesc +"  is not array init description");
				}
			}
			index = posColon2+1;
		}		
		while(index < srcArray.length){
			index = skipWhitespace();
			ch = srcArray[index];
			if(ch==','){
				addToList();
			}else if(ch=='"'){
				fillBufferWithStringPart(false);
				addToList(TokenType.String);
				index = skipWhitespace();
				continue;
			}else if(ch == '}'){
				addToList();
			}else{
				buffer.append(ch);
			}
			index++;			
		}
		lastIndex = list.size()-1;
		if(list.size()<optionMaxSize){
			lastIndex = optionMaxSize - 1;
		}
	}
	/**
	 * list에 추가한다 option에 따라서 추가한다
	 * buffer를 지운다
	 * @throws CmsLatteFunctionException 
	 */
	private void addToList() throws CmsLatteFunctionException {
		if(buffer.length()<1) return;
		addToList(TokenType.Unknown);
	}
	private void addToList(TokenType tokenType) throws CmsLatteFunctionException {
		String s = buffer.toString();
		if(optionAllString || tokenType == TokenType.String){
			list.add(s);
		}else{
			TokenType t = LatteUtil.getTokenTypeWithString(s.trim());
			switch (t) {
			case Integer:
				list.add(LatteCommonFunctions.toInteger(s.trim()));
				break;
			case Double:
				list.add(LatteCommonFunctions.toDouble(s.trim()));
				break;
			case Date :
				list.add(LatteCommonFunctions.toDate(s.trim()));
				break;				
			case Boolean :
				list.add(LatteCommonFunctions.toBoolean(s.trim()));
				break;				
			default:
				break;
			}
		}
		bufferClear();
		
	}
	/**
	 * parsing한 결과를 ArrayField로 리턴해준다
	 * @return
	 * @throws CmsLatteException 
	 */
	public ArrayField getArrayField(String name) throws CmsLatteException{
		ArrayField arrayField = new ArrayField(name);
//		for (Object o : list) {
//			arrayField.append(o);
//		}
		arrayField.append(list.toArray());
		return arrayField;
		
	}
	@Override
	protected void init(){
		list.clear();
		index = 0;
		
		optionAllString = false;
		lastIndex= -1;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("ArrayParser:\n");
		for(int i=0;i<size();i++){
			Object o;
			try {
				o = get(i);
				sb.append("\t" + (i) + ":" + o.toString());
				sb.append("\n");
			} catch (CmsLatteException e) {
				;
			}
		}
		
		return sb.toString();
	}

}
