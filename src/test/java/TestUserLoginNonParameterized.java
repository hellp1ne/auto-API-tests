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

public class TestUserLoginNonParameterized {

    private String email;
    private String password;
    private String accessToken; // To store the access token of the created user

    // Create an instance of UserApiClient
    UserApiClient userApiClient = new UserApiClient();

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

        // Set the email and password for login
        this.email = randomEmail;
        this.password = randomPassword;
    }

    @Test
    @DisplayName("Test successful user login")
    @Description("Verify that a user can log in with valid credentials")
    public void testUserLoginSuccess() {
        // Create the request body for login
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                this.email,
                this.password
        );

        // Send a request to log in
        Response response = userApiClient.loginUser(userLoginRequest);

        // Verify the status code
        userApiClient.assertResponse(response, 200);

        // Verify that the response does not contain an error message
        userApiClient.assertResponseMessage(response, "success", "true");
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