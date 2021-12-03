import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrdersPostTests {
    private final String color;

    public OrdersPostTests(String color) {
        this.color = color;
    }

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Parameterized.Parameters
    public static Object[] getColor() {
        return new Object[][] {
                {"BLACK"},
                {"GRAY"},
                {"BLACK, GRAY"},
                {""},
        };
    }

    @Test
    public void ordersCreateTest() {
        String requestBody = "{\"firstName\":\"" + "Naruto" + "\","
                + "\"lastName\":\"" + "Uchiha" + "\","
                + "\"address\":\"" + "Konoha, 142 apt." + "\","
                + "\"metroStation\":\"" + 4 + "\","
                + "\"phone\":\"" + "+7 800 355 35 35" + "\","
                + "\"rentTime\":\"" + 5 + "\","
                + "\"deliveryDate\":\"" + "2020-06-06" + "\","
                + "\"comment\":\"" + "Saske, come back to Konoha" + "\","
                + "\"color\":[\"" + color + "\"]}";

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/api/v1/orders");
        response.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }

}