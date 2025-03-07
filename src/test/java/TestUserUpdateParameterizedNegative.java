import api.UserApiClient;
import data.Data;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import json.UserRequest;
import json.UserUpdateRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestUserUpdateParameterizedNegative {

    private String accessToken; // To store the access token of the created user
    private UserApiClient userApiClient = new UserApiClient(); // Create an instance of UserApiClient

    private final UserUpdateRequest userUpdateRequest;

    // Constructor for parameterized test
    public TestUserUpdateParameterizedNegative(UserUpdateRequest userUpdateRequest) {
        this.userUpdateRequest = userUpdateRequest;
    }

    // Parameters for the tests
    @Parameterized.Parameters(name = "Updated email: {0}, Updated name: {1}, Updated password: {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new UserUpdateRequest(Data.generateRandomEmail(), null, null)}, // Update email only
                {new UserUpdateRequest(null, "New Name", null)}, // Update name only
                {new UserUpdateRequest(null, null, Data.generateRandomPassword())} // Update password only
        });
    }

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
    @DisplayName("Test updating user data without authorization")
    @Description("Verify that a user cannot update their data without authorization")
    public void testUserUpdateWithAuthorization() {
        // Send a request to update user data with authorization
        Response response = userApiClient.updateUser(userUpdateRequest, null);

        // Verify the status code
        userApiClient.assertResponse(response, 401);

        // Verify the value of the "message" key in the response body
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