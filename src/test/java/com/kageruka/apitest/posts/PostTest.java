package com.kageruka.apitest.posts;

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

@Feature("Posts")
public class PostTest extends BaseTest {

//    GET ALL
    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /posts - List all posts")
    @Description("Verify GET /posts returns all 100 posts")
    public void testGetAllPosts(){
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoint.BASE)
        .then().spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(100))
                .body("id", everyItem(notNullValue()))
                .body("title", everyItem(notNullValue()));
    }

//    GET BY ID(parameterized)
    @ParameterizedTest(name = "GET /posts/{0} - returns correct post")
    @MethodSource("com.kageruka.apitest.posts.PostDataProvider#validPostIds")
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPostById(int postId, int expectedUserId, String expectedTitleStart){
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoint.BASE + "/" + postId)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("id", equalTo(postId))
                .body("userId", equalTo(expectedUserId))
                .body("title", startsWith(expectedTitleStart));
    }

//    GET RELATED: post comments
    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /posts/1/comments - Get comments for post")
    @Description("Verify relationship: posts have many comments")
    public void testGetPostComments() {
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoint.BASE + "/1/comments")
        .then().spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(greaterThan(0)))
                .body("postId", everyItem(equalTo(1)));
     }

//     Not found(parameterized)
    @ParameterizedTest(name = "GET /posts/{0} - returns 404")
    @MethodSource("com.kageruka.apitest.posts.PostDataProvider#invalidPostIds")
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    public void testGetPostNotFound(int invalidId) {
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoint.BASE + "/" + invalidId)
        .then()
                .statusCode(Constants.STATUS_NOT_FOUND);
    }

//    Schema validation
    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /posts/1 - Schema validation")
    @Description("Verify response matches post JSON schema")
    public void testPostSchemaValidation() {
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

//    PUT
    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /posts/1 - Update post")
    @Description("Verify PUT updates an existing post")
    public void testUpdatePost() {
        given()
                .spec(requestSpec)
                .body(PostPayload.update())
        .when()
                .put(PostEndpoint.BASE + "/1")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("title", equalTo("Updated Post Title"))
                .body("id", equalTo(1));
    }

//    Delete
    @Test
    @Order(8)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /posts/1 - Delete post")
    @Description("Verify DELETE returns 200")
    public void testDeletePost() {
        given()
                .spec(requestSpec)
        .when()
                .delete(PostEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK);
    }

//    Headers
    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /posts/1 - Verify headers")
    public void testPostHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .header("Content-Type", containsString("application/json"))
                .time(lessThan(Constants.MAX_RESPONSE_TIME_MS));
    }
}
