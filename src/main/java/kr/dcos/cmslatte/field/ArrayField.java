package kr.dcos.cmslatte.field;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

/**
 * Array
 * 1. a[1] = 10;
 * 2. a[100] = 10; -> lastIndex = 101 
 * @author Administrator
 *
 */
public class ArrayField extends Field {
	private Map<Integer,Object> map  = null;
    private int lastIndex = -1;
    //
    //constrcutor
    //
	public ArrayField(String name) {
		this(name,0);
	}
	public ArrayField(String name,int maxSize){
		super(name, null,FieldType.Array);
		//map = new HashMap<Integer, Object>();
		map = new LinkedHashMap<Integer,Object>();
		lastIndex = maxSize - 1;
	}
	//
    //private
    //

    private boolean isValidRange(int index){
    	if(index< 0 || index > lastIndex){
    		return false;
    	}
    	return true;
    }
    //
    //public
    //
	public void setLastIndex(int lastColumnIndex) {
		lastIndex = lastColumnIndex;
	}
    public int getLastIndex() {
		return lastIndex;
	}
    public boolean isEmpty(){
    	return (lastIndex < 0);
    }
    public int size(){
    	return lastIndex + 1;
    }
    public void clear(){
    	lastIndex = -1;
    	map.clear();
    }

    public Object getValue(int index) throws CmsLatteException{
    	if(index< 0 || index > lastIndex){
    		throw new CmsLatteException("array field " + name + " index out of range");
    	}
    	
    	Integer key = new Integer(index);
    	if(map.containsKey(key)){
    		return map.get(key);
    	}
    	return null;
    }    
    public String getValueString(int index) throws CmsLatteException{
    	super.value = getValue(index);
    	return super.getValueString();
    }
    public void appendOrReplace(int index,Object value) throws CmsLatteException{
    	if(isValidRange(index)){ //already
    		map.put(new Integer(index), value);
    	}else{
    		if(index < 0) { 
    			throw new CmsLatteException("array field " + name + " index out of range");
    		}
    		map.put(new Integer(index),value);
       		lastIndex = index;
    	}
    }
    private void add(Object value){
		lastIndex++;
		map.put(new Integer(lastIndex), value);
    	
    }
	public void append(Object value) throws CmsLatteException {
		TokenType type = LatteUtil.getTokenTypeWithObject(value);
		if(type == TokenType.MatrixField){
			throw new CmsLatteException("table can not append to array");
		}else if(type == TokenType.ArrayField){
			ArrayField af = (ArrayField)value;
			for(int i=0;i<af.size();i++){
				add(af.getValue(i));
			}
		}else if(value instanceof Object[]){
			append((Object[])value);
		}else if(value instanceof List){
			for (Object o : (List)value) {
				add(o);
			}
		}else if(LatteUtil.isPrimitiveType(value)){
			add(value);
		}else{
			throw new CmsLatteException(value + " is not appendable type to ArrayField ");
		}
		
	}
	public void append(Object[] valueArray) throws CmsLatteException{
		for (Object object : valueArray) {
			append(object);
		}
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("ArrayField:"+name);
		sb.append("\n");
		for(int i=0;i<map.size();i++){
			Object value = map.get(i);
			
			String v = (value == null) ? "" : value.toString();
			sb.append(i+":"+v+",");
		}
		String retval = sb.toString();
		if( retval.endsWith(",")){
			return retval.substring(0,retval.length()-1);
		}else{
			return retval;
		}
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
		ArrayField obj2 = (ArrayField) obj;
		if(this.map.size() != obj2.map.size()){
			return false;
		}
		//ArrayField obj2 = (ArrayField) obj;
		Set<Integer> keySet = map.keySet();
		Iterator<Integer> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			Object value = map.get(key);
			if(!obj2.map.containsKey(key)){ 
				return false;
			}
			if(!obj2.map.get(key).getClass().equals(value.getClass())){
				return false;
			}
			if(!obj2.map.get(key).toString().equals(value.toString())){
				return false;
			}
		}
		return true;
	}
	/**
	 * 배열을 seperator로 묶어서 1개의 문자열로 만든다.
	 * {1,2,3} -> sperator *라면  1*2*3 으로 만든다
	 * @param string
	 * @return
	 * @throws CmsLatteException 
	 */
	public String joinWith(String seperator) throws CmsLatteException {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<size();i++){
			if(getValue(i)==null){
				sb.append("");
			}else{
				sb.append(getValue(i).toString());
			}
			if(i<(lastIndex)){ sb.append(seperator);}
		}
		return sb.toString();
	}
	/**
	 * startIndex에서부터 Object를 찾아서 맞으면 그 index를 리턴한다
	 * @param key
	 * @param startIndex
	 * @return
	 * @throws CmsLatteException 
	 */
	public Integer indexOf(Object key, int startIndex) throws CmsLatteException {
		//TokenType keyTokenType = LatteUtil.getTokenTypeWithObject(key);
		for(int i=0;i<size();i++){
			Object o = getValue(i);
			if (o.equals(key)){
				return i;
			}
		}
		return -1;
	}
	/**
	 * index에 object를 끼워 넣는다.
	 * 기존 것은 뒤로 밀린다.
	 * lastIndex보다 크면 넣고 lastIndex를 index로 한다.
	 * 
	 * @param index
	 * @param object
	 * @throws CmsLatteException 
	 */
	public void insert(int index, Object object) throws CmsLatteException {
		if(index<0){
			throw new CmsLatteException("index can not under zero");
		}
		//index가 현재의 lastIndex보다 크다
		if(index>lastIndex){
			appendOrReplace(index, object);
			lastIndex = index;
			return;
		}
		//하나씩 뒤로 밀기
		int i = lastIndex;
		while(i>=index){
			if(map.containsKey(i)){
				appendOrReplace(i+1, map.get(i));
			}
			i--;
		}
		appendOrReplace(index, object);
	}
	/**
	 * index에 해당하는 요소를 제거한다
	 * index가 범위에 없다면 그냥 리턴한다
	 * {1,2,3} => {1,3};
	 * @param index
	 * @throws CmsLatteException 
	 */
	public void remove(int index) throws CmsLatteException {
		if(isValidRange(index)==false)return;
		map.remove(index);
		for(int i=index;i<size()-1;i++){
			Object o = getValue(i+1);
			appendOrReplace(i, o);
		}
		map.remove(lastIndex);
		lastIndex = lastIndex-1;
	}
	/**
	 * beginIndex에서 endIndex까지의 요소들로 Array를 만들어서 리턴한다
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 * @throws CmsLatteException 
	 */
	public ArrayField sub(int beginIndex, int endIndex) throws CmsLatteException {
		if (beginIndex > endIndex){
			throw new CmsLatteException("array sub beginIndex cannot bigger than endIndex");
		}
		ArrayField af = new ArrayField(LatteUtil.getUUID());
		Iterator<Integer> keyIt = map.keySet().iterator();
		while(keyIt.hasNext()){
			int index = keyIt.next();
			if(beginIndex<=index && index<=endIndex){
				af.append(getValue(index));
			}
		}
		return af;
	}

}
