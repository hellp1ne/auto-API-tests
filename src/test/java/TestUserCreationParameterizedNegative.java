import api.UserApiClient;
import data.Data;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import json.UserRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestUserCreationParameterizedNegative {

    // Create a UserApiClient instance
    UserApiClient userApiClient = new UserApiClient();

    private String createdUserEmail; // Store the email of the created user
    private String accessToken; // Store the access token for cleanup

    private final UserRequest userRequest;

    // Constructor for parameterized test
    public TestUserCreationParameterizedNegative(UserRequest userRequest) {
        this.userRequest = userRequest;
    }

    // Data provider for parameterized test
    @Parameterized.Parameters(name = "JSON with missing params: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new UserRequest(null, Data.generateRandomPassword(), "Username")},
                {new UserRequest(Data.generateRandomEmail(), null, "Username")},
                {new UserRequest(Data.generateRandomEmail(), Data.generateRandomPassword(), null)}
        });
    }

    @Test
    @DisplayName("Test creating a user with missing required fields")
    @Description("Verify that a user cannot be created if required fields are missing")
    public void testCreateUserMissingRequiredField() {
        // Step 1: Send POST request to create a user with missing required field
        Response response = userApiClient.createUser(userRequest);

        // Step 2: Assert the response status code is 403
        userApiClient.assertResponse(response, 403);

        // Step 3: Assert the value of the "message" key in the response body
        userApiClient.assertResponseMessage(response, "message", "Email, password and name are required fields");
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