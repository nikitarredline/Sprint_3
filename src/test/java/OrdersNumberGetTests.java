import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrdersNumberGetTests {

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void orderGetterNumberTest() {
        ArrayList track = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders").path("orders.track");

        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .queryParam("t", track.get(0))
                .get("/api/v1/orders/track");
        response.then().assertThat()
                .statusCode(200);
    }

    @Test
    public void orderGetterWithoutNumberTest() {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders/track");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
    }

    @Test
    public void orderGetterIncorrectNumberTest() {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .queryParam("t", 999999999)
                .get("/api/v1/orders/track");
        response.then().assertThat().body("message", equalTo("Заказ не найден"))
                .and()
                .statusCode(404);
    }
}