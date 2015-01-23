package kr.dcos.cmslatte.scanner;

import kr.dcos.cmslatte.exception.CmsLatteException;

/**
 * 문자열을 파싱할때의  기본 클래스 
 * @author Administrator
 *
 */
public class ScannerBase {
	protected int index;
	protected StringBuilder buffer ;
	protected char[] srcArray;
	//protected boolean inStr;

	//
	// constructor
	//
	public ScannerBase(){
		buffer= new StringBuilder();
		index = 0;
		srcArray = null;
		//inStr = false;
	}
	protected void init(){
		index = 0;
		buffer.setLength(0);
		srcArray = null;
	}
	/**
	 * null, "", all whitespace -> empty is true
	 * @param s
	 * @return
	 */
	public boolean isEmpty(String s){
		if(s == null || s.length()<1) {
			return true;
		}
		for (char  c : s.toCharArray()) {
			if(!Character.isWhitespace(c)) return false;
		}
		return true;
	}
	public boolean isNotEmpty(String s){
		return !isEmpty(s);
	}
	public boolean isBufferEmpty() {
		return isEmpty(buffer.toString());
	}
	public boolean isBufferNotEmpty() {
		return !isEmpty(buffer.toString());
	}
	public void bufferClear(){
		buffer.setLength(0);
	}
	//남은 갯수
	public int notScanLength(){
		return srcArray.length - index -1;
	}
	
