package exception;

public class SearchInputException extends Exception {
	

	private static final long serialVersionUID = -7952861081958428877L;

	public SearchInputException() {
		super();
	}

	public SearchInputException(String msg) {
		super(msg);
	}

	public SearchInputException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SearchInputException(Throwable cause) {
		super(cause);
	}
	
}
