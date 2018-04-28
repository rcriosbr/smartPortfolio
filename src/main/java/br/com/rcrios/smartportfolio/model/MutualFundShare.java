package br.com.rcrios.smartportfolio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This entity holds mutual fund share values.
 * 
 * @see {@link https://en.wikipedia.org/wiki/Net_asset_value}
 *
 */
@Entity
public class MutualFundShare implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(MutualFundShare.class);

  private static final long serialVersionUID = 1L;

  /**
   * Internal identity code for a given share value
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * A share value must be attached to a mutual fund, which in turn is represented by a LegalEntity
   */
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private LegalEntity mutualFund;

  /**
   * The mutual fund share value
   */
  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal value;

  /**
   * The date in which the value was set
   * 
   * @see TemporalType#DATE
   */
  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  private Date date;

  /**
   * Validates if the provided MutuaFundShare object is valid.
   * 
   * To be considered valid it must be not null, have an attached fund (LegalEntity) and its date and value defined. If the object is invalid, the method will throw an
   * {@linkplain InvalidObject} exception.
   * 
   * @param toBeValidated
   *          Object to be validated
   */
  public static void validate(MutualFundShare toBeValidated) {
    if (toBeValidated == null) {
      throw new InvalidObject("MutuaFundShare cannot be null.");
    }

    if (toBeValidated.getFund() == null || !toBeValidated.getFund().isValid()) {
      throw new InvalidObject("MutualFundShare must have a valid fund.");
    }

    if (toBeValidated.getDate() == null) {
      throw new InvalidObject("MutuaFundShare date cannot be null.");
    }

    if (toBeValidated.getValue() == null) {
      throw new InvalidObject("MutuaFundShare value cannot be null.");
    }
  }

  /**
   * Helper method that verifies if this object is valid.
   * 
   * @return True if the object is valid. False otherwise. The underlying exception will be logged as a warning.
   */
  public boolean isValid() {
    try {
      MutualFundShare.validate(this);
    } catch (InvalidObject e) {
      LOGGER.warn("Object is invalid.", e);
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

  public LegalEntity getFund() {
    return mutualFund;
  }

  public void setFund(LegalEntity fund) {
    this.mutualFund = fund;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public Date getDate() {
    if (this.date != null) {
      return new Date(date.getTime());
    }
    return null;
  }

  public void setDate(Date date) {
    if (date != null) {
      this.date = new Date(date.getTime());
    }
  }

  @Override
  public String toString() {
    return String.format("MutualFundShare [id=%s, value=%s, date=%s, mutualfund.id=%s, mutualfund.name=%s]", id, value, date, mutualFund != null ? mutualFund.getId() : null,
        mutualFund != null ? mutualFund.getName() : null);
  }
}
