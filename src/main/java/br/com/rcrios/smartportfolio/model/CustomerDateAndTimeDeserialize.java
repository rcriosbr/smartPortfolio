package br.com.rcrios.smartportfolio.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomerDateAndTimeDeserialize extends JsonDeserializer<Date> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDateAndTimeDeserialize.class);

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

  @Override
  public Date deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext) throws IOException, JsonProcessingException {
    final String str = paramJsonParser.getText().trim();

    try {
      return this.dateFormat.parse(str);
    } catch (final ParseException e) {
      LOGGER.error("Error deserializing date.", e);
    }
    return paramDeserializationContext.parseDate(str);
  }
}
