package br.com.rcrios.smartportfolio.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {

  @SuppressWarnings("rawtypes")
  public HttpEntity getRequestHeaders() {
    final List<MediaType> acceptTypes = new ArrayList<>();
    acceptTypes.add(MediaType.APPLICATION_JSON_UTF8);

    final HttpHeaders reqHeaders = new HttpHeaders();
    reqHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    reqHeaders.setAccept(acceptTypes);

    return new HttpEntity<>("parameters", reqHeaders);
  }

  @SuppressWarnings("rawtypes")
  public HttpEntity getPostRequestHeaders(String jsonPostBody) {
    final List<MediaType> acceptTypes = new ArrayList<>();
    acceptTypes.add(MediaType.APPLICATION_JSON_UTF8);

    final HttpHeaders reqHeaders = new HttpHeaders();
    reqHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    reqHeaders.setAccept(acceptTypes);

    return new HttpEntity<>(jsonPostBody, reqHeaders);
  }
}
