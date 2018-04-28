package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.model.LegalEntity;
import br.com.rcrios.smartportfolio.repository.LegalEntityRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LegalEntityControllerTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(LegalEntityControllerTest.class);

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private LegalEntityRepository leRepository;

  @Autowired
  private TestHelper testHelper;

  @After
  public void cleanup() {
    this.leRepository.deleteAll();
  }

  private static final String name = "unit test " + System.nanoTime();
  private static final String ntpid = String.valueOf(System.nanoTime());
  private static final String nickname = "ut";

  @Test
  public void successSave() {
    final String resourceUrl = "/api/legalentities/v1/";

    final JSONObject postBody = jsonFactory();

    final ResponseEntity<LegalEntity> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.POST,
        this.testHelper.getPostRequestHeaders(postBody.toString()), LegalEntity.class);

    LOGGER.trace("{}", responseEntity);

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());

    final LegalEntity createdLegalEntity = responseEntity.getBody();
    assertNotNull(createdLegalEntity.getId());
    assertEquals(name, createdLegalEntity.getName());
    assertEquals(ntpid, createdLegalEntity.getNationalTaxPayerId());
    assertEquals(nickname, createdLegalEntity.getNickname());
  }

  @Test
  public void failedSave() {
    final String resourceUrl = "/api/legalentities/v1/";

    final JSONObject postBody = jsonFactory("", ntpid, nickname);

    final ResponseEntity<Object> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.POST,
        this.testHelper.getPostRequestHeaders(postBody.toString()), Object.class);

    LOGGER.trace("{}", responseEntity);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());
    assertTrue(responseEntity.getBody().toString().contains("Posted object isn't valid"));
  }

  @Test
  public void internalServerErrorSave() {
    final LegalEntity le = objFactory();
    this.leRepository.save(le);

    final String resourceUrl = "/api/legalentities/v1/";

    final JSONObject postBody = jsonFactory(le.getName(), le.getNationalTaxPayerId(), le.getNickname());

    final ResponseEntity<Object> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.POST,
        this.testHelper.getPostRequestHeaders(postBody.toString()), Object.class);

    LOGGER.trace("{}", responseEntity);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());
    assertTrue(responseEntity.getBody().toString().contains("Failed to save legal entity"));
  }

  @Test
  public void getAllTest() {
    this.leRepository.save(objFactory());

    final String resourceUrl = "/api/legalentities/v1/";

    final ResponseEntity<Object> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.GET, this.testHelper.getRequestHeaders(), Object.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());

    @SuppressWarnings("unchecked")
    final List<LegalEntity> list = (List<LegalEntity>) responseEntity.getBody();
    assertFalse(list.isEmpty());
  }

  @Test
  public void getLegalEntityTest() {
    final LegalEntity createdLegalEntity = this.leRepository.save(objFactory());

    final String resourceUrl = "/api/legalentities/v1/" + createdLegalEntity.getNationalTaxPayerId();

    final ResponseEntity<LegalEntity> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.GET, this.testHelper.getRequestHeaders(),
        LegalEntity.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());

    final LegalEntity parsedContact = responseEntity.getBody();
    assertEquals(createdLegalEntity.getId(), parsedContact.getId());
    assertEquals(createdLegalEntity.getName(), parsedContact.getName());
    assertEquals(createdLegalEntity.getNationalTaxPayerId(), parsedContact.getNationalTaxPayerId());
    assertEquals(createdLegalEntity.getNickname(), parsedContact.getNickname());
  }

  @Test
  public void handleNotFound() {
    this.leRepository.save(objFactory());

    final String resourceUrl = "/api/legalentities/v1/666";

    final ResponseEntity<LegalEntity> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.GET, this.testHelper.getRequestHeaders(),
        LegalEntity.class);

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());
  }

  @Test
  public void handleEmptyList() {
    this.leRepository.deleteAll();

    final String resourceUrl = "/api/legalentities/v1/";

    final ResponseEntity<Object> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.GET, this.testHelper.getRequestHeaders(), Object.class);

    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());
  }

  public static JSONObject jsonFactory() {
    return jsonFactory(name, ntpid, nickname);
  }

  public static JSONObject jsonFactory(String name, String ntpid, String nickname) {
    final JSONObject postBody = new JSONObject();
    try {
      postBody.put("name", name);
      postBody.put("nationalTaxPayerId", ntpid);
      postBody.put("nickname", nickname);
      return postBody;
    } catch (final JSONException e) {
      return null;
    }
  }

  public static LegalEntity objFactory() {
    final LegalEntity le = new LegalEntity();
    le.setName(name);
    le.setNationalTaxPayerId(ntpid);
    le.setNickname(nickname);
    return le;
  }
}
