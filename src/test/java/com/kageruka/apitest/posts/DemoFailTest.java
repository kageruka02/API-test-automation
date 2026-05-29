package com.kageruka.apitest.posts;

import com.kageruka.apitest.base.BaseTest;
import com.kageruka.apitest.config.Constants;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@Feature("Posts")
public class DemoFailTest extends BaseTest {

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DEMO - Intentional failure for notification testing")
    @Description("BUG-001 | GET /posts/1 returns 200 instead of expected 404. " + "Severity: Critical | Priority: High | Status: Open. "
            + "Expected: Status code 404 Not Found. "
            + "Actual: Status code 200 OK — resource should not exist. "
            + "Steps: 1) Send GET request to /posts/1. "
            + "2) Observe status code 200 returned. "
            + "3) Expected 404 since resource was previously deleted.")
    public void testDemoFailure() {
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoint.BASE + "/1")
        .then()
                .statusCode(Constants.STATUS_NOT_FOUND);  // ← WILL FAIL: expects 404, gets 200
    }
}