	/**
	 * endMatch 일때까지 데이터를 가져온다. 
	 * index Point는 endMatch 다음에 위치한다.
	 * 넘겨주는 데이터에는 endMatch가 포함되지 않는다.
	 * @param endMatch
	 * @return
	 */
	public String fillBufferUntil(String endMatch) {
		return this.fillBufferUntil(endMatch, true);
	}
	public String fillBufferUntil(String endMatch,boolean skipString) {
		char[] tmp = endMatch.toCharArray();
		if(notScanLength()<tmp.length){return null;}
		boolean inStr=false;
		char prev = '\0';
		while(index <= (srcArray.length-tmp.length)){
			if(!inStr&&match(tmp)){
				index += tmp.length; // @> 2 char 
				return buffer.toString();
			}
			// 약간 문제가 있네
			if(skipString && prev != '\\' && srcArray[index]=='"') {
				inStr = !inStr;
			}
			buffer.append(srcArray[index]);
			prev = srcArray[index];
			index++;
		}
		return null;
	}
	//srcArray의 현재 index에서부터 tmp가 match되는지 여부를 리턴한다
	public boolean match(char[] tmp) {
		int j = index;
		for(int i=0;i<tmp.length;i++,j++){
			if(srcArray[j] != tmp[i]) return false;
		}
		return true;
	}
	/**
	 * [ ], ( ) 을 찾기 위해서
	 * 마지막 point는 right 다음이 된다.
	 * left는 시작char [ or ( 여야한다.
	 * [ 에서 ]까지 포함되는 모든 글짜들로 이루어진 문자열을 버퍼에 넣는다.
	 * [ 안에 [ 가 있는 경우가 있다.
	 * skipString은 " "에 left나 right문자가 있어도 무방하다
	 * @param left
	 * @param right
	 * @return
	 */
	public void fillBufferUntilPair(char left,char right){
		
//		buffer.append(left); index++;
//		int leftCount = 1;
//		while(index < srcArray.length){
//			char ch = srcArray[index];
//			if(ch == left){
//				leftCount++;
//			}else if(ch == right){
//				leftCount--;
//			}
//			buffer.append(ch);
//			index++;
//			if(leftCount == 0)return;
//		}
		this.fillBufferUntilPair(left, right, false);
	} 
	public void fillBufferUntilPair(char left,char right,boolean skipString){
		buffer.append(left); index++;
		int leftCount = 1;
		boolean inStr=false;
		while(index < srcArray.length){
			char ch = srcArray[index];
			if(ch == left && !inStr){
				leftCount++;
			}else if(ch == right && !inStr){
				leftCount--;
			}else if(ch == '"' && skipString){
				inStr = !inStr;
			}
			buffer.append(ch);
			index++;
			if(leftCount == 0 && !inStr)return;
		}

	}
	/**
	 * index를 whitespace가 아닐때까지 전진시킨다.
	 * @return
	 */
	public int skipWhitespace(){
		char ch = srcArray[index];
		while(Character.isWhitespace(ch)){
			index++;
			if(index >= srcArray.length) break;
			ch = srcArray[index];
		}
		return index;
	}
	/**
	 * ch일때까지 index를 전진 시킨다. index는 ch다음에 위치한다
	 * @param ch
	 */
	public void skipUntil(char ch) {
	
		while(index<srcArray.length){
			char nowch = srcArray[index++];
			if(nowch == ch){
				index++; 
				return;
			}
		}
		return;
	}
	/**
	 * match를 만날때까지 계속 전진하고 match다음의 index에 위치시킨다
	 * @param match
	 */
	public void skipUntil(String match){
		while(index<srcArray.length){
			if(match(match.toCharArray())){
				index += match.length();
				return;
			}
			index++;
		}
	}
	/**
	 * ch가 string의 문자중 하나라면 true를 리턴한다
	 * charInString(ch,"abc") 일 경우 ch가 a,b,c 3문자중하나이면 true
	 * 
	 * @param ch
	 * @param string
	 * @return
	 */
	public boolean charInString(char ch ,String string){
		char[] chArray = string.toCharArray();
		for (char c : chArray) {
			if(ch == c){
				return true;
			}
		}
		return false;
	}
	/**
	 * ".."을 모두 버퍼에 담고 index를 리턴한다.
	 * "을 만났을 때 호출되는 것으로 가정한다.
	 * 쌍따옴표 안의 부분만을 buffer에 채울때는 includeQuoto가 false여야한다
	 * index는 마지막" 다음
	 * \\ 와 \" 인 경우만 \,"로 한글자만 넣고 그렇치 않고 \n인경우과 같은 경우는 그냥 \도 한개의 
	 * char로 넣는다.
	 * @return
	 */
	public void fillBufferWithStringPart(boolean includeQuoto){
		if(includeQuoto == true){
			buffer.append(srcArray[index]); // "
		}	
		index++;
		while(index<srcArray.length){
			char ch = srcArray[index];
			if(ch == '\\' && (index+1)<srcArray.length){
				char nextch = srcArray[index+1];
				if(nextch == '"'){
					buffer.append(ch);
					buffer.append(nextch);
					index+=2;
				}else{
					buffer.append(ch);
					index++;
				}
			}else if(ch == '"'){
				if(includeQuoto == true){
					buffer.append(ch);
				}
				index++;
				return;
			}else{
				buffer.append(ch);
				index++;
			}
		}
	}
	/**
	 * buffer에 들어 있는 char 의 갯수를 리턴한다
	 * index 값은 변하지 않는다
	 * @param ch
	 * @return
	 */
	public int countOf(char ch){
		if(buffer.length()<1) return 0;
		int count = 0;
		String s = buffer.toString();
	    for (int i=0; i < s.length(); i++)
	    {
	        if (s.charAt(i) == ch){
	             count++;
	        }
	    }
	    return count;
	}
	/**
	 * 현재의 index에서부터 srcArray에서 찾아나가서 
	 * ch가 발견되면  그 index를, 발견하지 못하면 -1을 리턴한다
	 * @param ch
	 * @return
	 */
	public int indexOf(char ch){
		int p = index;
		while(p<srcArray.length){
			if(srcArray[p] == ch){
				return p;
			}
			p++;
		}
		return -1;
	}
	/**
	 * srcArray에서 beginIndex에서부터 endIndex까지의 char를 묶어서 하나의 string으로 리턴한다
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 * @throws CmsLatteException 
	 */
	public String getString(int beginIndex, int endIndex) throws CmsLatteException {
		if(beginIndex>endIndex){
			//logger.error("getString error : beginIndex:" + beginIndex + " , " +"endIndex:" + endIndex);
			throw new CmsLatteException("fieldname parsing error : " + srcArray.toString());
		}
		StringBuilder sb = new StringBuilder();
		for(int i= beginIndex;i<=endIndex;i++){
			sb.append(srcArray[i]);
		}
		return sb.toString();
	}
}
