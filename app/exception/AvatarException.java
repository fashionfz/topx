package exception;

public class AvatarException extends Exception {

	public AvatarException() {
		super();
	}

	public AvatarException(String msg) {
		super(msg);
	}

	public AvatarException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AvatarException(Throwable cause) {
		super(cause);
	}
	
}
