package kr.dcos.cmslatte.field;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

public class MatrixField extends Field {
	
	private static Logger logger = LoggerFactory.getLogger(MatrixField.class);
	

	//private Map<String, Object> map = null;
	private Map<Integer, ArrayField> map = null;
	private int lastRowIndex, lastColumnIndex;

	//
	// constructor
	//
	public MatrixField(String name, int rowSize, int columnSize) {
		super(name, "", FieldType.Matrix);
		lastRowIndex = rowSize - 1;
		lastColumnIndex = columnSize - 1;
		map = new HashMap<Integer, ArrayField>();
	}
	public MatrixField(String name) {
		this(name,0,0);
	}
//	private String getKey(int row,int col){
//		return String.format("%d,%d",row,col);
//	}
	private boolean isValidRange(int row, int col) {
		if (0 <= row && row <= lastRowIndex && 0 <= col
				&& col <= lastColumnIndex) {
			return true;
		}
		return false;
	}
    public boolean isEmpty(){
    	return (lastRowIndex < 0 && lastColumnIndex < 0);
    }

	public int getLastRowIndex() {
		return lastRowIndex;
	}
	public int getLastColumnIndex() {
		return lastColumnIndex;
	}
	public int getRowCount(){
		return lastRowIndex + 1;
	}
	public int getColumnCount(){
		return lastColumnIndex + 1;
	}
    public int size(){
    	return (getRowCount()*getColumnCount());
    }
	public int getLastIndex() {
		return size()-1;
		
	}
    public void clear(){
    	lastRowIndex = -1;
    	lastColumnIndex = -1;
    	map.clear();
    }
    private ArrayField getRowArray(int row){
    	ArrayField af = map.get(row);
    	if(af==null){
    		af= new ArrayField(Integer.toString(row),getColumnCount());
    		map.put(row, af);
    		return af;
    	}else{
    		return af;
    	}
    }
    /**
     * table에 추가하거나 replace한다
     * 
     * @param row
     * @param col
     * @param value DISBD여야한다 Array,Matrix일수 없다
     * @throws CmsLatteException
     */
	public void appendOrReplace(int row,int col,Object value) throws CmsLatteException  {
		if(row < 0 || col < 0 ) {
			throw new CmsLatteException("matrix field " + name + " out of index range");
		}
		if(LatteUtil.isDISBD(value)==false){
			throw new CmsLatteException("matrix field not allow array or matrix data");
		}
		ArrayField af = getRowArray(row);
		af.appendOrReplace(col, value);
		//map.put(row, af);
		if(row>lastRowIndex){
			lastRowIndex = row;
		}
		if(col>lastColumnIndex){ //array의 lastIndex를 늘어난것으로 맞춘다
			lastColumnIndex = col;
			setArrayLastIndexWithLastColumnIndex();
		}

	}
	public void append(Object object) throws CmsLatteException {
		TokenType t = LatteUtil.getTokenTypeWithObject(object);
		
		if(t==TokenType.MatrixField){
			int baseRow = lastRowIndex+1;
			MatrixField m = (MatrixField)object;
			for(int r=0;r<m.getRowCount();r++){
				for(int c=0;c<m.getColumnCount();c++){
					appendOrReplace(baseRow+r, c, m.getValue(r,c));
				}
			}
		}else if(t == TokenType.ArrayField){
			int newRowIndex = lastRowIndex+1;
			ArrayField a = (ArrayField)object;
			for(int i=0;i<a.size();i++){
				appendOrReplace(newRowIndex, i, a.getValue(i));
			}
		}else{
			int newRowIndex = lastRowIndex+1;
			int newColIndex = 0;
			appendOrReplace(newRowIndex, newColIndex, object);
		}
		
	}
	
	/**
	 * index는 row와 col의 결합이다.
	 * 1번 row 의 첫번째 인자 = rowIndex * columnSize + columnIndex
	 * @param index
	 * @return
	 * @throws CmsLatteException 
	 */
	public Object getValue(int index) throws CmsLatteException{
		if(index >=0 && index< size()){
			return getValue(index/getColumnCount(), index % getColumnCount());
		}else{
			throw new CmsLatteException("matrix field " + name + " out of index range");
		}
	}
	
