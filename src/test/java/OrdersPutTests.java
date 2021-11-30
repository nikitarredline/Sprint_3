import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrdersPutTests {
    scooterRegisterCourier registerCourier = new scooterRegisterCourier();
    ArrayList<String> loginPass = registerCourier.registerNewCourierAndReturnLoginPassword();

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void acceptOrderTest() {
        String requestBody = "{\"login\":\"" + loginPass.get(0) + "\","
                + "\"password\":\"" + loginPass.get(1) + "\"}";

        int courierId = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login").path("id");

        ArrayList orderId = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders").path("orders.id");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/" + orderId.get(0));
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(200);

        String deleteBody = "{\"id\":\"" + courierId + "\"}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(deleteBody)
                .when()
                .delete("/api/v1/courier/" + courierId);
    }

    @Test
    public void acceptOrderWithoutCourierIdTest() {
        ArrayList orderId = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders").path("orders.id");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .put("/api/v1/orders/accept/" + orderId.get(0));
        response.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
    }

    @Test
    public void acceptOrderIncorrectCourierIdTest() {
        ArrayList orderId = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders").path("orders.id");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .queryParam("courierId", 999999999)
                .put("/api/v1/orders/accept/" + orderId.get(0));
        response.then().assertThat().body("message", equalTo("Курьера с таким id не существует"))
                .and()
                .statusCode(404);
    }

    @Test
    public void acceptOrderIncorrectNumberTest() {
        String requestBody = "{\"login\":\"" + loginPass.get(0) + "\","
                + "\"password\":\"" + loginPass.get(1) + "\"}";

        int courierId = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login").path("id");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/999999999");
        response.then().assertThat().body("message", equalTo("Заказа с таким id не существует"))
                .and()
                .statusCode(404);

        String deleteBody = "{\"id\":\"" + courierId + "\"}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(deleteBody)
                .when()
                .delete("/api/v1/courier/" + courierId);
    }
}