package br.com.rcrios.smartportfolio.repository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.controller.PortfolioController;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.MutualFundShare;

/**
 * Runtime exception that is used to report errors related to system preconditions that aren't satisfied. When reported
 * through a response, will have a {@link HttpStatus#PRECONDITION_FAILED}
 *
 * Examples of system preconditions:
 * <ul>
 * <li>SmartPortfolio must have only one root portfolio ({@link PortfolioController#getRootValue()}</li>
 * <li>A {@link MutualFundShare} must exist before a {@link Deal}
 * </ul>
 */
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
