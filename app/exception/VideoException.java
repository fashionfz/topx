package exception;

public class VideoException extends Exception {

	public VideoException() {
		super();
	}

	public VideoException(String msg) {
		super(msg);
	}

	public VideoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public VideoException(Throwable cause) {
		super(cause);
	}
	
}
