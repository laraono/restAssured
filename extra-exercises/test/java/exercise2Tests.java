import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class exercise2Tests {
    private static final String URL_BASE = "https://restful-booker.herokuapp.com";
    private static RequestSpecification requestSpec;

    @BeforeAll
    static void setuṕ() {
        baseURI = URL_BASE;
        String token = given()
                .contentType("application/json")
                .body("{ \"username\" : \"admin\", \"password\" : \"password123\" }")
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .addHeader("Cookie", "token=" + token)
                .setContentType("application/json")
                .build();
    }

    @Test
    @DisplayName("Get booking sucess")
    void getBookingSucessTest() {
        given().pathParam("booking", 1).when().get("/booking/{booking}").then().statusCode(200).body("firstname", is(
                "Mark"));
    }

    @Test
    @DisplayName("Get invalid booking")
    void getInvalidBookingTest() {
        given().pathParam("booking", 100000).when().get("/booking/{booking}").then().statusCode(404);
    }

    @Test
    @DisplayName("Get invalid type booking")
    void getInvalidTypeBookingTest() {
        given().pathParam("booking", "AAAA").when().get("/booking/{booking}").then().statusCode(is(404));
    }

    @Test
    @DisplayName("Get invalid value booking")
    void getInvalidValueBookingTest() {
        given().pathParam("booking", "-200").when().get("/booking/{booking}").then().statusCode(is(404));
    }

    @Test
    @DisplayName("Get invalid boundary booking")
    void getInvalidBoundaryBookingTest() {
        given().pathParam("booking", "0").when().get("/booking/{booking}").then().statusCode(is(404));
    }


    @Test
    @DisplayName("Delete booking")
    void deleteBookingTest() {
        given().spec(requestSpec).pathParam("booking", "1").when().delete("/booking/{booking}").then().statusCode(is(201));
    }

    @Test
    @DisplayName("Delete invalid booking")
    void deleteInvalidBookingTest() {
        given().spec(requestSpec).pathParam("booking", "0").when().delete("/booking/{booking}").then().statusCode(is(405));
    }

    @Test
    @DisplayName("Delete invalid type booking")
    void deleteInvalidTypeBookingTest() {
        given().spec(requestSpec).pathParam("booking", "AAA").when().delete("/booking/{booking}").then().statusCode(is(405));
    }

    @Test
    @DisplayName("Post booking")
    void postBookingTest() {
        String booking = "{\n" +
                "    \"firstname\" : \"Nome\",\n" +
                "    \"lastname\" : \"Silva\",\n" +
                "    \"totalprice\" : 150,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-01-01\",\n" +
                "        \"checkout\" : \"2024-01-10\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        given().spec(requestSpec).body(booking).when().post("/booking").then().statusCode(is(201));
    }

    @Test
    @DisplayName("Post incomplete booking")
    void postIncompleteBookingTest() {
        String booking = "{\n" +
                "    \"lastname\" : \"Silva\",\n" +
                "    \"totalprice\" : 150,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-01-01\",\n" +
                "        \"checkout\" : \"2024-01-10\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        given().spec(requestSpec).body(booking).when().post("/booking").then().statusCode(is(500));
    }

    @Test
    @DisplayName("Post InvalidType booking")
    void postInvalidTypeBookingTest() {
        String booking = "{\n" +
                "    \"firstname\" : \"Nome\",\n" +
                "    \"lastname\" : 500,\n" +
                "    \"totalprice\" : 150,\n" +
                "    \"depositpaid\" : -1,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-01-01\",\n" +
                "        \"checkout\" : \"2024-01-10\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        given().spec(requestSpec).body(booking).when().post("/booking").then().statusCode(is(500));
    }

    @Test
    @DisplayName("Post incorrect booking - checkout before checkin")
    void postIncorrectBookingTest() {
        String booking = "{\n" +
                "    \"firstname\" : \"Nome\",\n" +
                "    \"lastname\" : \"Silva\",\n" +
                "    \"totalprice\" : 150,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-01-20\",\n" +
                "        \"checkout\" : \"2024-01-10\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        given().spec(requestSpec).body(booking).when().post("/booking").then().statusCode(is(500));
    }

    @Test
    @DisplayName("Post incorrect booking - non existent date")
    void postIncorrectBookingDateTest() {
        String booking = "{\n" +
                "    \"firstname\" : \"Nome\",\n" +
                "    \"lastname\" : \"Silva\",\n" +
                "    \"totalprice\" : 150,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-01-20\",\n" +
                "        \"checkout\" : \"2024-02-31\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        given().spec(requestSpec).body(booking).when().post("/booking").then().statusCode(is(500));
    }

    @Test
    @DisplayName("Post boundary booking")
    void postBoundaryBookingDateTest() {
        String booking = "{\n" +
                "    \"firstname\" : \"Breakfast1231111111111111111111111111111111\",\n" +
                "    \"lastname\" : \"Breakfast1231111111111111111111111111111111\",\n" +
                "    \"totalprice\" : 1500000000000000000000000000000000000,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"3000-01-20\",\n" +
                "        \"checkout\" : \"3000-02-31\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast1231111111111111111111111111111111\"\n" +
                "}";
        given().spec(requestSpec).body(booking).when().post("/booking").then().statusCode(is(200));
    }

    @Test
    @DisplayName("No auth booking")
    void noAuthBookingTest() {
        String booking = "{\n" +
                "    \"firstname\" : \"Nome\",\n" +
                "    \"lastname\" : \"Silva\",\n" +
                "    \"totalprice\" : 150,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2024-01-01\",\n" +
                "        \"checkout\" : \"2024-01-10\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        given().body(booking).when().post("/booking").then().statusCode(is(500));
    }
}
