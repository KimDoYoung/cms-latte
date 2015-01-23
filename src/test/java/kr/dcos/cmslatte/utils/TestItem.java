package kr.dcos.cmslatte.utils;

public class TestItem {
	private String title;
	private String code;
	private String result;
	
	public TestItem(String title,String code,String result){
		this.title=title;
		this.code=code;
		this.result = result;
	}
	public TestItem(){
		this(null,null,null);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n--------["+title+"]-----------\n");
		sb.append("code:"+code + "\n");
		sb.append("reuslt:["+result+"]");
		
		return sb.toString();
	}
}
