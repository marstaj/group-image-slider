package cz.via.slidecaster.exception;

public class ApplicationException extends Exception {

	private static final long serialVersionUID = 6072904519343217453L;

	public ApplicationException(Throwable e) {
		super(e);
	}

	public ApplicationException(String string) {
		super(string);
		// TODO Auto-generated constructor stub
	}

}
