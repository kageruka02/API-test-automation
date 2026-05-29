package com.kageruka.apitest.todos;

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

@Feature("Todos")
public class TodoTest extends BaseTest {

//    GET
    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /todos - List all todos")
    @Description("Verify GET /todos returns all 200 todos with valid data")
    public void testGetAllTodos() {
        given()
                .spec(requestSpec)
        .when()
                .get(TodoEndpoint.BASE)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(200))
                .body("id", everyItem(notNullValue()))
                .body("title", everyItem(notNullValue()));
    }

    @ParameterizedTest(name = "GET /todos/{0} - returns correct todo")
    @MethodSource("com.kageruka.apitest.todos.TodoDataProvider#validTodoIds")
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify GET /todos/{id} returns correct todo with matching userId and status")
    public void testGetTodoById(int todoId, int expectedUserId, boolean expectedCompleted) {
        given()
                .spec(requestSpec)
        .when()
                .get(TodoEndpoint.BASE + "/" + todoId)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("id", equalTo(todoId))
                .body("userId", equalTo(expectedUserId))
                .body("completed", equalTo(expectedCompleted));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /todos?userId=1 - Filter by userId")
    @Description("Verify filtering todos by userId query parameter returns only matching todos")
    public void testGetTodosByUserId() {
        given()
                .spec(requestSpec)
                .queryParam("userId", 1)
        .when()
                .get(TodoEndpoint.BASE)
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("$", hasSize(greaterThan(0)))
                .body("userId", everyItem(equalTo(1)));
    }

    @ParameterizedTest(name = "GET /todos/{0} - returns 404")
    @MethodSource("com.kageruka.apitest.todos.TodoDataProvider#invalidTodoIds")
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /todos/{id} returns 404 for non-existent or invalid IDs")
    public void testGetTodoNotFound(int invalidId) {
        given()
                .spec(requestSpec)
        .when()
                .get(TodoEndpoint.BASE + "/" + invalidId)
        .then()
                .statusCode(Constants.STATUS_NOT_FOUND);
    }

//    Schema validation
    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /todos/1 - Schema validation")
    @Description("Verify response structure matches todo JSON schema contract")
    public void testTodoSchemaValidation() {
        given()
                .spec(requestSpec)
        .when()
                .get(TodoEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .body(matchesJsonSchemaInClasspath("schemas/todo-schema.json"));
    }

//    Post
    @Test
    @Order(6)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /todos - Create new todo")
    @Description("Verify POST /todos creates a new todo and returns 201 with correct data")
    public void testCreateTodo() {
        given()
                .spec(requestSpec)
                .body(TodoPayload.create())
        .when()
                .post(TodoEndpoint.BASE)
        .then()
                .statusCode(Constants.STATUS_CREATED)
                .body("title", equalTo("Test Todo Item"))
                .body("completed", equalTo(false))
                .body("id", notNullValue());
    }

//    Put
    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /todos/1 - Update todo")
    @Description("Verify PUT /todos/1 updates the todo and returns 200 with updated data")
    public void testUpdateTodo() {
        given()
                .spec(requestSpec)
                .body(TodoPayload.update())
        .when()
                .put(TodoEndpoint.BASE + "/1")
        .then()
                .spec(responseSpec)
                .statusCode(Constants.STATUS_OK)
                .body("title", equalTo("Updated Todo Item"))
                .body("completed", equalTo(true))
                .body("id", equalTo(1));
    }

//    Delete
    @Test
    @Order(8)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /todos/1 - Delete todo")
    @Description("Verify DELETE /todos/1 returns 200 confirming deletion")
    public void testDeleteTodo() {
        given()
                .spec(requestSpec)
        .when()
                .delete(TodoEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK);
    }

//    Header & Performance
    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /todos/1 - Verify headers and response time")
    @Description("Verify Content-Type header is JSON and response time is under threshold")
    public void testTodoHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(TodoEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_OK)
                .header("Content-Type", containsString("application/json"))
                .time(lessThan(Constants.MAX_RESPONSE_TIME_MS));
    }
}
