package com.kageruka.apitest.comments;

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

@Feature("Comments")
public class CommentTest extends BaseTest {

//    GET
    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /comments - List comments with limit")
    @Description("Verify GET /comments?_limit=10 returns exactly 10 comments with valid data")
    public void testGetAllComments() {
        given()
                .spec(requestSpec)
                .queryParam("_limit", 10)
        .when()
                .get(CommentEndpoint.BASE)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(10))
                .body("id", everyItem(notNullValue()))
                .body("email", everyItem(notNullValue()));
    }

    @ParameterizedTest(name = "GET /comments/{0} - returns correct comment")
    @MethodSource("com.kageruka.apitest.comments.CommentDataProvider#validCommentIds")
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify GET /comments/{id} returns correct comment with matching postId")
    public void testGetCommentById(int commentId, int expectedPostId) {
        given()
                .spec(requestSpec)
        .when()
                .get(CommentEndpoint.BASE + "/" + commentId)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("id", equalTo(commentId))
                .body("postId", equalTo(expectedPostId))
                .body("name", notNullValue())
                .body("email", notNullValue())
                .body("body", notNullValue());
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /comments?postId=1 - Filter by query param")
    @Description("Verify filtering comments by postId query parameter returns only matching comments")
    public void testGetCommentsByPostId() {
        given()
                .spec(requestSpec)
                .queryParam("postId", 1)
        .when()
                .get(CommentEndpoint.BASE)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(greaterThan(0)))
                .body("postId", everyItem(equalTo(1)));
    }

    @ParameterizedTest(name = "GET /comments/{0} - returns 404")
    @MethodSource("com.kageruka.apitest.comments.CommentDataProvider#invalidCommentIds")
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /comments/{id} returns 404 for non-existent or invalid IDs")
    public void testGetCommentNotFound(int invalidId) {
        given()
                .spec(requestSpec)
        .when()
                .get(CommentEndpoint.BASE + "/" + invalidId)
        .then()
                .statusCode(Constants.STATUS_NOT_FOUND);
    }

//    Schema validation
    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /comments/1 - Schema validation")
    @Description("Verify response structure matches comment JSON schema contract")
    public void testCommentSchemaValidation() {
        given()
                .spec(requestSpec)
        .when()
                .get(CommentEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .body(matchesJsonSchemaInClasspath("schemas/comment-schema.json"));
    }

//    Post
    @Test
    @Order(6)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /comments - Create new comment")
    @Description("Verify POST /comments creates a new comment and returns 201 with correct data")
    public void testCreateComment() {
        given()
                .spec(requestSpec)
                .body(CommentPayload.create())
        .when()
                .post(CommentEndpoint.BASE)
        .then()
                .statusCode(Constants.STATUS_CREATED)
                .body("name", equalTo("Test Comment"))
                .body("email", equalTo("test@example.com"))
                .body("id", notNullValue());
    }
//    Put
    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /comments/1 - Update comment")
    @Description("Verify PUT /comments/1 updates the comment and returns 200 with updated data")
    public void testUpdateComment() {
        given()
                .spec(requestSpec)
                .body(CommentPayload.update())
        .when()
                .put(CommentEndpoint.BASE + "/1")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("name", equalTo("Updated Comment"))
                .body("email", equalTo("updated@example.com"))
                .body("id", equalTo(1));
    }

//    Delete
    @Test
    @Order(8)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /comments/1 - Delete comment")
    @Description("Verify DELETE /comments/1 returns 200 confirming deletion")
    public void testDeleteComment() {
        given()
                .spec(requestSpec)
        .when()
                .delete(CommentEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK);
    }

//    Headers & Performance
    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /comments/1 - Verify headers and response time")
    @Description("Verify Content-Type header is JSON and response time is under threshold")
    public void testCommentHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(CommentEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .header("Content-Type", containsString("application/json"))
                .time(lessThan(Constants.MAX_RESPONSE_TIME_MS));
    }
}
