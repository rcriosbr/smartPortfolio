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
 * Collective investment vehicles may be formed under company law, by legal trust or by statute. The nature of the vehicle and its limitations are often linked
 * to its constitutional nature and the associated tax rules for the type of structure within a given jurisdiction. Is a way of investing money alongside other
 * investors in order to benefit from the inherent advantages of working as part of a group.
 * 
 * @see {@link https://en.wikipedia.org/wiki/Mutual_fund}
 *
 */
@Entity
public class MutualFund implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(MutualFund.class);

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private LegalEntity fund;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private LegalEntity manager;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private LegalEntity trustee;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal shares;

  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  private Date creationDate;

  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  private Date lastUpdated;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal value;

  public static void validate(MutualFund toBeValidated) {
    if (toBeValidated == null) {
      throw new InvalidObject("MutualFund cannot be null.");
    }

    if (toBeValidated.getFund() == null || !toBeValidated.getFund().isValid()) {
      throw new InvalidObject("MutualFund must have a valid fund.");
    }

    if (toBeValidated.getManager() == null || !toBeValidated.getManager().isValid()) {
      throw new InvalidObject("MutualFund must have a valid manager.");
    }

    if (toBeValidated.getTrustee() == null || !toBeValidated.getTrustee().isValid()) {
      throw new InvalidObject("MutualFund must have a valid trustee.");
    }

    if (toBeValidated.getShares() == null) {
      throw new InvalidObject("MutualFund shares cannot be null.");
    }

    if (toBeValidated.getValue() == null) {
      throw new InvalidObject("MutualFund value cannot be null.");
    }

    if (toBeValidated.creationDate == null) {
      throw new InvalidObject("MutualFund creation date cannot be null.");
    }

    if (toBeValidated.getLastUpdated() == null) {
      throw new InvalidObject("MutualFund last updated date cannot be null.");
    }
  }

  /**
   * Helper method that verifies if this object is valid.
   * 
   * @return True if the object is valid. False otherwise. The underlying exception will be logged as a warning.
   */
  public boolean isValid() {
    try {
      MutualFund.validate(this);
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
    return fund;
  }

  public void setFund(LegalEntity fund) {
    this.fund = fund;
  }

  public LegalEntity getManager() {
    return manager;
  }

  public void setManager(LegalEntity manager) {
    this.manager = manager;
  }

  public LegalEntity getTrustee() {
    return trustee;
  }

  public void setTrustee(LegalEntity trustee) {
    this.trustee = trustee;
  }

  public BigDecimal getShares() {
    return shares;
  }

  public void setShares(BigDecimal shares) {
    this.shares = shares;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public Date getLastUpdated() {
    if (lastUpdated != null) {
      return new Date(lastUpdated.getTime());
    }
    return null;
  }

  public void setLastUpdated(Date lastUpdated) {
    if (lastUpdated != null) {
      this.lastUpdated = new Date(lastUpdated.getTime());
    } else {
      this.lastUpdated = null;
    }
  }

  public Date getCreationDate() {
    if (creationDate != null) {
      return new Date(creationDate.getTime());
    }
    return null;
  }

  public void setCreationDate(Date creationDate) {
    if (creationDate != null) {
      this.creationDate = new Date(creationDate.getTime());
    } else {
      this.creationDate = null;
    }
  }

  @Override
  public String toString() {
    return String.format("MutualFund [id=%s, shares=%s, value=%s, creationDate=%s, lastUpdated=%s, fund.id=%s, fund.name=%s, manager.id=%s, trustee.id=%s]", id, shares, value,
        creationDate, lastUpdated, fund != null ? fund.getId() : null, fund != null ? fund.getName() : null, manager != null ? manager.getId() : null,
        trustee != null ? trustee.getId() : null);
  }
}
