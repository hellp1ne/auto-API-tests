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

public class TestGetUserOrders {

    private String accessToken; // To store the access token of the created user
    private UserApiClient userApiClient = new UserApiClient(); // Create an instance of UserApiClient

    // Create a user and an order before running the tests
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

        // Create an order for the user
        OrderRequest orderRequest = new OrderRequest(
                Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa6f") // Valid ingredients
        );

        // Send a request to create the order
        Response orderResponse = userApiClient.createOrder(orderRequest, this.accessToken);

        // Verify that the order was successfully created
        userApiClient.assertResponse(orderResponse, 200);
    }


    @Test
    @DisplayName("Test retrieving user orders with authorization")
    @Description("Verify that a user can retrieve their orders when authorized")
    public void testGetUserOrdersWithAuthorization() {
        // Send a request to retrieve user orders with authorization
        Response response = userApiClient.getUserOrders(this.accessToken);

        // Verify the status code
        userApiClient.assertResponse(response, 200);

        // Verify the value of the "success" key in the response body
        userApiClient.assertResponseMessage(response, "success", "true");
    }


    @Test
    @DisplayName("Test retrieving user orders without authorization")
    @Description("Verify that a user cannot retrieve their orders without authorization")
    public void testGetUserOrdersWithoutAuthorization() {
        // Send a request to retrieve user orders without authorization
        Response response = userApiClient.getUserOrders(null);

        // Verify the status code
        userApiClient.assertResponse(response, 401);

        // Verify the value of the "success" key in the response body
        userApiClient.assertResponseMessage(response, "message", "You should be authorised");
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