import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class courierDeleteTest {
    scooterRegisterCourier registerCourier = new scooterRegisterCourier();
    ArrayList<String> loginPass = registerCourier.registerNewCourierAndReturnLoginPassword();

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void deleteCourierTest() {
        String requestBody = "{\"login\":\"" + loginPass.get(0) + "\","
                + "\"password\":\"" + loginPass.get(1) + "\"}";

        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login").path("id");

        String deleteBody = "{\"id\":\"" + id + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(deleteBody)
                .when()
                .delete("/api/v1/courier/" + id);
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    public void deleteCourierWithoutIdTest() {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body("")
                .when()
                .delete("/api/v1/courier/");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для удаления курьера"))
                .and()
                .statusCode(400);
    }

    @Test
    public void deleteCourierIncorrectIdTest() {
        int id = 0;
        String requestBody = "{\"id\":\"" + id + "\"}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .delete("/api/v1/courier/" + id);
        response.then().assertThat().body("message", equalTo("Курьера с таким id нет"))
                .and()
                .statusCode(404);
    }
}
