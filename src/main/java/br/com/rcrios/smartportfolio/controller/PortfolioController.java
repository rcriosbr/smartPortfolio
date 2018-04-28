package br.com.rcrios.smartportfolio.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.InvalidObject;
import br.com.rcrios.smartportfolio.model.MutualFund;
import br.com.rcrios.smartportfolio.model.Portfolio;
import br.com.rcrios.smartportfolio.repository.PortfolioRepository;
import br.com.rcrios.smartportfolio.repository.PreconditionFailed;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/portfolios")
public class PortfolioController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioController.class);

  @Autowired
  private PortfolioRepository repo;

  @PostMapping("v1/")
  public ResponseEntity<Portfolio> save(@RequestBody Portfolio portfolio) {
    LOGGER.debug("Saving {}", portfolio);

    try {
      Portfolio.validate(portfolio);
    } catch (final SmartPortfolioRuntimeException e) {
      final String msg = "Posted object isn't valid. Full stacktrace was logged with id " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      throw new InvalidObject(msg, e);
    }

    Portfolio saved = null;
    try {
      saved = this.repo.save(portfolio);
    } catch (final DataAccessException e) {
      final String msg = "Failed to save portfolio. Full stacktrace was logged with id " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      throw new SmartPortfolioRuntimeException(msg, e);
    }

    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }

  /**
   * Retrieves ALL portfolios from repository.
   *
   * @see https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html
   *
   * @return A list of portfolios wrapped by a {@link ResponseEntity}. If the list is empty, ResponseEntity will have a
   *         {@literal HttpStatus#NO_CONTENT}.
   */
  @GetMapping("v1/")
  public ResponseEntity<List<Portfolio>> getAll() {
    final List<Portfolio> result = this.repo.findAll();
    if (result.isEmpty()) {
      return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity<>(result, HttpStatus.OK);
    }
  }

  /**
   * Search and return a Portfolio by its name.
   *
   * @param name
   *          String that will be used to locate the Portfolio. case INsensitive.
   *
   * @see PortfolioRepository#findFirstByNameIgnoreCase(String)
   * @see https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html
   *
   * @return A Portfolio wrapped by a {@link ResponseEntity}. If not found, ResponseEntity will have a
   *         {@literal HttpStatus#NOT_FOUND}.
   */
  @GetMapping("v1/{name}")
  public ResponseEntity<Portfolio> getByName(@PathVariable("name") String name) {
    LOGGER.debug("Getting portfolio with name '{}'", name);

    final Optional<Portfolio> result = this.repo.findFirstByNameIgnoreCase(name);

    if (result.isPresent()) {
      return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    return new ResponseEntity<>(new Portfolio(), HttpStatus.NOT_FOUND);
  }

  @GetMapping("v1/root")
  public ResponseEntity<Portfolio> getRootValue() {
    final Optional<Portfolio> result = this.repo.getRootPortfolio();

    if (result.isPresent()) {
      return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    throw new PreconditionFailed("SmartPortfolio isn't properly configured. It doesn't have a root portfolio.");
  }

  @PatchMapping("v1/{id}")
  public ResponseEntity<Portfolio> attach(@PathVariable Long id, @RequestBody MutualFund mf) {
    final Optional<Portfolio> p = this.repo.findById(id);
    if (!p.isPresent()) {
      throw new SmartPortfolioRuntimeException("Impossible to attach mutual fund. Portfolio '" + Objects.toString(id) + "' not found.");
    }

    final Portfolio portfolio = p.get();

    LOGGER.debug("Attaching mutual fund '{}' to portfolio '{}'", mf.getFund().getNickname(), portfolio.getName());

    if (portfolio.add(mf)) {
      if (mf.getValue().compareTo(BigDecimal.ZERO) != 0) {
        final BigDecimal shares = mf.getValue().divide(portfolio.getShareValue(), Utils.DEFAULT_MATHCONTEXT);
        portfolio.setShares(portfolio.getShares().add(shares, Utils.DEFAULT_MATHCONTEXT));
        portfolio.setValue(portfolio.getShares().multiply(portfolio.getShareValue(), Utils.DEFAULT_MATHCONTEXT));
      }
    } else {
      throw new SmartPortfolioRuntimeException("It wasn't possible to add mutual fund to the portfolio.");
    }

    final Portfolio saved = this.repo.save(portfolio);
    LOGGER.trace("Mutual fund was attached to portfolio successluly. {}", saved);

    this.updateMaster(saved, mf);

    return new ResponseEntity<>(saved, HttpStatus.OK);
  }

  private void updateMaster(Portfolio portfolio, MutualFund mf) {
    Portfolio master = portfolio.getMaster();
    while (master != null) {
      LOGGER.debug("Updating master '{}'", master);

      final BigDecimal shares = mf.getValue().divide(master.getShareValue(), Utils.DEFAULT_MATHCONTEXT);
      portfolio.setShares(master.getShares().add(shares, Utils.DEFAULT_MATHCONTEXT));
      portfolio.setValue(master.getShares().multiply(master.getShareValue(), Utils.DEFAULT_MATHCONTEXT));

      master = this.repo.save(master).getMaster();
      LOGGER.trace("Post save: '{}'", master);
    }
  }
}
