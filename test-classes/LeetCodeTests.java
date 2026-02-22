package com.raidiam.hello;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class LeetCodeTests {

    private static final String DEFAULT_URL = "https://api.exemplo.com/";

    @BeforeAll
    static void setup() {
        baseURI = DEFAULT_URL;
        Response auth = given().body("{name: \"1\", pass: \"123\"}").when().post("/auth");
        requestSpecification =
                new RequestSpecBuilder().addHeader("Authorization", "Bearer " + auth.path("token")).setContentType(
                        "application/json").build();
    }

    @DisplayName("GET endpoint")
    @Test
    void getEndpoint() {
        given().spec(requestSpecification).pathParam("users", "1").when().get("/v1").then().statusCode(200).time(is(2000L)).body("name", allOf(isA(String.class), not(emptyString())));
    }

    @DisplayName("GET products endpoint")
    @Test
    void getProductsEndpoint() {
        Response response = given().when().get("/products");
        List<Map<String, ?>> products = response.path("products.findAll {it.price >= 500}");
        System.out.println("Produtos caros:" + products);
        response.then().body("products.findAll {it.price >= 500}.category", everyItem(is("Eletronics")));
        Map<String, ?> product = response.path("products.max{it.price}.name");
        System.out.println("Mais caro" + product);
    }

    @DisplayName("POST user endpoint")
    @Test
    void postUserEndpoint() {
        User user = new User("nome", 1);
        Response response =
                given().spec(requestSpecification).body(user).when().post("/users").then().statusCode(201).extract().response();
        Integer id = response.path("id");
        System.out.println("Id" + id);
    }

    @DisplayName("GET user endpoint")
    @Test
    void getUserEndpoint() {
        List<User> response =
                given().spec(requestSpecification).when().get("/users").then().statusCode(200).extract().as(new TypeRef<List<User>>() {});
        System.out.println(response.size());
    }

    @DisplayName("GET user endpoint 2")
    @Test
    void getUserEndpoint2() {
        given().spec(requestSpecification).when().get("/users").then().statusCode(200).body("size()", not(0)).body(
                "name", everyItem(allOf(isA(String.class), not(emptyOrNullString()))));
    }

    //    JSON retornado pelo endpoint de "Contas" (/accounts) segue exatamente o contrato definido (campos
    //    obrigatórios, tipos de dados).

    @DisplayName("GET account endpoint")
    @Test
    void getAccountEndpoint() {
        given().spec(requestSpecification)
                .when().get("/accounts")
                .then().statusCode(200)
                .body("$.size()", greaterThan(0))
                .body("type", everyItem(allOf(isA(String.class), not(emptyOrNullString()))))
                .body("id", everyItem(allOf(isA(Integer.class), notNullValue())))
                .body("owner_id", everyItem(allOf(isA(Integer.class), notNullValue())))
                .body("venue", everyItem(allOf(isA(Number.class), notNullValue(), greaterThanOrEqualTo(0.0f))));
    }

    @DisplayName("GET account without authorization endpoint")
    @Test
    void getAccountWithoutAuthEndpoint() {
        given().when().get("/accounts").then().statusCode(401).body("error", is("Not Authorized"));
    }

    @DisplayName("GET account with invalid authorization endpoint")
    @Test
    void getAccountWithInvalidAuthEndpoint() {
        given().header("Authorization","Bearer NOTVALID").when().get("/accounts").then().statusCode(403).body("error", is("Not Authorized"));
    }
}
