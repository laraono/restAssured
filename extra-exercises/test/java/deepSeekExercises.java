import io.restassured.builder.RequestSpecBuilder;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Consent;
import org.example.ConsentService;
import org.hamcrest.collection.ArrayAsIterableMatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class deepSeekExercises {
    static final String BASE_URL = "/api/v1/";
    static final String ID = "1";
    static RequestSpecification authenticated;
    static ConsentService consentService = new ConsentService();
    public static boolean isISO8601(String dateString) {
        // O formato esperado: 2025-02-16T12:00:00Z
        // Nota: O 'Z' precisa ser tratado de forma especial.
        // Uma solução simples é substituir 'Z' por "+0000" se o parser não lidar bem com ele.
        String adaptedDateString = dateString.replace("Z", "+0000");

        // Define o formato esperado. O 'X' lida com fusos horários no padrão ISO (como Z, +00, -03)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setLenient(false); // Torna a validação estrita, sem aceitar valores como "2025-02-31"

        try {
            Date date = sdf.parse(adaptedDateString);
            return true; // A data é válida
        } catch (ParseException e) {
            return false; // A data NÃO está no formato ISO 8601
        }
    }

    class Credentials {
        Integer id;
        String secret;
        String token_url;
        String scope;

        public Credentials() {
        }

        Map<String,String> getMap(){
            Map<String, String> map = new HashMap<>();
            map.put("id", id.toString());
            map.put("secret",secret);
            map.put("token_url", token_url);
            map.put("scope",scope);
            return map;
        }
    }

    @BeforeAll
    static void setup() {
        baseURI = BASE_URL;

        Credentials token =
                given().body("{\"name\": \"Lara\", \"pass\": \"123\"}").when().post("/login").then().extract().response().as(Credentials.class);
        authenticated = new RequestSpecBuilder().addHeaders(token.getMap()).build();
    }

    @DisplayName("GET Status Endpoint")
    @Test
    void upStatusValidation() {
        String res = given()
                .when().get("/health")
                .then()
                .statusCode(200)
                .header("Content-Type", "application/json")
                .body("status", is("UP"))
                .body("timestamp", is(notNullValue()))
                .extract().response().path("timestamp");
        assertTrue(isISO8601(res));
    }

    @DisplayName("Corresponds to Schema")
    @Test
    void schemaValidation() {
        given().pathParam("id", ID).when().get("/accounts/{id}").then().body(matchesJsonSchemaInClasspath("../." +
                "./main/resources/accountSchema.json"));
    }

    @DisplayName("AUTH works")
    @Test
    void authValidation() {
        consentService.createConsent(authenticated,null).then().body(matchesJsonSchemaInClasspath("../." +
                "./main/resources/consentSchema.json"));
    }

    @ParameterizedTest
    @CsvSource(value = "'value'," +
            "null," +
            "'INVALID'")
    @DisplayName("Given Authenticated When Post Consent And Permission Invalid")
    void givenAuthenticatedWhenPostConsentAndPermissionInvalid(String value) {
        List<String> list = new ArrayList<>();
        if(value != null) {
            list.add(value);
        }
        consentService.createConsent(authenticated, new Consent(list))
                .then().statusCode(400).body("error",is("Bad Request"));
    }

    @DisplayName("Given Unauthenticated When Post Consent")
    @Test
    void givenUnauthenticatedWhenPostConsent() {
        consentService.createConsent(given(), null).then().statusCode(401).body("error",is("Unauthorized"));
    }

    @DisplayName("Given Authenticated When Post Consent And Expiration Date Passed")
    @Test
    void givenAuthenticatedWhenPostConsentAndExpirationDatePassed() {
        consentService.createConsent(authenticated, new Consent(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).then().statusCode(422).body("error",is("Unprocessable Entity"));
    }

    @DisplayName("Given Authenticated When Post Consent And Delete")
    @Test
    void givenAuthenticatedWhenPostConsentAndDelete() {
        Response res = consentService.createConsent(authenticated, null);
        String id = res.path("consentId");
        consentService.deleteConsent(authenticated, id).then().statusCode(204);
        consentService.getConsent(authenticated, id).then().statusCode(404);
        consentService.deleteConsent(authenticated, id).then().statusCode(404);
    }
}
