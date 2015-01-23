package kr.dcos.cmslatte.token;



import java.util.Date;

public class Token implements Cloneable{
	
	//private static Logger logger = LoggerFactory.getLogger(Token.class);
	
	private String name=null;
	private Object value ;
    private TokenType tokenType = TokenType.Unknown;
   
	private int icp = 0; // Incoming procedence 스택에 들어올 때의 우선순위
    private  int isp = 0; // In-Stack procedence 스택안에서의 우선순위

	//
	// constructor
	//
    public Token(String value){
    	//this(LatteUtil.createUUID(),TokenType.String,value,0,0);
    	this(null,TokenType.String,value,0,0);
    }
    public Token(Object value){
   		//this(LatteUtil.createUUID(),null,value,0,0);
   		this(null,null,value,0,0);
    }

	public Token(Object value,TokenType tokenType){
		//this(LatteUtil.createUUID(),tokenType,value,0,0);
		this(null,tokenType,value,0,0);
	}
	public Token(TokenType tokenType){
		//this(LatteUtil.createUUID(),tokenType,null,0,0);
		this(null,tokenType,null,0,0);
	}
	public Token(TokenType tokenType,Object value){
		//this(LatteUtil.createUUID(),tokenType,value,0,0);
		this(null,tokenType,value,0,0);
	}
	public Token(String name,TokenType tokenType,Object value){
		this(name,tokenType,value,0,0);
	}
    public Token(String name, TokenType tokenType,Object value,int icp,int isp){
    	this.name = name;
    	this.tokenType = tokenType;
    	this.value = value;
    	if(tokenType == null || tokenType == TokenType.Unknown){
    		this.tokenType = getPanJungTokenType(value);
    	}
    	
    	this.icp = icp;
    	this.isp = isp;
    }
    /**
     * value에 따라서 tokentype을 정한다
     * @param value
     * @return
     */
    private TokenType getPanJungTokenType(Object object){
    	//date->double->integer->boolean->string
    	
    	if(object instanceof Date ) {
    		return TokenType.Date;
    	}else if(object instanceof Double){
    		return TokenType.Double;
    	}else if(object instanceof Integer){
    		return TokenType.Integer;
    	}else if(object instanceof Boolean){
    		return TokenType.Boolean;
    	}else{ 
	    	return TokenType.String;
    	}
    }
	public String getName() {
		return name;
	}
	@SuppressWarnings("unchecked")
	public <R extends Object> R getValue(final Class<R> type){
		return (R)value;
	}
	public Object getValue(){
//		if(tokenType == TokenType.String){
//			return getString();
//		}
		return value;
	}
	public String getString(){
		return value.toString();
	}
	public Date getDate(){
		return (Date)value;
	}
	public Double getDouble(){
		if (tokenType == TokenType.Integer) {
			return ((Integer) value * 1.0);
		}
		return (Double) value;
	}
	public Integer getInteger(){
		return (Integer)value;
	}
	public Boolean getBoolean(){
		return (Boolean)value;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public int getIcp() {
		return icp;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Token(this.name, this.tokenType,this.value , this.icp, this.isp);
	}
	 
	public int getIsp() {
		return isp;
	}
	@Override
	public String toString(){
		String v = "";
		if(value != null){
			v = value.toString();
		}
		return String.format("Token->name:[%s], type:[%s], value:[%s], icp:[%d], isp:[%d]",this.name,this.tokenType,v,this.icp,this.isp);
	}
	public String getHashKey() {
		String key = "token"+this.toString();
		int i =  key.hashCode();
		return ((new Integer(i)).toString());
	}
}
