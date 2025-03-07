package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import json.OrderRequest;
import json.UserLoginRequest;
import json.UserRequest;
import json.UserUpdateRequest;
import uri.RequestSpec;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class UserApiClient {

    // Define constants for API endpoints
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String CREATE_ORDER_ENDPOINT = "/api/orders";
    private static final String GET_USER_ORDERS_ENDPOINT = "/api/orders";
    private static final String UPDATE_USER_ENDPOINT = "/api/auth/user";
    private static final String DELETE_USER_ENDPOINT = "/api/auth/user";

    @Step("Send POST request to create a user")
    public Response createUser(UserRequest userRequest) {
        return given()
                .spec(RequestSpec.getRequestSpec()) // Use the common request specification
                .body(userRequest) // Serialize the UserRequest object to JSON
                .post(REGISTER_ENDPOINT); // Use the constant for the endpoint
    }

    @Step("Send POST request to log in a user")
    public Response loginUser(UserLoginRequest userLoginRequest) {
        return given()
                .spec(RequestSpec.getRequestSpec()) // Use the common request specification
                .body(userLoginRequest) // Serialize the UserLoginRequest object to JSON
                .post(LOGIN_ENDPOINT); // Use the constant for the endpoint
    }

    @Step("Send POST request to create an order")
    public Response createOrder(OrderRequest orderRequest, String accessToken) {
        RequestSpecification requestSpec = RequestSpec.getRequestSpec();
        if (accessToken != null) {
            // Add Authorization header if accessToken is provided
            requestSpec = RequestSpec.setAuth(requestSpec, accessToken);
        }

        return given()
                .spec(requestSpec) // Use the request specification
                .body(orderRequest) // Serialize the OrderRequest object to JSON
                .post(CREATE_ORDER_ENDPOINT); // Use the constant for the endpoint
    }

    @Step("Send GET request to retrieve user orders")
    public Response getUserOrders(String accessToken) {
        RequestSpecification requestSpec = RequestSpec.getRequestSpec();
        if (accessToken != null) {
            // Add Authorization header if accessToken is provided
            requestSpec = RequestSpec.setAuth(requestSpec, accessToken);
        }

        return given()
                .spec(requestSpec) // Use the request specification
                .get(GET_USER_ORDERS_ENDPOINT); // Use the constant for the endpoint
    }

    @Step("Send PATCH request to update user data")
    public Response updateUser(UserUpdateRequest userUpdateRequest, String accessToken) {
        RequestSpecification requestSpec = RequestSpec.getRequestSpec();
        if (accessToken != null) {
            // Add Authorization header if accessToken is provided
            requestSpec = RequestSpec.setAuth(requestSpec, accessToken);
        }

        return given()
                .spec(requestSpec) // Use the request specification
                .body(userUpdateRequest) // Serialize the UserUpdateRequest object to JSON
                .patch(UPDATE_USER_ENDPOINT); // Use the constant for the endpoint
    }

    @Step("Assert the response from the user creation request")
    public void assertResponse(Response response, int expectedStatusCode) {
        // Assert the status code
        response.then()
                .statusCode(expectedStatusCode);
    }

    @Step("Extract access token from the response")
    public String extractAccessToken(Response response) {
        return response.jsonPath().getString("accessToken");
    }

    @Step("Send DELETE request to delete a user")
    public Response deleteUser(String accessToken) {
        return given()
                .spec(RequestSpec.setAuth(RequestSpec.getRequestSpec(), accessToken)) // Set the Authorization header
                .delete(DELETE_USER_ENDPOINT); // Use the constant for the endpoint
    }

    @Step("Assert the value of a key in the response body")
    public void assertResponseMessage(Response response, String key, String expectedValue) {
        String actualValue = response.jsonPath().getString(key);
        assertEquals("The value of key '" + key + "' is not as expected.", expectedValue, actualValue);
    }

    @Step("Assert the value of a key in the response HTML body")
    public void assertResponseMessageHTML(Response response, String key, String expectedValue) {
        String actualValue = response.htmlPath().getString(key);
        assertEquals("The value of key '" + key + "' is not as expected.", expectedValue, actualValue);
    }
}