package cz.via.slidecaster.exception;

public class UnknownException extends ApplicationException {

	private static final long serialVersionUID = 5266965970946868102L;

	public UnknownException(Throwable e) {
		super(e);
	}

	public UnknownException(String string) {
		super(string);
		// TODO Auto-generated constructor stub
	}
}
