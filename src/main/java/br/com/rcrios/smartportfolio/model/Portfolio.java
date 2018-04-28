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

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
public class Portfolio implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @ManyToOne
  private Portfolio master;

  @OneToMany(fetch = FetchType.EAGER)
  private List<MutualFund> mutualFunds;

  @Temporal(TemporalType.DATE)
  private Date quoteValueDate;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal quotes;

  @Column(precision = 16, scale = 6, nullable = false)
  private @NonNull BigDecimal quoteValue;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal value;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal quoteValueBenchmark;

  @JsonInclude()
  @Transient
  private PortfolioFacts facts;

  public boolean add(MutualFund mf) {
    if (mf.isValid()) {
      if (this.mutualFunds == null) {
        this.mutualFunds = new ArrayList<>();
      }
      this.mutualFunds.add(mf);
      return true;
    }
    return false;
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

  public Portfolio getMaster() {
    return master;
  }

  public void setMaster(Portfolio master) {
    this.master = master;
  }

  public List<MutualFund> getMutualFunds() {
    return Collections.unmodifiableList(this.mutualFunds);
  }

  public void setMutualFunds(List<MutualFund> mutualFunds) {
    this.mutualFunds = mutualFunds;
  }

  public Date getQuoteValueDate() {
    if (quoteValueDate != null) {
      return new Date(quoteValueDate.getTime());
    }
    return null;
  }

  public void setQuoteValueDate(Date quoteValueDate) {
    if (quoteValueDate != null) {
      this.quoteValueDate = new Date(quoteValueDate.getTime());
    } else {
      this.quoteValueDate = null;
    }
  }

  public BigDecimal getQuotes() {
    return quotes;
  }

  public void setQuotes(BigDecimal quotes) {
    this.quotes = quotes;
  }

  public BigDecimal getQuoteValue() {
    return quoteValue;
  }

  public void setQuoteValue(BigDecimal quoteValue) {
    this.quoteValue = quoteValue;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public BigDecimal getQuoteValueBenchmark() {
    return quoteValueBenchmark;
  }

  public void setQuoteValueBenchmark(BigDecimal quoteValueBenchmark) {
    this.quoteValueBenchmark = quoteValueBenchmark;
  }

  public PortfolioFacts getFacts() {
    return facts;
  }

  public void setFacts(PortfolioFacts facts) {
    this.facts = facts;
  }

  @Override
  public String toString() {
    return String.format("Portfolio [id=%s, name=%s, master.id=%s, mutualfunds=%s, quoteValueDate=%s, quotes=%s, quoteValue=%s, value=%s, quoteValueBenchmark=%s]", id, name,
        master != null ? master.getId() : null, mutualFunds, quoteValueDate, quotes, quoteValue, value, quoteValueBenchmark);
  }
}
