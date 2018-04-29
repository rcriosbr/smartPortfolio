package br.com.rcrios.smartportfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic system runtime exception. When reported through a response, will have a
 * {@link HttpStatus#INTERNAL_SERVER_ERROR}
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SmartPortfolioRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SmartPortfolioRuntimeException() {
    super();
  }

  public SmartPortfolioRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public SmartPortfolioRuntimeException(String message) {
    super(message);
  }

}
