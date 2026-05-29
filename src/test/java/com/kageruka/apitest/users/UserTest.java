package com.kageruka.apitest.users;

import com.kageruka.apitest.base.BaseTest;
import com.kageruka.apitest.config.Constants;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Feature("Users")
public class UserTest extends BaseTest {

//    GET
    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users - List all users")
    @Description("Verify GET /users returns all 10 users with valid data")
    public void testGetAllUsers() {
        given()
                .spec(requestSpec)
                .when()
                .get(UserEndpoint.BASE)
                .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(10))
                .body("id", everyItem(notNullValue()))
                .body("name", everyItem(notNullValue()))
                .body("email", everyItem(notNullValue()));
    }

    @ParameterizedTest(name = "GET /users/{0} - returns correct user")
    @MethodSource("com.kageruka.apitest.users.UserDataProvider#validUserIds")
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify GET /users/{id} returns correct user profile data")
    public void testGetUserById(int userId, String expectedName, String expectedUsername) {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoint.BASE + "/" + userId)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("id", equalTo(userId))
                .body("name", equalTo(expectedName))
                .body("username", equalTo(expectedUsername));
    }

//    Relationship Tests
    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users/1/posts - Get user's posts")
    @Description("Verify relationship: users have many posts, all linked by userId")
    public void testGetUserPosts() {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoint.BASE + "/1/posts")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(greaterThan(0)))
                .body("userId", everyItem(equalTo(1)));
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users/1/albums - Get user's albums")
    @Description("Verify relationship: users have many albums, all linked by userId")
    public void testGetUserAlbums() {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoint.BASE + "/1/albums")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(greaterThan(0)))
                .body("userId", everyItem(equalTo(1)));
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users/1/todos - Get user's todos")
    @Description("Verify relationship: users have many todos, all linked by userId")
    public void testGetUserTodos() {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoint.BASE + "/1/todos")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(greaterThan(0)))
                .body("userId", everyItem(equalTo(1)));
    }

    @ParameterizedTest(name = "GET /users/{0} - returns 404")
    @MethodSource("com.kageruka.apitest.users.UserDataProvider#invalidUserIds")
    @Order(6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /users/{id} returns 404 for non-existent or invalid IDs")
    public void testGetUserNotFound(int invalidId) {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoint.BASE + "/" + invalidId)
        .then()
                .statusCode(Constants.STATUS_NOT_FOUND);
    }

//    Schema validation
    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users/1 - Schema validation")
    @Description("Verify response structure matches user JSON schema contract")
    public void testUserSchemaValidation() {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

//    Post
    @Test
    @Order(8)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /users - Create new user")
    @Description("Verify POST /users creates a new user and returns 201 with correct data")
    public void testCreateUser() {
        given()
                .spec(requestSpec)
                .body(UserPayload.create())
        .when()
                .post(UserEndpoint.BASE)
        .then()
                .statusCode(Constants.STATUS_CREATED)
                .body("name", equalTo("Test User"))
                .body("username", equalTo("testuser"))
                .body("id", notNullValue());
    }

//    Put
    @Test
    @Order(9)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /users/1 - Update user")
    @Description("Verify PUT /users/1 updates the user and returns 200 with updated data")
    public void testUpdateUser() {
        given()
                .spec(requestSpec)
                .body(UserPayload.update())
        .when()
                .put(UserEndpoint.BASE + "/1")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("name", equalTo("Updated User"))
                .body("username", equalTo("updateduser"))
                .body("id", equalTo(1));
    }

//    Delete
    @Test
    @Order(10)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /users/1 - Delete user")
    @Description("Verify DELETE /users/1 returns 200 confirming deletion")
    public void testDeleteUser() {
        given()
                .spec(requestSpec)
        .when()
                .delete(UserEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK);
    }

//    Headers & Performance
    @Test
    @Order(11)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /users/1 - Verify headers and response time")
    @Description("Verify Content-Type header is JSON and response time is under threshold")
    public void testUserHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .header("Content-Type", containsString("application/json"))
                .time(lessThan(Constants.MAX_RESPONSE_TIME_MS));
    }
}
