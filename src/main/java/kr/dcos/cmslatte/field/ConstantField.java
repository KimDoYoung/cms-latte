package kr.dcos.cmslatte.field;

public class ConstantField extends Field{
	
	public ConstantField(String name,Object value){
		super(name,value,FieldType.Constant);
	}
}
