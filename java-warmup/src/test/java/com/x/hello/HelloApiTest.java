package com.raidiam.hello;

import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;

/**
 * Template: test GET /hello (api must be running on baseUrl).
 * Requires: Authorization Bearer token, header "header-test".
 */
class HelloApiTest {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final HelloApiPage helloApiPage = new HelloApiPage();
    @BeforeAll
    static void setup() {
        String baseUrl = System.getProperty("baseUrl", DEFAULT_BASE_URL);
        RestAssured.baseURI = baseUrl;
    }

    @DisplayName("Verifying authorization according to headers")
    @ParameterizedTest
    @CsvSource(value = {
            "Success Case             | Bearer token | valid-header | 200 | message | Hello World",
            "Missing Authorization    | ''           | valid-header | 401 | error   | Authorization is required. Bearer token is missing or invalid.",
            "Missing Custom Header    | Bearer token | ''           | 400 | error   | Required header 'header-test' is missing",
            "Both Headers Missing     | ''           | ''           | 400 | error   | Required header 'header-test' is missing"
    }, delimiter = '|')
    void success_withValidHeaders(String desc, String authorization, String header, Integer status, String title, String output) {
        System.out.println(desc);
        helloApiPage.requestWithHeader(authorization,header).when().get("/hello").then().statusCode(status).body(title, is(output));

    }

}
