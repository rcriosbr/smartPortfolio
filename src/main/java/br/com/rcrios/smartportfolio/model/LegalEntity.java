package br.com.rcrios.smartportfolio.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LegalEntity is any non-human entity, (firm, government agency, etc) that is recognized as having privileges and obligations, such as having the ability to
 * enter into contracts, to sue, and to be sued. It is the underlying business entity behind a mutual fund.
 */
@Entity
public class LegalEntity implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(LegalEntity.class);

  private static final long serialVersionUID = 1L;

  /**
   * Internal entity id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * LegalEntity name. Cannot be null and must be unique.
   */
  @Column(nullable = false, unique = true)
  private String name;

  /**
   * LegalEntity nickname.
   */
  private String nickname;

  /**
   * National tax payer id (in Brazil, CNPJ). An unique id that identifies the entity into government agencies.
   */
  @Column(nullable = false, unique = true)
  private String nationalTaxPayerId;

  /**
   * Verifies if the provided object is valid. If it is not, throws an SmartPortfolioRuntimeException. To be valid, a LegalEntity must be not null and also must
   * have a name and a national tax payer id.
   * 
   * @param toBeValidated
   *          Object to be validated
   */
  public static void validate(LegalEntity toBeValidated) {
    if (toBeValidated == null) {
      throw new InvalidObject("LegalEntity object cannot be null.");
    }

    if (toBeValidated.getName() == null || toBeValidated.getName().trim().isEmpty()) {
      throw new InvalidObject("LegalEntity name cannot be null or empty.");
    }

    if (toBeValidated.getNationalTaxPayerId() == null || toBeValidated.getNationalTaxPayerId().trim().isEmpty()) {
      throw new InvalidObject("LegalEntity national tax payer id cannot be null or empty.");
    }
  }

  /**
   * Helper method that verifies if this object is valid.
   * 
   * @return True if the object is valid. False otherwise. The underlying exception will be logged as a warning.
   */
  public boolean isValid() {
    try {
      LegalEntity.validate(this);
    } catch (InvalidObject e) {
      LOGGER.warn("Object is invalid. Current object state: " + this.toString(), e);
      return false;
    }
    return true;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getNationalTaxPayerId() {
    return nationalTaxPayerId;
  }

  public void setNationalTaxPayerId(String nationalTaxPayerId) {
    this.nationalTaxPayerId = nationalTaxPayerId;
  }

  @Override
  public String toString() {
    return String.format("LegalEntity [id=%s, name=%s, nickname=%s, nationalTaxPayerId=%s]", id, name, nickname, nationalTaxPayerId);
  }
}
