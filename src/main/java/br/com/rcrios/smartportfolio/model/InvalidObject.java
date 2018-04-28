package br.com.rcrios.smartportfolio.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidObject extends SmartPortfolioRuntimeException {

  private static final long serialVersionUID = 1L;

  public InvalidObject() {
    super();
  }

  public InvalidObject(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidObject(String message) {
    super(message);
  }
}
