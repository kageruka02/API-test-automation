package com.kageruka.apitest.photos;

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

@Feature("Photos")
public class PhotoTest extends BaseTest {

//    GET
    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /photos - List photos with limit")
    @Description("Verify GET /photos?_limit=10 returns exactly 10 photos with valid data")
    public void testGetAllPhotos() {
        given()
                .spec(requestSpec)
                .queryParam("_limit", 10)
        .when()
                .get(PhotoEndpoint.BASE)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(10))
                .body("id", everyItem(notNullValue()))
                .body("title", everyItem(notNullValue()));
    }

    @ParameterizedTest(name = "GET /photos/{0} - returns correct photo")
    @MethodSource("com.kageruka.apitest.photos.PhotoDataProvider#validPhotoIds")
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify GET /photos/{id} returns correct photo with matching albumId")
    public void testGetPhotoById(int photoId, int expectedAlbumId) {
        given()
                .spec(requestSpec)
        .when()
                .get(PhotoEndpoint.BASE + "/" + photoId)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("id", equalTo(photoId))
                .body("albumId", equalTo(expectedAlbumId))
                .body("title", notNullValue())
                .body("url", notNullValue())
                .body("thumbnailUrl", notNullValue());
    }

    @ParameterizedTest(name = "GET /photos/{0} - returns 404")
    @MethodSource("com.kageruka.apitest.photos.PhotoDataProvider#invalidPhotoIds")
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /photos/{id} returns 404 for non-existent or invalid IDs")
    public void testGetPhotoNotFound(int invalidId) {
        given()
                .spec(requestSpec)
        .when()
                .get(PhotoEndpoint.BASE + "/" + invalidId)
        .then()
                .statusCode(Constants.STATUS_NOT_FOUND);
    }

//    Schema validation
    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /photos/1 - Schema validation")
    @Description("Verify response structure matches photo JSON schema contract")
    public void testPhotoSchemaValidation() {
        given()
                .spec(requestSpec)
        .when()
                .get(PhotoEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .body(matchesJsonSchemaInClasspath("schemas/photo-schema.json"));
    }

//    POST
    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /photos - Create new photo")
    @Description("Verify POST /photos creates a new photo and returns 201 with correct data")
    public void testCreatePhoto() {
        given()
                .spec(requestSpec)
                .body(PhotoPayload.create())
        .when()
                .post(PhotoEndpoint.BASE)
        .then()
                .statusCode(Constants.STATUS_CREATED)
                .body("title", equalTo("Test Photo Title"))
                .body("albumId", equalTo(1))
                .body("id", notNullValue());
    }

//    PUT
    @Test
    @Order(6)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /photos/1 - Update photo")
    @Description("Verify PUT /photos/1 updates the photo and returns 200 with updated data")
    public void testUpdatePhoto() {
        given()
                .spec(requestSpec)
                .body(PhotoPayload.update())
        .when()
                .put(PhotoEndpoint.BASE + "/1")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("title", equalTo("Updated Photo Title"))
                .body("id", equalTo(1));
    }

//    Delete
    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /photos/1 - Delete photo")
    @Description("Verify DELETE /photos/1 returns 200 confirming deletion")
    public void testDeletePhoto() {
        given()
                .spec(requestSpec)
        .when()
                .delete(PhotoEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK);
    }

//    Header & Performance
    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /photos/1 - Verify headers and response time")
    @Description("Verify Content-Type header is JSON and response time is under threshold")
    public void testPhotoHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(PhotoEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .header("Content-Type", containsString("application/json"))
                .time(lessThan(Constants.MAX_RESPONSE_TIME_MS));
    }
}
