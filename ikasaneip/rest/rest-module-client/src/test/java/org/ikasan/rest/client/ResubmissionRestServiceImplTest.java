package org.ikasan.rest.client;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class ResubmissionRestServiceImplTest
{
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    private ResubmissionRestServiceImpl uut;

    private String contexBaseUrl;

    @Before
    public void setup()
    {
        contexBaseUrl = "http://localhost:" + wireMockRule.port();
        Environment environment = new StandardEnvironment();
        uut = new ResubmissionRestServiceImpl(environment);
    }

    @Test
    public void resubmit()
    {
        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(200)));
        boolean result = uut.resubmit(contexBaseUrl, "test Module Name", "flow Test", "resubmit", "testErrorURI");
        assertEquals(true, result);
    }

    @Test
    public void resubmit_returns400()
    {
        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(400)));
        boolean result = uut.resubmit(contexBaseUrl, "test Module Name", "flow Test", "resubmit", "testErrorURI");
        assertEquals(false, result);
    }

    @Test
    public void resubmit_returns404()
    {
        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString())).withRequestBody(
                containing(
                    "{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
                    .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()).withStatus(404)));
        boolean result = uut.resubmit(contexBaseUrl, "test Module Name", "flow Test", "resubmit", "testErrorURI");
        assertEquals(false, result);
    }

    @Test
    public void resubmit_returns500()
    {

        stubFor(put(urlEqualTo(ResubmissionRestServiceImpl.RESUBMSSION_URL))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
                    .withRequestBody(containing("{\"moduleName\":\"test Module Name\",\"flowName\":\"flow Test\",\"errorUri\":\"testErrorURI\",\"action\":\"resubmit\"}"))
                    .willReturn(aResponse()
                                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                                    .withStatus(500)
                               ));
        boolean result = uut.resubmit(contexBaseUrl,"test Module Name","flow Test","resubmit","testErrorURI");
        assertEquals(false, result);


    }
}