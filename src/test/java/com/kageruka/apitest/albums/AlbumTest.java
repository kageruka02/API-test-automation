package com.kageruka.apitest.albums;

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

@Feature("Albums")
public class AlbumTest extends BaseTest {

//    GET tests
    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /albums - List all albums")
    @Description("Verify GET /albums returns all 100 albums with valid ids and titles")
    public void testGetAllAlbums() {
        given()
                .spec(requestSpec)
        .when()
                .get(AlbumEndpoint.BASE)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(100))
                .body("id", everyItem(notNullValue()));
    }

    @ParameterizedTest(name = "GET /albums/{0} - returns correct album")
    @MethodSource("com.kageruka.apitest.albums.AlbumDataProvider#validAlbumIds")
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify GET /albums/{id} returns correct album data for valid IDs")
    public void testGetAlbumById(int albumId, int expectedUserId, String expectedTitleStart) {
        given()
                .spec(requestSpec)
        .when()
                .get(AlbumEndpoint.BASE + "/" + albumId)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("id", equalTo(albumId))
                .body("userId", equalTo(expectedUserId))
                .body("title", startsWith(expectedTitleStart));
    }

    @Test @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /albums/1/photos - Get album photos")
    @Description("Verify relationship: albums have many photos, all linked by albumId")
    public void testGetAlbumPhotos() {
        given()
                .spec(requestSpec)
        .when()
                .get(AlbumEndpoint.BASE + "/1/photos")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(greaterThan(0)))
                .body("albumId", everyItem(equalTo(1)));
    }

    @ParameterizedTest(name = "GET /albums/{0} - returns 404")
    @MethodSource("com.kageruka.apitest.albums.AlbumDataProvider#invalidAlbumIds")
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /albums/{id} returns 404 for non-existent or invalid IDs")
    public void testGetAlbumNotFound(int invalidId) {
        given()
                .spec(requestSpec)
        .when()
                .get(AlbumEndpoint.BASE + "/" + invalidId)
        .then()
                .statusCode(Constants.STATUS_NOT_FOUND);
    }

//    Schema validation
    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /albums/1 - Schema validation")
    @Description("Verify response structure matches album JSON schema contract")
    public void testAlbumSchemaValidation() {
        given()
                .spec(requestSpec)
        .when()
                .get(AlbumEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .body(matchesJsonSchemaInClasspath("schemas/album-schema.json"));
    }

//    post
    @Test
    @Order(6)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /albums - Create new album")
    @Description("Verify POST /albums creates a new album and returns 201 with correct data")
    public void testCreateAlbum() {
        given()
                .spec(requestSpec)
                .body(AlbumPayload.create())
        .when()
                .post(AlbumEndpoint.BASE)
        .then()
                .statusCode(Constants.STATUS_CREATED)
                .body("title", equalTo("Test Album Title"))
                .body("userId", equalTo(1))
                .body("id", notNullValue());
    }

//    PUT
    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /albums/1 - Update album")
    @Description("Verify PUT /albums/1 updates the album and returns 200 with updated data")
    public void testUpdateAlbum() {
        given()
                .spec(requestSpec)
                .body(AlbumPayload.update())
        .when()
                .put(AlbumEndpoint.BASE + "/1")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("title", equalTo("Updated Album Title"))
                .body("id", equalTo(1));
    }

//    Delete
    @Test
    @Order(8)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /albums/1 - Delete album")
    @Description("Verify DELETE /albums/1 returns 200 confirming deletion")
    public void testDeleteAlbum() {
        given()
                .spec(requestSpec)
        .when()
                .delete(AlbumEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK);
    }

//    Headers & performance
    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /albums/1 - Verify headers and response time")
    @Description("Verify Content-Type header is JSON and response time is under threshold")
    public void testAlbumHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(AlbumEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .header("Content-Type", containsString("application/json"))
                .time(lessThan(Constants.MAX_RESPONSE_TIME_MS));
    }
}
