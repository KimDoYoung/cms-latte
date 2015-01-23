package kr.dcos.cmslatte.field;

import java.util.Date;

import kr.dcos.cmslatte.utils.LatteUtil;

public abstract class Field {
	protected String name;
	protected Object value;
	protected FieldType type;
	//
	//constructor
	//
	public Field(String name,Object value,FieldType type){
		this.name = name;
		this.value = value;
		this.type = type;
	}
	//
	//getter,setter
	//
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public String getValueString(){
		if(LatteUtil.isDate(value)){
			return LatteUtil.longSimpleDateFormat.format((Date)value);
		}else{
			return value.toString();
		}
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public FieldType getType() {
		return type;
	}
	public void setType(FieldType type) {
		this.type = type;
	}
	@Override
	public String toString(){
		String v ="null";
		if(value != null){
			v = value.toString();
		}
		return String.format("name:%s,value:%s,type:%s",this.name,v,this.type.toString());
	}
	/**
	 * name은 상관없이 내용이 같다면 같은 것이다
	 */
	@Override
	public boolean equals(Object obj) {
		if (this==obj)return true;
		if (obj == null) return false;
		if (!this.getClass().equals(obj.getClass())){
			return false;
		}
		ConstantField obj2 = (ConstantField) obj;
		if ( 	this.value == null  && 
				obj2.value == null  &&
				this.type == obj2.type){
			return true;
		}
		if ( this.value == obj2.value &&
			 this.value.getClass().equals(obj2.value.getClass())&&
			 this.type == obj2.type) {
			return true;
		} else{
			return false;
		}
	}
	@Override
	public int hashCode(){
		return (name+value+type.toString()).hashCode();
	}
}
