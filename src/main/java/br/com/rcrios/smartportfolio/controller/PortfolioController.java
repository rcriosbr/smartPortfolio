package br.com.rcrios.smartportfolio.controller;

import java.math.BigDecimal;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.InvalidObject;
import br.com.rcrios.smartportfolio.model.MutualFund;
import br.com.rcrios.smartportfolio.model.Portfolio;
import br.com.rcrios.smartportfolio.repository.PortfolioRepository;
import br.com.rcrios.smartportfolio.repository.PreconditionFailed;

/**
 * All responses are wrapped into a {@link ResponseEntity}.
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/portfolios")
public class PortfolioController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioController.class);

  @Autowired
  private PortfolioRepository repo;

  /**
   * If a portfolio is being created (through a POST request), the action will only impact its share quantity, not its
   * share value. If portfolio being saved has masters attached to it, they will also be updated.
   *
   * @see #updateMaster(Portfolio)
   *
   * @param portfolio
   *
   * @return If everything works, will return a ResponseEntity with {@link HttpStatus#OK} and a Portfolio in its body.
   *         Otherwise will return a {@link org.springframework.web.bind.annotation.ResponseStatus} with code
   *         {@link HttpStatus#UNPROCESSABLE_ENTITY} if the posted object isn't valid, or
   *         {@link HttpStatus#INTERNAL_SERVER_ERROR} if a DataAccessException occurs.
   */
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

      if (portfolio.getLastUpdated() == null) {
        portfolio.setLastUpdated(new Date());
      }

      saved = this.repo.save(portfolio);
      LOGGER.trace("Saved {}", saved);
    } catch (final DataAccessException e) {
      final String msg = "Failed to save portfolio. Full stacktrace was logged with id " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      throw new SmartPortfolioRuntimeException(msg, e);
    }

    if (saved == null) {
      throw new SmartPortfolioRuntimeException("Something went wrong saving " + portfolio);
    }

    this.updateMaster(portfolio);

    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }

  /**
   * Retrieves ALL portfolios from repository.
   *
   * @return A list of portfolios wrapped by a {@link ResponseEntity} with {@literal HttpStatus#OK}. If the list is empty,
   *         ResponseEntity will have a {@literal HttpStatus#NO_CONTENT}.
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
   * Retrieves a single portfolio.
   *
   * @param id
   *          Internal portfolio identification.
   *
   * @return A portfolio wrapped by a {@link ResponseEntity} with {@literal HttpStatus#OK}. If ID doesn't exist,
   *         ResponseEntity will have a {@literal HttpStatus#NOT_FOUND}.
   */
  @GetMapping("v1/{id}")
  public ResponseEntity<Portfolio> getById(@PathVariable("id") Long id) {
    LOGGER.debug("Getting portfolio with id '{}'", id);

    final Optional<Portfolio> result = this.repo.findById(id);

    if (result.isPresent()) {
      return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    return new ResponseEntity<>(new Portfolio(), HttpStatus.NOT_FOUND);
  }

  /**
   * Retrieves a single portfolio.
   *
   * @param name
   *          String that will be used to locate the Portfolio. Case INsensitive. Perfect match.
   *
   * @see PortfolioRepository#findFirstByNameIgnoreCase(String)
   *
   * @return A portfolio wrapped by a {@link ResponseEntity}. If not found, ResponseEntity will have a
   *         {@literal HttpStatus#NOT_FOUND}.
   */
  @GetMapping("v1")
  public ResponseEntity<Portfolio> getByName(@RequestParam("name") String name) {
    LOGGER.debug("Getting portfolio with name '{}'", name);

    final Optional<Portfolio> result = this.repo.findFirstByNameIgnoreCase(name);

    if (result.isPresent()) {
      return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    return new ResponseEntity<>(new Portfolio(), HttpStatus.NOT_FOUND);
  }

  /**
   * Retrieves the root portfolio. Root portfolio is the main portfolio, to which other portfolios are attached. It's the
   * only one that has a null master.
   *
   * @return A portfolio wrapped by a {@link ResponseEntity}. Otherwise will return a
   *         {@link org.springframework.web.bind.annotation.ResponseStatus} with code
   *         {@link HttpStatus#PRECONDITION_FAILED}
   */
  @GetMapping("v1/root")
  public ResponseEntity<Portfolio> getRootValue() {
    final Optional<Portfolio> result = this.repo.getRootPortfolio();

    if (result.isPresent()) {
      return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    throw new PreconditionFailed("SmartPortfolio isn't properly configured. It doesn't have a root portfolio.");
  }

  /**
   * Attach a mutual fund to a portfolio. When attaching, portfolio shares will be updated. If portfolio being modified
   * has masters attached to it, they will also be updated. Changes will only be applied to quantity. Share value will not
   * be changed.
   *
   * @see #updateMaster(Portfolio, MutualFund)
   *
   * @param portfolio
   *
   * @return If everything works, will return a ResponseEntity with {@link HttpStatus#OK} and a Portfolio in its body.
   *         Otherwise will return a {@link org.springframework.web.bind.annotation.ResponseStatus} with code
   *         {@link HttpStatus#INTERNAL_SERVER_ERROR} if the portfolio doesn't exist or if the system wasn't able to
   *         attach the mutual fund.
   */
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

  /**
   * Update a master portfolio from portfolio based on values from a mutual fund. Portfolio shares will be increased by
   * mutual fund value divided by portfolio share value. Will recursively update all masters.
   *
   * @param portfolio
   * @param mf
   */
  private void updateMaster(Portfolio portfolio, MutualFund mf) {
    Portfolio master = portfolio.getMaster();
    while (master != null) {
      LOGGER.debug("Updating master '{}'", master);

      final BigDecimal shares = mf.getValue().divide(master.getShareValue(), Utils.DEFAULT_MATHCONTEXT);
      master.setShares(master.getShares().add(shares, Utils.DEFAULT_MATHCONTEXT));
      master.setValue(master.getShares().multiply(master.getShareValue(), Utils.DEFAULT_MATHCONTEXT));
      master.setLastUpdated(portfolio.getLastUpdated());

      master = this.repo.save(master).getMaster();
      LOGGER.trace("Post master update: '{}'", master);
    }
  }

  /**
   * Update a master portfolio based on values from its children. Master shares will be increased by child portfolio value
   * divided by master share value. Will recursively update all masters.
   *
   * @param portfolio
   */
  private void updateMaster(Portfolio portfolio) {
    Portfolio master = portfolio.getMaster();

    LOGGER.debug("Portfolio '{}' has a master: {}", portfolio.getName(), portfolio.getMasterAsString());

    while (master != null) {
      LOGGER.debug("Updating master portfolio '{}'", master);

      final BigDecimal shares = portfolio.getValue().divide(master.getShareValue(), Utils.DEFAULT_MATHCONTEXT);
      master.setShares(master.getShares().add(shares, Utils.DEFAULT_MATHCONTEXT));
      master.setValue(master.getShares().multiply(master.getShareValue(), Utils.DEFAULT_MATHCONTEXT));
      master.setLastUpdated(portfolio.getLastUpdated());

      master = this.repo.save(master).getMaster();
      LOGGER.trace("Post master portfolio update: '{}'", master);
    }
  }
}
