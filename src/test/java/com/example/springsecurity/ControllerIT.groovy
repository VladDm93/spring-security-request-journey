package com.example.springsecurity

import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import org.apache.http.HttpStatus
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

import static io.restassured.RestAssured.given
import static io.restassured.RestAssured.when
import static org.hamcrest.core.IsEqual.equalTo

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = [
        "security.user.password=pass",
        "security.enable-csrf=true",
        "security.sessions=if_required"
])
class ControllerIT {

    @LocalServerPort
    private int serverPort;

    @Before
    void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.filters(new ResponseLoggingFilter());
        RestAssured.filters(new RequestLoggingFilter());
    }

    @Test
    void 'api call without authentication must fail'() {
        when()
            .get("/")
        .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    void 'api call with authentication must succeed'() {
        given()
            .auth().preemptive().basic("user", "pass")
        .when()
            .get("/")
        .then()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 'POST without CSRF token must return 403'() {
        given()
            .auth().basic("user", "pass")
        .when()
            .post("/post")
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    void 'get css/hello must succeed'() {
        when()
            .get("css/hello")
        .then()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 'passed x-custom-header must be returned'() {
        def sessionCookie = given()
                .header("x-custom-header", "hello")
        .when()
            .get("customHeader")
        .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED)
            .extract().cookie("JSESSIONID")

        given()
            .auth().basic("user", "pass")
            .cookie("JSESSIONID", sessionCookie)
        .when()
            .get("customHeader")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(equalTo("hello"))
    }

    @Test
    void 'JSESSIONID must be changed after login'() {
        def sessionCookie = when()
            .get("/")
        .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED)
            .extract().cookie("JSESSIONID")

        def newCookie = given()
            .auth().basic("user", "pass")
            .cookie("JSESSIONID", sessionCookie)
        .when()
            .get("/")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .extract().cookie("JSESSIONID")

        Assert.assertNotEquals(sessionCookie, newCookie)
    }
}
