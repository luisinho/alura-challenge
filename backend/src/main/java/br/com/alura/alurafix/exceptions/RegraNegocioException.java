package br.com.alura.alurafix.exceptions;

public class RegraNegocioException  extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RegraNegocioException() {

	}

	public RegraNegocioException(String msg) {
		super(msg);
	}
}
