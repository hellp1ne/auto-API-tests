import api.UserApiClient;
import data.Data;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import json.OrderRequest;
import json.UserRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestOrderCreate {

    private String accessToken; // To store the access token of the created user
    private UserApiClient userApiClient = new UserApiClient(); // Create an instance of UserApiClient

    // Create a user before running the tests
    @Before
    public void setUp() {
        // Generate a random email and password
        String randomEmail = Data.generateRandomEmail();
        String randomPassword = Data.generateRandomPassword();

        // Create a request to register a user
        UserRequest userRequest = new UserRequest(
                randomEmail,
                randomPassword,
                "Username"
        );

        // Send a request to create the user
        Response response = userApiClient.createUser(userRequest);

        // Verify that the user was successfully created
        userApiClient.assertResponse(response, 200);

        // Save the access token for cleanup
        this.accessToken = userApiClient.extractAccessToken(response);
    }


    @Test
    @DisplayName("Test creating an order with authorization and valid ingredients")
    @Description("Verify that a user can create an order with valid ingredients when authorized")
    public void testCreateOrderWithAuthorizationAndValidIngredients() {
        // Create the request body for creating an order
        OrderRequest orderRequest = new OrderRequest(
                Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa6f") // Valid ingredients
        );

        // Send a request to create an order with authorization
        Response response = userApiClient.createOrder(orderRequest, this.accessToken);

        // Verify the status code
        userApiClient.assertResponse(response, 200);

        // Verify the value of the "success" key in the response body
        userApiClient.assertResponseMessage(response, "success", "true");
    }


    @Test
    @DisplayName("Test creating an order without authorization but with valid ingredients")
    @Description("Verify that a user can create an order with valid ingredients even without authorization")
    public void testCreateOrderWithoutAuthorizationButWithValidIngredients() {
        // Create the request body for creating an order
        OrderRequest orderRequest = new OrderRequest(
                Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa6f") // Valid ingredients
        );

        // Send a request to create an order without authorization
        Response response = userApiClient.createOrder(orderRequest, null);

        // Verify the status code
        userApiClient.assertResponse(response, 200);

        // Verify the value of the "success" key in the response body
        userApiClient.assertResponseMessage(response, "success", "true");
    }


    @Test
    @DisplayName("Test creating an order with authorization but without ingredients")
    @Description("Verify that a user cannot create an order without ingredients even when authorized")
    public void testCreateOrderWithAuthorizationButWithoutIngredients() {
        // Create the request body for creating an order
        OrderRequest orderRequest = new OrderRequest(
                List.of() // Empty list of ingredients
        );

        // Send a request to create an order with authorization
        Response response = userApiClient.createOrder(orderRequest, this.accessToken);

        // Verify the status code
        userApiClient.assertResponse(response, 400);

        // Verify the value of the "message" key in the response body
        userApiClient.assertResponseMessage(response, "message", "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Test creating an order with authorization and invalid ingredients")
    @Description("Verify that a user cannot create an order with invalid ingredients even when authorized")
    public void testCreateOrderWithAuthorizationAndInvalidIngredients() {
        // Create the request body for creating an order
        OrderRequest orderRequest = new OrderRequest(
                Arrays.asList("invalidIngredient1", "invalidIngredient2") // Invalid ingredients
        );

        // Send a request to create an order with authorization
        Response response = userApiClient.createOrder(orderRequest, this.accessToken);

        // Verify the status code
        userApiClient.assertResponse(response, 500);

        // Verify the value of the "html.body.pre" key in the response body
        userApiClient.assertResponseMessageHTML(response, "html.body.pre", "Internal Server Error");
    }

    // Delete the user after running the tests
    @After
    public void tearDown() {
        if (this.accessToken != null) {
            // Delete the user
            Response response = userApiClient.deleteUser(this.accessToken);

            // Verify that the user was successfully deleted
            userApiClient.assertResponse(response, 202);
        }
    }
}