package org.example;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ConsentService {

    public Response createConsent(RequestSpecification requestSpecification, Consent consentInput){
        Consent body = (consentInput == null) ? new Consent() : consentInput;
        return requestSpecification.body(consentInput).when().post("/consents");
    }

    public Response deleteConsent(RequestSpecification requestSpecification, String id){
        return requestSpecification.pathParam("consentId", id).when().delete("/consents/{consentId}");
    }

    public Response getConsent(RequestSpecification requestSpecification, String id){
        return requestSpecification.pathParam("consentId", id).when().get("/consents/{consentId}");
    }


}
