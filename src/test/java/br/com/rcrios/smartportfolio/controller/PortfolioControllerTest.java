package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.model.MutualFund;
import br.com.rcrios.smartportfolio.model.Portfolio;
import br.com.rcrios.smartportfolio.repository.PortfolioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PortfolioControllerTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioControllerTest.class);

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private PortfolioRepository pRepository;

  @Autowired
  private TestHelper testHelper;

  @After
  public void cleanup() {
    this.pRepository.deleteAll();
  }

  private static final String name = "unit test " + System.nanoTime();
  private static final BigDecimal shares = BigDecimal.ONE;
  private static final BigDecimal shareValue = BigDecimal.TEN;
  private static final Date shareValueDate = new Date();
  private static final Date lastUpdated = new Date();
  private static final BigDecimal value = BigDecimal.TEN;
  private static final BigDecimal benchmarkValue = BigDecimal.TEN;
  private static final List<MutualFund> mutualFunds = new ArrayList<>();

  @Test
  public void success_save_only_root_portfolio() {
    final String resourceUrl = "/api/portfolios/v1/";

    final JSONObject postBody = jsonFactory();

    ResponseEntity<Portfolio> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.POST,
        this.testHelper.getPostRequestHeaders(postBody.toString()), Portfolio.class);

    LOGGER.trace("{}", responseEntity);

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    final Portfolio created = responseEntity.getBody();
    assertNotNull(created.getId());
    assertEquals(name, created.getName());
    assertTrue(shares.compareTo(created.getShares()) == 0);
    assertTrue(shareValue.compareTo(created.getShareValue()) == 0);
    assertEquals(sdf.format(shareValueDate), sdf.format(created.getShareValueDate()));
    assertTrue(value.compareTo(created.getValue()) == 0);
    assertTrue(benchmarkValue.compareTo(created.getBenchmarkValue()) == 0);

    responseEntity = this.restTemplate.exchange(resourceUrl + "root", HttpMethod.GET, this.testHelper.getRequestHeaders(), Portfolio.class);

    final Portfolio retrived = responseEntity.getBody();
    assertNotNull(retrived.getId());
    assertEquals(name, retrived.getName());
    assertTrue(shares.compareTo(retrived.getShares()) == 0);
    assertTrue(shareValue.compareTo(retrived.getShareValue()) == 0);
    assertEquals(sdf.format(shareValueDate), sdf.format(retrived.getShareValueDate()));
    assertTrue(value.compareTo(retrived.getValue()) == 0);
    assertTrue(benchmarkValue.compareTo(retrived.getBenchmarkValue()) == 0);
  }

  @Test
  public void success_save_with_master() {
    final String resourceUrl = "/api/portfolios/v1/";

    // Creating master portfolio
    final JSONObject masterBody = jsonFactory("unit test master " + System.nanoTime(), shares, shareValue, shareValueDate, value, benchmarkValue, lastUpdated,
        null);

    ResponseEntity<Portfolio> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.POST,
        this.testHelper.getPostRequestHeaders(masterBody.toString()), Portfolio.class);

    final Portfolio master = responseEntity.getBody();
    LOGGER.trace("Master: {}", master);

    // Creating child portfolio
    final JSONObject childBody = jsonFactory("unit test child " + System.nanoTime(), shares, shareValue, shareValueDate, value, benchmarkValue, lastUpdated,
        master);

    final HttpEntity<?> httpEntity = this.testHelper.getPostRequestHeaders(childBody.toString());
    responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.POST, httpEntity, Portfolio.class);

    final Portfolio child = responseEntity.getBody();
    LOGGER.trace("Child: {}", child);

    // Retrieving master to check if child save updated it
    responseEntity = this.restTemplate.exchange(resourceUrl + master.getId(), HttpMethod.GET, this.testHelper.getRequestHeaders(), Portfolio.class);
    final Portfolio masterPostUpdate = responseEntity.getBody();

    LOGGER.trace("Post update master: {}", masterPostUpdate);

    final BigDecimal updtShares = master.getShares().add(child.getValue().divide(master.getShareValue()));
    final BigDecimal updtValue = master.getShareValue().multiply(updtShares);

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    assertTrue(updtShares.compareTo(masterPostUpdate.getShares()) == 0);
    assertTrue(updtValue.compareTo(masterPostUpdate.getValue()) == 0);
    assertTrue(master.getShareValue().compareTo(masterPostUpdate.getShareValue()) == 0);
    assertEquals(sdf.format(child.getLastUpdated()), sdf.format(masterPostUpdate.getLastUpdated()));
  }

  @Test
  public void getAllTest() {
    this.pRepository.save(objFactory());

    final String resourceUrl = "/api/portfolios/v1/";

    final ResponseEntity<Object> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.GET, this.testHelper.getRequestHeaders(), Object.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());

    @SuppressWarnings("unchecked")
    final List<Portfolio> list = (List<Portfolio>) responseEntity.getBody();
    assertFalse(list.isEmpty());
  }

  @Test
  public void testGetByName() {
    final Portfolio created = this.pRepository.save(objFactory());

    final String resourceUrl = "/api/portfolios/v1?name=" + created.getName();

    final ResponseEntity<Portfolio> responseEntity = this.restTemplate.exchange(resourceUrl, HttpMethod.GET, this.testHelper.getRequestHeaders(),
        Portfolio.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    final Portfolio parsed = responseEntity.getBody();
    assertEquals(parsed.getName(), created.getName());
    assertTrue(parsed.getShares().compareTo(created.getShares()) == 0);
    assertTrue(parsed.getShareValue().compareTo(created.getShareValue()) == 0);
    assertEquals(sdf.format(parsed.getShareValueDate()), sdf.format(created.getShareValueDate()));
    assertTrue(parsed.getValue().compareTo(created.getValue()) == 0);
    assertTrue(parsed.getBenchmarkValue().compareTo(created.getBenchmarkValue()) == 0);
  }

  public static JSONObject jsonFactory() {
    return jsonFactory(name, shares, shareValue, shareValueDate, value, benchmarkValue, lastUpdated, null);
  }

  public static JSONObject jsonFactory(String name, BigDecimal shares, BigDecimal shareValue, Date shareValueDate, BigDecimal value, BigDecimal benchmarkValue,
      Date lastUpdated, Portfolio master) {

    final JSONObject postBody = new JSONObject();
    try {
      postBody.put("name", name);
      postBody.put("shares", shares);
      postBody.put("shareValue", shareValue);
      postBody.put("shareValueDate", shareValueDate);
      postBody.put("value", value);
      postBody.put("benchmarkValue", benchmarkValue);
      postBody.put("lastUpdated", lastUpdated);

      if (master != null) {
        final JSONObject masterBody = new JSONObject();
        masterBody.put("id", master.getId());
        masterBody.put("name", master.getName());
        masterBody.put("shares", master.getShares());
        masterBody.put("shareValue", master.getShareValue());
        masterBody.put("shareValueDate", master.getShareValueDate());
        masterBody.put("value", master.getValue());
        masterBody.put("benchmarkValue", master.getBenchmarkValue());
        masterBody.put("lastUpdated", master.getLastUpdated());

        postBody.put("master", masterBody);
      }

      return postBody;
    } catch (final JSONException e) {
      return null;
    }
  }

  public static Portfolio objFactory() {
    final Portfolio p = new Portfolio();
    p.setName(name);
    p.setShares(shares);
    p.setShareValue(shareValue);
    p.setShareValueDate(shareValueDate);
    p.setValue(value);
    p.setBenchmarkValue(benchmarkValue);
    p.setLastUpdated(lastUpdated);
    return p;
  }
}
