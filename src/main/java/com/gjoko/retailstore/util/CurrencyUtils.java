package com.gjoko.retailstore.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class CurrencyUtils {

  private static NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);

  public static Double stringToDouble(String currency) throws ParseException {
    return numberFormat.parse(currency).doubleValue();
  }

  public static String doubleToString(Double value) {
    return numberFormat.format(value);
  }
}
