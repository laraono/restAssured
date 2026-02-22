package com.raidiam.hello;

import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class HelloApiPage {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    public RequestSpecification requestWithHeader(String auth, String headerTest){
        Map<String, String> headers = new HashMap<>();

        if(auth != null){ headers.put("Authorization", auth);}
        if(headerTest != null){ headers.put("header-test", headerTest);}

        return given().baseUri(DEFAULT_BASE_URL).headers(headers);
    }
}
