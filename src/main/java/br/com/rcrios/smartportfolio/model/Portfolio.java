package br.com.rcrios.smartportfolio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
public class Portfolio implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(Portfolio.class);

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @ManyToOne
  private Portfolio master;

  @OneToMany(fetch = FetchType.EAGER)
  private List<MutualFund> mutualFund = new ArrayList<>();

  @JsonFormat(pattern = "EEE MMM dd HH:mm:ss z yyyy", locale = "US")
  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  private Date shareValueDate;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal shares;

  @Column(precision = 16, scale = 6, nullable = false)
  private @NonNull BigDecimal shareValue;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal value;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal benchmarkValue;

  @JsonFormat(pattern = "EEE MMM dd HH:mm:ss z yyyy", locale = "US")
  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  private Date lastUpdated;

  @JsonInclude()
  @Transient
  private PortfolioFacts facts;

  /**
   * Verifies if the provided object is valid. If it is not, throws an SmartPortfolioRuntimeException. To be valid, ...
   *
   * @param toBeValidated
   *          Object to be validated
   */
  public static void validate(Portfolio toBeValidated) {
    if (toBeValidated == null) {
      throw new InvalidObject("Portfolio object cannot be null.");
    }

    if ((toBeValidated.getName() == null) || toBeValidated.getName().trim().isEmpty()) {
      throw new InvalidObject("Portfolio name cannot be null or empty.");
    }

    // TODO Implementar validações faltantes
  }

  /**
   * Helper method that verifies if this object is valid.
   *
   * @return True if the object is valid. False otherwise. The underlying exception will be logged as a warning.
   */
  public boolean isValid() {
    try {
      Portfolio.validate(this);
    } catch (final InvalidObject e) {
      LOGGER.warn("Object is invalid. Current object state: " + this.toString(), e);
      return false;
    }
    return true;
  }

  public boolean add(MutualFund mf) {
    if (mf.isValid()) {
      this.mutualFund.add(mf);
      return true;
    }
    return false;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Portfolio getMaster() {
    return this.master;
  }

  public void setMaster(Portfolio master) {
    this.master = master;
  }

  /**
   * Helper method to debug
   *
   * @return
   */
  public String getMasterAsString() {
    if (this.master != null) {
      return String.format("master.id=%s; master.name=%s", this.master.getId(), this.master.getName());
    }
    return "null";
  }

  public List<MutualFund> getMutualFund() {
    return Collections.unmodifiableList(this.mutualFund);
  }

  public void setMutualFund(List<MutualFund> mutualFund) {
    this.mutualFund = mutualFund;
  }

  public Date getShareValueDate() {
    if (this.shareValueDate != null) {
      return new Date(this.shareValueDate.getTime());
    }
    return null;
  }

  public void setShareValueDate(Date shareValueDate) {
    if (shareValueDate != null) {
      this.shareValueDate = new Date(shareValueDate.getTime());
    } else {
      this.shareValueDate = null;
    }
  }

  public BigDecimal getShares() {
    return this.shares;
  }

  public void setShares(BigDecimal shares) {
    this.shares = shares;
  }

  public BigDecimal getShareValue() {
    return this.shareValue;
  }

  public void setShareValue(BigDecimal shareValue) {
    this.shareValue = shareValue;
  }

  public BigDecimal getValue() {
    return this.value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public BigDecimal getBenchmarkValue() {
    return this.benchmarkValue;
  }

  public void setBenchmarkValue(BigDecimal benchmarkValue) {
    this.benchmarkValue = benchmarkValue;
  }

  public Date getLastUpdated() {
    if (this.lastUpdated != null) {
      return new Date(this.lastUpdated.getTime());
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

  public PortfolioFacts getFacts() {
    return this.facts;
  }

  public void setFacts(PortfolioFacts facts) {
    this.facts = facts;
  }

  @Override
  public String toString() {
    return String.format("Portfolio [id=%s, name=%s, master.id=%s, mutualfunds=%s, shareValueDate=%s, shares=%s, shareValue=%s, value=%s, benchmarkValue=%s]",
        this.id, this.name, this.master != null ? this.master.getId() : null, this.mutualFund, this.shareValueDate, this.shares, this.shareValue, this.value,
        this.benchmarkValue);
  }
}
