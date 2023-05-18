package com.baeldung.lss;

import com.baeldung.lss.spring.LssApp;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This Test class relies on HSQL DB and all integration tests in this class are run against
 * in-memory HSQL DB instead of MySQL DB.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LssApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LssCheckAuthorizationIntegrationTest {

    private static String APP_ROOT = "http://localhost:8081";
    private final FormAuthConfig formAuthConfig = new FormAuthConfig("/doLogin", "username", "password");

    @Test
    public void givenAdminUser_whenViewUserInfo_thenOK() {
        final Response response = givenAuth("test@email.com", "pass")
                .get(APP_ROOT + "/user/1");
        assertEquals(200, response.getStatusCode());
        assertThat(response.body()
                .htmlPath()
                .getString("html.body.div.div.div.ul[2].li[0].a.span")).isEqualTo("test@email.com");
    }

    @Test
    public void givenNormalUser_whenViewUserInfo_thenForbidden() {
        final Response response = givenAuth("user@email.com", "pass").get(APP_ROOT + "/user/2");
        assertEquals(403, response.getStatusCode());
    }


    @Test
    public void givenAdminUser_whenAccessCreateUserForm_thenOK() {
        final Response response = givenAuth("test@email.com", "pass").get(APP_ROOT + "/user?form");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void givenNormalUser_whenAccessCreateUserForm_thenForbidden() {
        final Response response = givenAuth("user@email.com", "pass").get(APP_ROOT + "/user?form");
        assertEquals(403, response.getStatusCode());
    }

    private RequestSpecification givenAuth(String username, String password) {
        return RestAssured.given()
                .auth()
                .form(username, password, formAuthConfig);
    }
}