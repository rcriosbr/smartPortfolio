package br.com.rcrios.smartportfolio.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.model.InvalidObject;
import br.com.rcrios.smartportfolio.model.LegalEntity;
import br.com.rcrios.smartportfolio.repository.LegalEntityRepository;

/**
 * Exposes services related with a LegalEntity object.
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/legalentities/")
public class LegalEntityController {
  private static final Logger LOGGER = LoggerFactory.getLogger(LegalEntityController.class);

  @Autowired
  private LegalEntityRepository repo;

  /**
   * Persists a LegalEntity object into the repository.
   *
   * @param person
   *          Object to be persisted
   *
   * @return A ResponseEntity whose body is a LegalEntity object and HttpStatus.OK. If the save action fails, the
   *         ResponseEntity will have a HttpStatus that indicates the error and the body will be the error description.
   */
  @PostMapping("v1/")
  public ResponseEntity<LegalEntity> save(@RequestBody LegalEntity legalEntity) {
    LOGGER.debug("Saving {}", legalEntity);

    try {
      LegalEntity.validate(legalEntity);
    } catch (final SmartPortfolioRuntimeException e) {
      final String msg = "Posted object isn't valid. Full stacktrace was logged with id " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      throw new InvalidObject(msg, e);
    }

    LegalEntity saved = null;
    try {
      saved = this.repo.save(legalEntity);
    } catch (final DataAccessException e) {
      final String msg = "Failed to save legal entity. Full stacktrace was logged with id " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      throw new SmartPortfolioRuntimeException(msg, e);
    }

    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }

  /**
   * Retrieves a LegalEntoty by its national tax payer id
   *
   * @param nationalTaxPayerId
   *
   * @return A legal entity wrapped by a {@link ResponseEntity}. If not found, ResponseEntity will have a
   *         {@literal HttpStatus#NO_CONTENT}.
   */
  @GetMapping("v1/{ntpid}")
  public ResponseEntity<LegalEntity> getLegalEntity(@PathVariable("ntpid") String nationalTaxPayerId) {
    LOGGER.debug("Getting legal entoty with ntpId '{}'", nationalTaxPayerId);

    final Optional<LegalEntity> result = this.repo.findByNationalTaxPayerId(nationalTaxPayerId);

    if (result.isPresent()) {
      return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    return new ResponseEntity<>(new LegalEntity(), HttpStatus.NOT_FOUND);
  }

  /**
   * Retrieves ALL legal entities from repository.
   *
   * @see https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html
   *
   * @return A list of legal entities wrapped by a {@link ResponseEntity}. If the list is empty, ResponseEntity will have
   *         a {@literal HttpStatus#NO_CONTENT}.
   */
  @GetMapping("v1/")
  public ResponseEntity<List<LegalEntity>> getAll() {
    final List<LegalEntity> result = this.repo.findAll();
    if (result.isEmpty()) {
      return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity<>(result, HttpStatus.OK);
    }
  }
}
