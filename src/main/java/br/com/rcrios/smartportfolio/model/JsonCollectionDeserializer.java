package br.com.rcrios.smartportfolio.model;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonCollectionDeserializer extends StdDeserializer<Object> implements ContextualDeserializer {
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonCollectionDeserializer.class);

  private final BeanProperty property;

  /**
   * Default constructor needed by Jackson to be able to call 'createContextual'. Beware, that the object created here
   * will cause a NPE when used for deserializing!
   */
  public JsonCollectionDeserializer() {
    super(Collection.class);
    this.property = null;
  }

  /**
   * Constructor for the actual object to be used for deserializing.
   *
   * @param property
   *          this is the property/field which is to be serialized
   */
  private JsonCollectionDeserializer(BeanProperty property) {
    super(property.getType());
    this.property = property;
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
    return new JsonCollectionDeserializer(property);
  }

  @Override
  public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    LOGGER.debug("{}", jp);
    switch (jp.getCurrentToken()) {
    case VALUE_STRING:
      LOGGER.debug("{}", jp.getText());
      // value is a string but we want it to be something else: unescape the string and convert it
      return new ObjectMapper().readValue(jp.getText(), this.property.getType());
    // return JacksonUtil.MAPPER.readValue(StringUtil.unescapeXml(jp.getText()), this.property.getType());
    default:
      // continue as normal: find the correct deserializer for the type and call it
      return ctxt.findContextualValueDeserializer(this.property.getType(), this.property).deserialize(jp, ctxt);
    }
  }
}
