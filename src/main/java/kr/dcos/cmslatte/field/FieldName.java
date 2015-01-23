package kr.dcos.cmslatte.field;

/**
 * Field 는 Variable이라고 생각할 수 있는데, 
 * constant Field는 a, b, c와 같이 단어로
 * array field는 a[1]과 같은 형태일 수 있다.
 * table field는 t[1,2]와 같은 형태 일 수 있으며
 * 3가지 field 모두 c.length(), a[1].length(), t[1,2].substring(2,3)과 같은 형태를 갖는다.
 *  
 * @author Administrator
 *
 */
public class FieldName {
	

	private char prefix = 0x00;// 앞에 prefix
    private String name =null;//= string.Empty;
    private String subFuncDesc=null; //table1.header : header, cols, rows 만 허용
    private String rowSusikString=null; //
    private String colSusikString=null;
    private  int rowIndex = -1;// = 0; //Matrix row_idx
    private  int colIndex = -1;// = 0; //Matrix col_idx
    private FieldType fieldType = FieldType.Unknown;


	/**
     * 생성자
     */
    public FieldName(String name){
   	 	this(name,FieldType.Unknown);
    }
    public FieldName(String name,FieldType fieldType){
    	this.name = name;
    	this.fieldType = fieldType;
    }
    public FieldName(){
    	this(null);
    }
    

    //
    //getter,setter
    //
	public FieldType getFieldType() {
		return fieldType;
	}
	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}
    public char getPrefix() {
		return prefix;
	}
   
	public void setPrefix(char prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubFuncDesc() {
		return subFuncDesc;
	}

	public void setSubFuncDesc(String subFuncDesc) {
		this.subFuncDesc = subFuncDesc.trim();
	}

	public String getRowSusikString() {
		return rowSusikString;
	}

	public void setRowSusikString(String rowSusikString) {
		this.rowSusikString = rowSusikString.trim();
	}

	public String getColSusikString() {
		return colSusikString;
	}

	public void setColSusikString(String colSusikString) {
		this.colSusikString = colSusikString.trim();
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getColIndex() {
		return colIndex;
	}

	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}	
	@Override
	public String toString(){

		StringBuilder sb = new StringBuilder();
		sb.append("FieldName:\n");
		sb.append("\t prefix:"+prefix+"\n");
		sb.append("\t name:"+name+"\n");
		sb.append("\t rowSusikString:"+rowSusikString+"\n");
		sb.append("\t colSusikString:"+colSusikString+"\n");
		sb.append("\t rowIndex:"+rowIndex+"\n");
		sb.append("\t colIndex:"+colIndex+"\n");
		sb.append("\t subFuncDesc:"+subFuncDesc+"\n");

		return sb.toString();
	}
}
