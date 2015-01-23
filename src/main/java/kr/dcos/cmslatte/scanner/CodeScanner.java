package kr.dcos.cmslatte.scanner;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.scanner.LatteItem.LatteItemType;
import kr.dcos.cmslatte.token.TokenStack;

/**
 * 입력받은 <@...@>부분을 해석해서 tokenstack의 리스트로 만든다
 * <@ , @> 를 포함하지 않는 안쪽 문자열들
 * 1. ... ; 이면 한개의 문장으로 munjangScanner에 tokenstack을 만들게 한다
 * 2. ... default,case,begin,end이면 stack에 넣게 한다.
 * 3. 주석을 소거한다.
 * @author Administrator
 *
 */
public class CodeScanner extends ScannerBase {
	
	private static Logger logger = LoggerFactory.getLogger(CodeScanner.class);
	

	private List<LatteItem> list;
	private MunjangScanner munjangScanner;
	
	//
	//constructor
	//
	public CodeScanner(){
		super();
		list = new ArrayList<LatteItem>();
		munjangScanner = new MunjangScanner();
		
	}
	
	public List<LatteItem> scan(String src) throws  CmsLatteException{
		String source = src;
		if(source == null || source.length()<1 )return null;
		list.clear();
		init();
		
		srcArray = source.toCharArray();
		char nowChar=0,nextChar=0;
		String twoChar=null;
		StringBuilder word = new StringBuilder();
		while(index < srcArray.length){
			nowChar = srcArray[index];
			twoChar = "";nextChar = 0x00;
			if(notScanLength()>=1)nextChar = srcArray[index + 1];
			if (notScanLength()>=2) {  //남은 갯수가 2보다 크거나 같다면
				twoChar = Character.toString(nowChar) + Character.toString(nextChar);
			}
			if(Character.isWhitespace(nowChar)){
				if(word.toString().length()>0){
					if(word.toString().equals("begin") || word.toString().equals("end")){
						makeBufferToTokenStackAndAppendList();
						makeStackAndAppendListWithKeyword(word.toString()); //begin..일경우 begin으로 만든다.
					}else{
						buffer.append(word.toString());
						buffer.append(nowChar);
					}
					word.setLength(0);
				}
				index = skipWhitespace();
				continue;
			}
			if(nowChar == '"'){
				if(word.length()>0){
					buffer.append(word.toString());
					word.setLength(0);
				}
				fillBufferWithStringPart(true);
				continue;
			}else if(nowChar == '?'){
				buffer.append(word.toString());
				if(fillBufferUntil(";",true)==null){
					throw new CmsLatteException("questionif not end");
				}
				makeBufferToTokenStackAndAppendList();
				word.setLength(0);
				index++;
				continue;
//				TokenStack lastStack=null;
//				if(word.length()<1){ //(1==1) ? a : b; 로 스페이스를 만나서 이미 넣었다
//					LatteItem lastItem = list.get(list.size()-1);
//					if(lastItem.getType() != LatteItemType.LatteCode){
//						throw new CmsLatteException("Syntax error , QuestionIf fail");
//					}
//					lastStack = lastItem.getTokenStack();
//				}else{
//					buffer.append(word.toString());
//				}
				
			}else if(nowChar == ':'){
				buffer.append(word.toString());
				makeBufferToTokenStackAndAppendList();
				word.setLength(0);
				index++;
				continue;				
			}else if(nowChar == ';'){
				buffer.append(word.toString());
				makeBufferToTokenStackAndAppendList();
				word.setLength(0);
				index++;
				continue;
			}else if(nowChar == '/' && twoChar.equals("//")){ //... \n까지 skip
				index += 2;
				skipUntil('\n');
				continue;
			}else if(nowChar == '/' && twoChar.equals("/*")){ // /* .. */까지 skip
				index += 2;
				skipUntil("*/");
				continue;
			}
			word.append(nowChar);
			index++;
		}
		buffer.append(word.toString());
		makeBufferToTokenStackAndAppendList();			
		return list;
	}
	

	
	/**
	 * begin, end... case,default일경우
	 * @param string
	 * @throws CloneNotSupportedException 
	 * @throws CmsLatteException 
	 */
	private void makeStackAndAppendListWithKeyword(String word) throws  CmsLatteException {
		String src = word;
		//logger.debug("make token Stack with :" + src);
		TokenStack tokenStack = munjangScanner.scan(src);
		LatteItem item = new LatteItem(LatteItemType.LatteCode, src, tokenStack	);
		list.add(item);
	}
	/**
	 * buffer에 들어 있는 것을 MunjangScanner에 넘겨서 tokenStack을 얻은 다음에
	 * 그것으로 LatteItem을 만들어서 list에 넣는다
	 * @throws CloneNotSupportedException 
	 * @throws CmsLatteException 
	 */
	private void makeBufferToTokenStackAndAppendList() throws  CmsLatteException {
		if(buffer.length()<1)return;
		//tokenstack을 만들어 리스트에 넣는다
		String src = buffer.toString();
		//logger.debug("make token Stack with :[" + src +"]");
		TokenStack tokenStack = munjangScanner.scan(src);
		LatteItem item = new LatteItem(LatteItemType.LatteCode, src, tokenStack	);
		list.add(item);
		//buffer clear
		bufferClear();
	}


	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (LatteItem item : list) {
			sb.append(item.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
