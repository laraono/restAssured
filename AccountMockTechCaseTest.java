import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

public class AccountMockTechCaseTest {
    private String baseUrl;
    private String accountsEndpoint;
    private List<Account> accounts;

    @BeforeEach
    void setUp() {
        // Get baseUrl from system property, environment variable, or use default
        baseUrl = "http://localhost:8080";
        accountsEndpoint = "/accounts";
        accounts = new ArrayList<>();
    }

    /**
     * Helper method to get all accounts from the API
     * @return List of Account objects with all properties set
     */

    // without header status code and message
    private List<Account> getAllAccounts() {
        // Getting all accounts from the mock project
        System.out.println(baseUrl);

        Response response = given()
            .header("Authorization", "Bearer test-token")
                .header("x-fapi-interaction-id","1f8e2a24-4c2b-4e06-b0b0-1f0d8bb2aabc")
            .when().log().all()
            .get(baseUrl + accountsEndpoint);

        // Checking if the response is successful
        response.then().log().all()
            .statusCode(200);
        
        // Deserializing the data attribute results into Account objects
        List<Account> deserializedAccounts = response.getBody().jsonPath().getList("data", Account.class);
        
        // Create Account objects dynamically for each account returned and set all properties using setters
        List<Account> accountList = new ArrayList<>();
        for (Account deserializedAccount : deserializedAccounts) {
            Account account = new Account();
            account.setAccountId(deserializedAccount.getAccountId());
            account.setBrandName(deserializedAccount.getBrandName());
            account.setCompanyCnpj(deserializedAccount.getCompanyCnpj());
            account.setType(deserializedAccount.getType());
            account.setCompeCode(deserializedAccount.getCompeCode());
            account.setBranchCode(deserializedAccount.getBranchCode());
            account.setNumber(deserializedAccount.getNumber());
            account.setCheckDigit(deserializedAccount.getCheckDigit());
            accountList.add(account);
        }
        
        return accountList;
    }

    /**
     * Helper method to get a specific account by ID from the API
     * @param accountId The account ID to retrieve
     * @return AccountDetail object with all properties set
     */
    private AccountDetail getAccountById(String accountId) {
        // Do a request to the specific account endpoint
        Response accountResponse = given()
            .auth()
            .oauth2("Bearer 1234567890")
            .header("x-fapi-interaction-id", UUID.randomUUID().toString())
            .when().log().all()
            .get(baseUrl + accountsEndpoint + "/" + accountId);

        accountResponse.then().log().all()
            .statusCode(200);
        
        // Deserializing the data attribute results into AccountDetail object
        AccountDetail accountDetail = accountResponse.getBody().jsonPath().getObject("data", AccountDetail.class);
        
        return accountDetail;
    }

    @Test
    void testGetAllAccounts() {
        // Get all accounts using the helper method
        accounts = getAllAccounts();

        // Print all values from all objects at the end
        for (Account account : accounts) {
            System.out.println("Account ID: " + account.getAccountId());
            System.out.println("Brand Name: " + account.getBrandName());
            System.out.println("Company CNPJ: " + account.getCompanyCnpj());
            System.out.println("Type: " + account.getType());
            System.out.println("COMPE Code: " + account.getCompeCode());
            System.out.println("Branch Code: " + account.getBranchCode());
            System.out.println("Number: " + account.getNumber());
            System.out.println("Check Digit: " + account.getCheckDigit());
            System.out.println("---");
        }
    }
    @DisplayName("Test without the interaction-id")
    @Test
    void givenAccountURLgetWithoutInteractionId() {
        given().log().all().baseUri(baseUrl).header("Authorization", "Bearer test-token")
                .when().log().all().get("/accounts").then().log().all().statusCode(400).body("errors[0].detail", is("Missing required header: x-fapi-interaction-id"));

    }

    @Test
    void testGetAccountById() {
        // Get all accounts using the helper method
        accounts = getAllAccounts();

        // Get each account by ID using the helper method and assert properties match
        for (Account account : accounts) {
            AccountDetail accountDetail = getAccountById(account.getAccountId());

            // Assert that common account properties match between the list and individual account response
            assertEquals(account.getType(), accountDetail.getType());
            assertEquals(account.getCompeCode(), accountDetail.getCompeCode());
            assertEquals(account.getBranchCode(), accountDetail.getBranchCode());
            assertEquals(account.getNumber(), accountDetail.getNumber());
            assertEquals(account.getCheckDigit(), accountDetail.getCheckDigit());
        }
    }

    @Getter
    @Setter
    public static class Account {
        private String accountId;
        private String brandName;
        private String companyCnpj;
        private String type;
        private String compeCode;
        private String branchCode;
        private String number;
        private String checkDigit;
    }

    @Getter
    @Setter
    public static class AccountDetail {
        private String compeCode;
        private String branchCode;
        private String number;
        private String checkDigit;
        private String type;
        private String subtype;
        private String currency;
    }
}
