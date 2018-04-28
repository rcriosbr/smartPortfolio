package br.com.rcrios.smartportfolio.repository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class PreconditionFailed extends SmartPortfolioRuntimeException {

  private static final long serialVersionUID = 1L;

  public PreconditionFailed() {
    super();
  }

  public PreconditionFailed(String message, Throwable cause) {
    super(message, cause);
  }

  public PreconditionFailed(String message) {
    super(message);
  }
}
