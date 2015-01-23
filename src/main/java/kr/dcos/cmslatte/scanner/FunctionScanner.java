package kr.dcos.cmslatte.scanner;

import java.util.ArrayList;
import java.util.List;

import kr.dcos.cmslatte.exception.CmsLatteFunctionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * substring("abcdef",1,3)
 * 문자열을 받아서. functionName
 * 과 List<String>형태로 ,로 분리된 문자열들을 리스트로 제공한다. 
 * @author Administrator
 *
 */
public class FunctionScanner extends ScannerBase{
	
	private static Logger logger = LoggerFactory
			.getLogger(FunctionScanner.class);
	

	private String funcDesc; //원본문자열
	private String functionName;
	private List<String> argStringList;
	/**
	 * constructor
	 */
	public FunctionScanner(){
		super();
		argStringList = new ArrayList<String>();
	}
	
	public void scan(String funcDesc) throws CmsLatteFunctionException{
		if(funcDesc == null || funcDesc.length()<1)return ;
		
		argStringList.clear();
		this.funcDesc = null;
		
		this.funcDesc = funcDesc.trim();
		
		String s = this.funcDesc;
		int p = s.indexOf('(');
		if(p<0){
			logger.error(funcDesc + " is not valid function description");
			return;
		}
		functionName = s.substring(0,p);
		index = p+1;
		srcArray = s.toCharArray();
		//logger.debug(":::: start char : " +srcArray[index]);
		//substring("12345",1,3);
		while(index<srcArray.length){
			char ch = srcArray[index];
			if(ch==')'){
				if(buffer.length()>0){
					argStringList.add(buffer.toString());
					bufferClear();
				}
				break;
			}
			else if(Character.isWhitespace(ch)){
				index = skipWhitespace();
				continue;
			}
			else if(ch==','){
				if(buffer.length()<1){
					throw new CmsLatteFunctionException(functionName, "argument missint");
				}
				argStringList.add(buffer.toString());
				bufferClear();	
				index++;
			}
			else if(ch=='['){
				fillBufferUntilPair('[', ']');
			}
			else if(ch=='"'){
				fillBufferWithStringPart(true); //"를 포함 한 문자를 넣는다.
				continue;
			}
			else if(ch=='('){
				fillBufferUntilPair('(', ')');
				continue;
			}else{
				buffer.append(ch);
				index++;
			}
		}
		
	}
	public String getFunctionName() {
		return functionName;
	}
	public List<String> getArgStringList() {
		return argStringList;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("FunctionScanner: functionName:" + functionName);
		sb.append("\n");
		sb.append("\t arg size:" + argStringList.size());
		sb.append("\n");
		int i=0;
		for (String s : argStringList) {
			sb.append("\t "+i+":" + s +"\n");i++;
		}
		return sb.toString();
	}
}
