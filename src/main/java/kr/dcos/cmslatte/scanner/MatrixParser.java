package kr.dcos.cmslatte.scanner;

import java.util.ArrayList;
import java.util.List;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.functions.LatteCommonFunctions;
import kr.dcos.cmslatte.utils.LatteUtil;
/**
 * TableInit를 parsing한다
 * @author Administrator
 *
 */
public class MatrixParser  extends ScannerBase{
	private List<ArrayParser> list;
	private int lastRowIndex=-1,lastColIndex=-1;
	
	public MatrixParser(){
		list = new ArrayList<ArrayParser>();
	}
	
	public int rowSize(){
		return lastRowIndex+1;
	}
	public int colSize(){
		return lastColIndex+1;
	}
	public int lastRowIndex(){
		return lastRowIndex;
	}
	public int lastColIndex(){
		return lastColIndex;
	}
	public Object get(int rowIndex, int colIndex) throws CmsLatteException {
		if(rowIndex<0 || rowIndex > lastRowIndex ||
		   colIndex<0 || colIndex > lastColIndex){
			throw new CmsLatteException("index out of bound");
		}
		ArrayParser ap = list.get(rowIndex);
		if(ap==null){
			return null;
		}
		return ap.get(colIndex);
	}

	public void scan(String matrixInitDesc) throws CmsLatteException{
		init();
		if(LatteUtil.isNullOrEmpty(matrixInitDesc)){
			return ;
		}
		int optionRowSize= -1;
		int optionColSize= -1;
		
		srcArray = matrixInitDesc.trim().toCharArray();
		char ch = srcArray[index];
		if(ch != '{'){
			throw new CmsLatteException(matrixInitDesc +"  is not array init");
		}
		
		index++;
		index = skipWhitespace();
		ch = srcArray[index];
		if(ch == '$'){ //첫번째 문자가 $이면
			int posColon1 = index;
			index++;
			int posColon2 = indexOf('$');
			if(posColon2<0){
				throw new CmsLatteException(matrixInitDesc +"  is not matrix init description");
			}
			String s = getString(posColon1+1,posColon2-1);
			if(s.indexOf(',')<0){
				throw new CmsLatteException(matrixInitDesc +"  is not matrix init description");
			}
			String[] rowcol = s.split(",");
			if(rowcol.length == 2){
				if(LatteUtil.isValidInteger(rowcol[0].trim()) && LatteUtil.isValidInteger(rowcol[1].trim())){
					optionRowSize = LatteCommonFunctions.toInteger(rowcol[0].trim());
					optionColSize = LatteCommonFunctions.toInteger(rowcol[1].trim());
				}
			}
			index = posColon2+1;
		}
		//int rowNumber = 0;
		int leftBraceNumber = 1;
		while(index < srcArray.length){
			if(leftBraceNumber==0) break;
			
			index = skipWhitespace();
			ch = srcArray[index];
			if(ch==','){
				//rowNumber++;
			}else if(ch=='{'){
				fillBufferUntilPair('{','}',true);
				addToList();
				continue;
			}else if(ch == '}'){
				leftBraceNumber--;
			}else{
				buffer.append(ch);
			}
			index++;			
		}
		//최대 크기를 정한다.
		lastRowIndex = list.size()-1;
		if((lastRowIndex+1)<optionRowSize){
			lastRowIndex = optionRowSize - 1;
		}
		int n=-1;
		for (ArrayParser ap : list) {
			if(n<ap.lastIndex()){
				n =ap.lastIndex();
			}
		}
		lastColIndex = n;
		if(n < optionColSize){
			lastColIndex = optionColSize-1;
		}
	}
	public MatrixField getMatrixField(String name) throws CmsLatteException{
		MatrixField matrixField = new MatrixField(name);
		for(int r=0;r<=lastRowIndex;r++){
			for(int c=0;c<=lastColIndex;c++){
				 Object o= get(r,c);
				 matrixField.appendOrReplace(r,c, o);
			}
		}
		return matrixField;
	}
	private void addToList() throws CmsLatteException {
		ArrayParser ap = new ArrayParser();
		ap.scan(buffer.toString());
		if(ap.size()>0){
			list.add(ap);
		}
		bufferClear();
		
	}
	
	@Override 
	protected void init(){
		super.init();
		list.clear();
		lastRowIndex = -1;
		lastColIndex = -1;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int r=0;r<=lastRowIndex;r++){
			for(int c=0;c<=lastColIndex;c++){
				Object o;
				try {
					o = get(r,c);
					sb.append("\t" + r+","+ c + ":" + o.toString());
				} catch (CmsLatteException e) {
					;
				}
				
			}
		}
		return sb.toString();
	}

}
