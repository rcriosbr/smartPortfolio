package br.com.rcrios.smartportfolio;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {
  private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

  public static final MathContext DEFAULT_MATHCONTEXT = MathContext.DECIMAL64;

  public static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

  /**
   * Hides utility class constructor
   */
  private Utils() {
    // Empty
  }

  public static BigDecimal nrFactory(Object number) {
    return nrFactory(Objects.toString(number, ""));
  }

  public static BigDecimal nrFactory(String number) {
    if (number == null || number.trim().isEmpty()) {
      return null;
    }

    return new BigDecimal(number, DEFAULT_MATHCONTEXT);
  }

  public static String toString(Number number) {
    return Utils.toString(number, "%.6f");
  }

  public static String toString(Number number, String format) {
    return number == null ? "" : String.format(format, number);
  }

  public static String toString(Date date) {
    return Utils.toString(date, DEFAULT_DATE_PATTERN);
  }

  public static String toString(Date date, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return date == null ? "" : sdf.format(date);
  }

  public static Date toDate(String date) {
    return Utils.toDate(date, DEFAULT_DATE_PATTERN);
  }

  public static Date toDate(String date, String pattern) {
    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
    try {
      return formatter.parse(date);
    } catch (ParseException e) {
      LOGGER.error("Could not convert '" + Objects.toString(date) + "' to a Date object using pattern '" + Objects.toString(pattern) + "'.", e);
      return null;
    }
  }

  public static boolean isNonZero(BigDecimal number) {
    if (number != null && !(number.compareTo(BigDecimal.ZERO) == 0)) {
      return true;
    }
    return false;
  }
}
