package br.com.rcrios.smartportfolio.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Benchmark {
  /**
   * Primary key. Internal system ID for benchmarks.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BenchmarkType type;

  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  private Date date;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal dailyFactor;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BenchmarkType getType() {
    return type;
  }

  public void setType(BenchmarkType type) {
    this.type = type;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public BigDecimal getDailyFactor() {
    return dailyFactor;
  }

  public void setDailyFactor(BigDecimal dailyFactor) {
    this.dailyFactor = dailyFactor;
  }

  @Override
  public String toString() {
    return String.format("Benchmark [id=%s, type=%s, date=%s, dailyFactor=%s]", id, type, date, dailyFactor);
  }
}
