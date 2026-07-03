import api.UserApiClient;
import data.Data;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import json.UserLoginRequest;
import json.UserRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestUserLoginParameterizedNegative {

    private String email;
    private String password;
    private String accessToken; // To store the access token of the created user

    // Create an instance of UserApiClient
    UserApiClient userApiClient = new UserApiClient();

    // Constructor for parameterized test
    public TestUserLoginParameterizedNegative(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Parameters for the tests
    @Parameterized.Parameters(name = "Login: {0}, Password: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, "wrong-password"}, // Unsuccessful login with wrong password
                {"nonexistent@example.com", null}, // Unsuccessful login with wrong login
                {"nonexistent@example.com", "wrong-password"} // Unsuccessful login with wrong login and password
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

        // Replace the email with the random email
        if (email == null) {
            email = randomEmail;
        }

        // Replace the password with the random email
        if (password == null) {
            password = randomPassword;
        }
    }

    @Test
    @DisplayName("Test unsuccessful user login")
    @Description("Verify that a user cannot log in with invalid credentials")
    public void testUserLogin() {
        // Create the request body for login
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                this.email,
                this.password
        );

        // Send a request to log in
        Response response = userApiClient.loginUser(userLoginRequest);

        // Verify the status code
        userApiClient.assertResponse(response, 401);

        // Verify the message from response body
        userApiClient.assertResponseMessage(response, "message", "email or password are incorrect");
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