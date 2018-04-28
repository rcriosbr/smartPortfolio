package br.com.rcrios.smartportfolio.model;

import java.util.Objects;

public enum TransactionType {
  CREATE_PERSON, CREATE_FUND, CREATE_PORTFOLIO, CREATE_QUOTE, BUY, SELL, UPDATE;

  public static TransactionType factory(Object type) {
    return factory(Objects.toString(type));
  }

  public static TransactionType factory(String type) {
    TransactionType result = null;
    switch (type) {
    case "CREATE_PERSON":
      result = TransactionType.CREATE_PERSON;
      break;
    case "CREATE_FUND":
      result = TransactionType.CREATE_FUND;
      break;
    case "CREATE_PORTFOLIO":
      result = TransactionType.CREATE_PORTFOLIO;
      break;
    case "CREATE_QUOTE":
      result = TransactionType.CREATE_QUOTE;
      break;
    case "BUY":
      result = TransactionType.BUY;
      break;
    case "SELL":
      result = TransactionType.SELL;
      break;
    case "UPDATE":
      result = TransactionType.UPDATE;
      break;
    }
    return result;
  }
}
