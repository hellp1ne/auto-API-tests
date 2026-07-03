import api.UserApiClient;
import data.Data;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import json.UserRequest;
import org.junit.After;
import org.junit.Test;

public class TestUserCreationNonParameterized {

    // Create a UserApiClient instance
    UserApiClient userApiClient = new UserApiClient();

    private String createdUserEmail; // Store the email of the created user
    private String accessToken; // Store the access token for cleanup

    @Test
    @DisplayName("Test creating a user with valid data")
    @Description("Verify that a user can be created with valid email, password, and name")
    public void testCreateUser() {
        // Step 1: Create a UserRequest object with a random email and password
        String randomEmail = Data.generateRandomEmail();
        String randomPassword = Data.generateRandomPassword();
        UserRequest userRequest = new UserRequest(
                randomEmail,
                randomPassword,
                "Username"
        );

        // Step 2: Send POST request to create a user
        Response response = userApiClient.createUser(userRequest);

        // Step 3: Assert the response
        userApiClient.assertResponse(response, 200);

        // Step 4: Extract the access token from the response
        this.accessToken = userApiClient.extractAccessToken(response);

        // Store the email of the created user for cleanup
        this.createdUserEmail = randomEmail;
    }

    @Test
    @DisplayName("Test creating a user that already exists")
    @Description("Verify that a user cannot be created if they already exist")
    public void testCreateUserAlreadyExists() {
        // Step 1: Create a UserRequest object with a random email and password
        String randomEmail = Data.generateRandomEmail();
        String randomPassword = Data.generateRandomPassword();
        UserRequest userRequest = new UserRequest(
                randomEmail,
                randomPassword,
                "Username"
        );

        // Step 2: Send POST request to create a user (first time)
        Response response = userApiClient.createUser(userRequest);

        // Step 3: Extract the access token from the response
        this.accessToken = userApiClient.extractAccessToken(response);

        // Step 4: Send POST request to create the same user again (second time)
        response = userApiClient.createUser(userRequest);

        // Step 5: Assert the response status code is 403
        userApiClient.assertResponse(response, 403);

        // Step 6: Assert the value of the "message" key in the response body
        userApiClient.assertResponseMessage(response, "message", "User already exists");

        // Store the email of the created user for cleanup
        this.createdUserEmail = randomEmail;
    }

    @After
    public void tearDown() {
        // Cleanup: Delete the created user
        if (createdUserEmail != null && accessToken != null) {
            // Send DELETE request to delete the user
            Response response = userApiClient.deleteUser(accessToken);

            // Assert the response status code is 202 (or the expected code for successful deletion)
            userApiClient.assertResponse(response, 202);
        }
    }
}