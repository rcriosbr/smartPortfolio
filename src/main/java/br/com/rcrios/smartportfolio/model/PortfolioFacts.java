package br.com.rcrios.smartportfolio.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class PortfolioFacts implements Serializable {

  private static final long serialVersionUID = 1L;

  private TrendType trendType;
  private BigDecimal variation;
  private boolean isAsset;
  private BigDecimal share;

  public TrendType getTrendType() {
    return trendType;
  }

  public void setTrendType(TrendType trendType) {
    this.trendType = trendType;
  }

  public BigDecimal getVariation() {
    return variation;
  }

  public void setVariation(BigDecimal variation) {
    this.variation = variation;
  }

  public boolean isAsset() {
    return isAsset;
  }

  public void setAsset(boolean isAsset) {
    this.isAsset = isAsset;
  }

  public BigDecimal getShare() {
    return share;
  }

  public void setShare(BigDecimal share) {
    this.share = share;
  }

  @Override
  public String toString() {
    return String.format("PortfolioFacts [trendType=%s, variation=%s, isAsset=%s, share=%s]", trendType, variation, isAsset, share);
  }
}
