package kr.dcos.cmslatte.scanner;

import java.util.ArrayList;
import java.util.List;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.scanner.LatteItem.LatteItemType;

/**
 * Template를 Text와 LatteCode로 구분하여 저장한다
 * @author Administrator
 *
 */
public class LatteSplitter extends ScannerBase {

	private List<LatteItem> list;
	private String template;
	private CodeScanner codeScanner;
	//
	// 생성자
	//
	public LatteSplitter() throws CmsLatteException{
		this(null);
	}
	public LatteSplitter(String template) throws CmsLatteException{
		super();
		list = new ArrayList<LatteItem>();
		this.template = template;
		codeScanner = new CodeScanner();
		scan();
	}
	//
	// public function
	//
	public void clear(){
		list.clear();
		template = null;
	}
	//
	// getter,setter
	//
	public List<LatteItem> getList() {
		return list;
	}
	public void setList(List<LatteItem> list) {
		this.list = list;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) throws CmsLatteException {
		clear();
		this.template = template;
		scan();
	}
	
	private void scan() throws CmsLatteException{
		if(template == null || template.length()< 1){
			return;
		}
		index = 0;
		srcArray = template.toCharArray();
		char nowChar=0,nextChar=0;
		String twoChar = null;
		
		while(index < srcArray.length){
			nowChar = srcArray[index];
			//if(Character.isWhitespace(nowChar)){index++;continue;}
			nextChar = 0x00;twoChar = "";
			if(notScanLength()>=1) nextChar = srcArray[index+1];
			if (notScanLength()>=2) {  //남은 갯수가 2보다 크거나 같다면
				twoChar = Character.toString(nowChar) + Character.toString(nextChar);
			}
			
			if(twoChar.equals("<@")){
				if(isBufferNotEmpty()){
					addTextToList(buffer.toString()); //리스트에 텍스트 추가
					bufferClear();
				}
				index+=2; //<@ 2 char go
				String s = fillBufferUntil("@>"); //@>일때까지 가져온다
				if(isNotEmpty(s)){
					addLatteCodeToList(buffer.toString());
					bufferClear();
				}
			}else{
				buffer.append(nowChar);
				index++;
			}
			
		}
		if(isBufferNotEmpty()){
			addTextToList(buffer.toString());
		}
	}
	
	
	//list에 Text (html or jsp code)를 넣는다
	private void addTextToList(String content) {
		LatteItem item = new LatteItem(LatteItemType.JustString, content);
		list.add(item);
	}
	/**
	 * code부분을 codeScanner로 파싱해서 list에 넣는다.
	 * @param content
	 * @throws CmsLatteException 
	 */
	private void addLatteCodeToList(String content) throws CmsLatteException  {
		List<LatteItem> codelist;
		codelist = codeScanner.scan(content);
		if(codelist != null){
			list.addAll(codelist);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (LatteItem item : list) {
			sb.append((i++) +":" + item.toString() +"\n");
		}
		return sb.toString();
	}
}