	public Object getValue(int row, int col) throws CmsLatteException {
		if(isValidRange(row, col)){
			if(map.containsKey(row)){
				return map.get(row).getValue(col);
			}else{
				return null;
			}
		}else{
			throw new CmsLatteException("matrix field" + name + " out of index range");
		}
	}
    public String getValueString(int row, int col) throws CmsLatteException{
    	super.value = getValue(row,col);
    	return super.getValueString();
    }
	/**
	 * object를 찾아서 그 index를 리턴한다
	 * @param object
	 * @return
	 * @throws CmsLatteException 
	 */
	public int indexOf(Object object) throws CmsLatteException {
		for(int i=0;i<size();i++){
			if(getValue(i).equals(object)){
				return i;
			}
		}
		return -1;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("MatrixField:"+name);
		sb.append("\n");
		try{
		for(int row = 0;row<= lastRowIndex;row++){
			for(int col=0;col<=lastColumnIndex;col++){
				Object o = getValue(row,col);
				String valueString = (o==null) ? "null" : o.toString();
				sb.append(valueString);
				sb.append(" ");
			}
			sb.append("\n");
		}
		}catch(CmsLatteException e){
			logger.error("",e);
		}
		return sb.toString();
	}
	@Override
	public int hashCode(){
		return (this.toString()).hashCode();
	}
	@Override
	public boolean equals(Object obj){
		if(this==obj)return true;
		if(obj == null){
			return false;
		}
		if (!this.getClass().equals(obj.getClass())){
			return false;
		}
		MatrixField obj2 = (MatrixField) obj;
		if(this.lastRowIndex != obj2.lastRowIndex ||
		   this.lastColumnIndex != obj2.lastColumnIndex ){
			return false;
		}
		Set<Integer> keySet = map.keySet();
		Iterator<Integer> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			ArrayField af = map.get(key);
			if(!obj2.map.containsKey(key)){ 
				return false;
			}
			if(!obj2.map.get(key).equals(af)){
				return false;
			}
		}
		return true;
	}
	public String joinWith(String rowSeperator, String colSeperator) throws CmsLatteException {
		StringBuilder sb = new StringBuilder();
		for(int r = 0; r < getRowCount() ; r++){
			for(int c = 0; c < getColumnCount() ; c++){
				Object o = getValue(r, c);
				String valueString = "";
				if (o != null){
					valueString = getValue(r, c).toString();
				}
				sb.append(valueString);
				if(c<lastColumnIndex){sb.append(colSeperator);}
			}
			if(r<lastRowIndex){sb.append(rowSeperator);}
		}
		return sb.toString();
	}
	/**
	 * index에 해당하는 row를 배열로 리턴, index범위가 벗어나면 {}리턴
	 * @param index
	 * @return
	 * @throws CmsLatteException 
	 */
	public ArrayField getRow(int index) throws CmsLatteException {
		ArrayField af = new ArrayField(LatteUtil.getUUID());
		if(index<0 || index > lastRowIndex){
			return af;
		}
		int r = index;
		//column을 쭉 돌면서
		for(int c=0;c<=lastColumnIndex;c++){
			Object o = getValue(r, c);
			if(o!=null){
				af.appendOrReplace(c, o);
			}
		}
		return af;
	}
	public ArrayField getColumn(int index) throws CmsLatteException {
		ArrayField af = new ArrayField(LatteUtil.getUUID());
		if(index<0 || index > lastColumnIndex){
			return af;
		}
		int c = index;
		//column을 쭉 돌면서
		for(int r=0;r<=lastRowIndex;r++){
			Object o = getValue(r, c);
			if(o!=null){
				af.appendOrReplace(r, o);
			}
		}
		return af;
	}
	public void insertRow(int index, ArrayField af) throws CmsLatteException {
		if(index<0 ){
			return ;
		}
		if(index>lastRowIndex){ //크다->그냥넣는다
			for(int i=0;i<=af.getLastIndex();i++){
				Object o = af.getValue(i);
				if(o==null)continue;
				appendOrReplace(index, i, af.getValue(i));
			}
			if(lastRowIndex != index) lastRowIndex = index;
		}else{
			//한줄씩 밑으로
			for(int row = lastRowIndex+1;row>index;row--){
				if(map.containsKey(row-1)){
					map.put(row, map.get(row-1));
				}
			}
			lastRowIndex++;
			//array를 넣기
			for(int i=0;i<=af.getLastIndex();i++){
				Object o = af.getValue(i);
				if(o==null)continue;
				appendOrReplace(index, i, af.getValue(i));
			}

		}
	}
	/**
	 * index에 af로 column을 만들어서 insert한다
	 * @param colIndex
	 * @param af
	 * @throws CmsLatteException 
	 */
	public void insertColumn(int colIndex, ArrayField af) throws CmsLatteException {
		
		if(colIndex>lastColumnIndex){
			lastColumnIndex = colIndex;
			setArrayLastIndexWithLastColumnIndex();
			for(int i=0;i<af.size();i++){
				Object o = af.getValue(i);
				if(o!=null){
					appendOrReplace(i, colIndex, af.getValue(i));
				}
			}
		}else{
			lastColumnIndex++;
			setArrayLastIndexWithLastColumnIndex();
			for(int row=0;row<af.size();row++){
				Object o = af.getValue(row);
				if(o!=null){
					getRowArray(row).insert(colIndex, o);
				}
			}
		}
	}
	private void setArrayLastIndexWithLastColumnIndex() {
		for (Entry<Integer, ArrayField> entry : map.entrySet()) {
			entry.getValue().setLastIndex(lastColumnIndex);
		}
	}
	/**
	 * index에 해당하는 row를 삭제한다
	 * @param index
	 */
	public void removeRow(int row) {
		if(row<0 || row>lastRowIndex) return;
		
		if(map.containsKey(row)){
			map.remove(row);
			for(int i=row+1;i<=lastRowIndex;i++){
				if(map.containsKey(i)){
					map.put(i-1,getRowArray(i));
				}
			}
			lastRowIndex--;
		}
	}
	/**
	 * colIndex에 해당하는 col을 삭제한다 
	 * @param colIndex
	 * @throws CmsLatteException 
	 */
	public void removeCol(Integer colIndex) throws CmsLatteException {
		if(colIndex<0 || colIndex > lastColumnIndex) return;
		
		for (Entry<Integer, ArrayField> entry : map.entrySet()) {
			entry.getValue().remove(colIndex);
		}
		lastColumnIndex--;
		setArrayLastIndexWithLastColumnIndex();
		
	}
	private void swap(int target,int source){
		ArrayField tmp = map.get(target);
		map.put(target, map.get(source));
		map.put(source,tmp);
	}
	public void sort(Integer colIndex,String dir)throws CmsLatteException{
		if(colIndex<0 || colIndex > lastColumnIndex) return;
		int i, j;
		ArrayField key;
		int n = getRowCount();
		for (i = 1; i < n; i++) {
			key = getRow(i); // 두 번째 값부터 선택
			for (j = i - 1; j >= 0 && (compareObject(getValue(j,colIndex),key.getValue(colIndex),dir)); j--) { // 선택된 값(key)보다 작은값을 찾는다.
				swap(j+1,j);; // list[j + 1] = list[j]작은 값을 찾은 경우 그 값뒤의 모든 값을 우측으로 이동
			}
			map.put(j + 1, key); // 해당되는 곳에 값을 삽입한다.
		}
	}
	private boolean compareObject(Object o1, Object o2,String mode) {
		if(o1 instanceof Integer && o2 instanceof Integer){
			int i1 = Integer.parseInt(o1.toString());
			int i2 = Integer.parseInt(o2.toString());
			return compareNumber(i1*1.0,i2*1.0,mode);
		}
		String s1,s2;
		if(o1 == null || o2 == null){
			s1 = LatteUtil.ifEmpty(o1, "").toString();
			s2 = LatteUtil.ifEmpty(o2, "").toString();
			return compareString(s1,s2,mode); 
		}else if(LatteUtil.isNumberString(o1.toString()) && LatteUtil.isNumberString(o2.toString())){
			double d1 = Double.parseDouble(o1.toString());
			double d2 = Double.parseDouble(o2.toString());
			return compareNumber(d1,d2,mode);
		}
		s1 = o1.toString();
		s2 = o2.toString();
		return compareString(s1, s2, mode);
	}
	private boolean compareNumber(double i1, double i2, String mode) {
		if(mode.equals("ASC")){
			if(i1<i2)return true;
			return false;
		}else{
			if(i1>i2)return true;
			return false;
			
		}
	}
	private boolean compareString(String s1, String s2, String mode) {
		if(mode.equals("ASC")){
			if(s1.compareTo(s2)<0) return true;
		}
		if (s1.compareTo(s2) > 0){
			return true;
		}
		return false;
	}

}
