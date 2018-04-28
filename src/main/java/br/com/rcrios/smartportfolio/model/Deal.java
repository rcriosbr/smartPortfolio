package br.com.rcrios.smartportfolio.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

@Entity
public class Deal {
  private static final Logger LOGGER = LoggerFactory.getLogger(Deal.class);

  /**
   * Primary key. Internal system ID for deals.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Every deal must be attached to one mutual fund.
   */
  @ManyToOne(fetch = FetchType.EAGER)
  private MutualFund mutualFund;

  /**
   * Date when the deal was done.
   */
  @Temporal(TemporalType.DATE)
  private Date date;

  /**
   * Deal value. Precision of 16 and scale of 6.
   */
  @Column(precision = 16, scale = 6)
  private BigDecimal value;

  /**
   * Deal quantity of quotes. Precision of 16 and scale of 6.
   */
  @Column(precision = 16, scale = 6)
  private BigDecimal shares;

  /**
   * Transaction type, such as BUY, SELL, etc.
   */
  private TransactionType type;

  /**
   * Comments that describe the deal.
   */
  private String comments;

  public static void validate(Deal toBeValidated) {
    if (toBeValidated == null) {
      throw new InvalidObject("Deal object cannot be null.");
    }

    if (toBeValidated.getMutualFund() == null || !toBeValidated.getMutualFund().isValid()) {
      throw new InvalidObject("Deal must have a valid mutual fund.");
    }

    if (toBeValidated.getDate() == null) {
      throw new InvalidObject("Deal date cannot be null.");
    }

    if (toBeValidated.getType() == null) {
      throw new InvalidObject("Deal type cannot be null.");
    }

    if (toBeValidated.getValue() == null && toBeValidated.getShares() == null) {
      throw new InvalidObject("Deal must have a value OR shares.");
    }
  }

  /**
   * Helper method that verifies if this object is valid.
   * 
   * @return True if the object is valid. False otherwise. The underlying exception will be logged as a warning.
   */
  public boolean isValid() {
    try {
      Deal.validate(this);
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

  public MutualFund getMutualFund() {
    return mutualFund;
  }

  public void setMutualFund(MutualFund mutualFund) {
    this.mutualFund = mutualFund;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public BigDecimal getShares() {
    return shares;
  }

  public void setShares(BigDecimal shares) {
    this.shares = shares;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  @Override
  public String toString() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    return String.format("Deal [id=%s, date=%s, value=%s, shares=%s, type=%s, mutualFund.id=%s, mutualFund.name=%s, comments=%s]", id, sdf.format(date), value, shares, type,
        mutualFund != null ? mutualFund.getId() : null, mutualFund != null ? (mutualFund.getFund() != null ? mutualFund.getFund().getName() : null) : null, comments);
  }
}
