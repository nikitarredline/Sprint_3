import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class courierPostTest {
    private final String courierLogin = RandomStringUtils.randomAlphabetic(10);
    private final String courierPassword = RandomStringUtils.randomAlphabetic(10);
    private final String courierFirstName = RandomStringUtils.randomAlphabetic(10);

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void registerNewCourierTest() {
        String requestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);

        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login").path("id");

        String deleteBody = "{\"id\":\"" + id + "\"}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(deleteBody)
                .when()
                .delete("/api/v1/courier/" + id);
    }

    @Test
    public void registerExistingCourierLoginTest() {
        scooterRegisterCourier registerCourier = new scooterRegisterCourier();
        ArrayList<String> loginPass = registerCourier.registerNewCourierAndReturnLoginPassword();
        String requestBody = "{\"login\":\"" + loginPass.get(0) + "\","
                + "\"password\":\"" + loginPass.get(1) + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
    }

    @Test
    public void registerWithoutCourierLoginTest() {
        String requestBody = "{\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    public void registerWithoutCourierPasswordTest() {
        String requestBody = "{\"login\":\"" + "courierLogin" + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

}