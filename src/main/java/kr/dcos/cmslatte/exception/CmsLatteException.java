package kr.dcos.cmslatte.exception;

public class CmsLatteException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7589866879352837529L;
	private String message = null;
	 
    public CmsLatteException() {
        super();
    }
 
    public CmsLatteException(String message) {
        super(message);
        this.message = message;
    }
 
    public CmsLatteException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}
