import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ReqResTest {

    @Before
    public void setup(){
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
        // Muestra los log de la ejecución
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
    }

    @Test
    public void loginTest(){
        given()
        //.log().all()
        .contentType(ContentType.JSON)
        .body("{\n" +
                "    \"email\": \"eve.holt@reqres.in\",\n" +
                "    \"password\": \"cityslicka\"\n" +
                "}")
        .post("login")
        .then()
        //.log().all()
        .statusCode(200)
        .body("token", notNullValue());
    }

    @Test
    public void getSingleUserTest(){
        given()
        //.log().all()
        .contentType(ContentType.JSON)
        .get("users/2")
        .then()
        //.log().all()
        .statusCode(200)
        .body("data.id", equalTo(2));
    }
}
