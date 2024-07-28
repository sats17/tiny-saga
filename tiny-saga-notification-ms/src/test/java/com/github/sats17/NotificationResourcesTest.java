package com.github.sats17;

import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class NotificationResourcesTest {

    @Test
    public void testPushNotificationSuccess() {
        String requestBody = "{\"orderId\":\"123\",\"productId\":\"1413241\",\"notificationMessage\":\"Hi your order is placed\",\"notificationType\":[\"SMS\"]}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post("/v1/api/notification/push/{userId}", 1)
            .then()
            .statusCode(200)
            .body("status", is(200))
            .body("responseMessage", is("Notification sent successfully"))
            .body("serviceName", is("Notification MS"));
    }
}
