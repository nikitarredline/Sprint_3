import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginPostTests {
    scooterRegisterCourier registerCourier = new scooterRegisterCourier();
    ArrayList<String> loginPass = registerCourier.registerNewCourierAndReturnLoginPassword();
    private final String courierLogin = RandomStringUtils.randomAlphabetic(10);
    private final String courierPassword = RandomStringUtils.randomAlphabetic(10);

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void loginCorrectTest() {
        String requestBody = "{\"login\":\"" + loginPass.get(0) + "\","
                + "\"password\":\"" + loginPass.get(1) + "\"}";

        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login").path("id");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("id", equalTo(id))
                .and()
                .statusCode(200);

        String deleteBody = "{\"id\":\"" + id + "\"}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(deleteBody)
                .when()
                .delete("/api/v1/courier/" + id);
    }

    @Test
    public void loginWithoutLoginTest() {
        String requestBody = "{\"password\":\"" + courierPassword + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    public void loginWithoutPasswordTest() {
        String requestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + "" + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    public void loginWithIncorrectLoginPasswordTest() {
        String incorrectLogin = "qwertyuiop";
        String incorrectPassword = "1234567890";
        String requestBody = "{\"login\":\"" + incorrectLogin + "\","
                + "\"password\":\"" + incorrectPassword + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

}