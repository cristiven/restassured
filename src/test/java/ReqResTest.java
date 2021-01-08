import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.path.json.JsonPath.from;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReqResTest extends BaseTest {


    @Test
    public void loginTest(){
        given()
        //.log().all()
        //.contentType(ContentType.JSON)
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
        .get("users/2")
        .then()
        //.log().all()
        //.statusCode(200)
        .statusCode(HttpStatus.SC_OK)
        .body("data.id", equalTo(2));
    }

    @Test
    public void deleteUserTest(){
        given()
                //.log().all()
                .delete("users/2")
                .then()
                //.log().all()
                //.statusCode(200)
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    //Para actualizar una o varias propiedades del recurso
    @Test
    public void patchUserTest(){
        String nameUpdate = given()
                //.log().all()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                //.log().all()
                //.statusCode(200)
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("name");

        assertThat(nameUpdate, equalTo("morpheus"));
    }

    @Test
    public void putUserTest(){
        String jobUpdate = given()
                //.log().all()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .put("users/2")
                .then()
                //.log().all()
                //.statusCode(200)
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("job");

        assertThat(jobUpdate, equalTo("zion resident"));
    }

    @Test
    public void getAllUsersTest(){
        Response response = given()
                .get("users?page=2");

        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.getContentType();

        assertThat(statusCode,equalTo(HttpStatus.SC_OK));
        System.out.println("body: "+ body);
        System.out.println("content type: "+ contentType);
        System.out.println("Headers: " + headers.toString());

        System.out.println("------------------");
        System.out.println(headers.get("Content-Type"));
        System.out.println(headers.get("Transfer-Encoding"));

    }

    @Test
    public void getAllUsers(){
        String response = given().when().get("users?page=2").then().extract().body().asString();
        int page = from(response).get("page");
        int totalPage = from(response).get("total_pages");
        int idFirtsUser = from(response).get("data[0].id");

        System.out.println("page: " + page);
        System.out.println("totalPage: " + totalPage);
        System.out.println("idFirtsUser: " + idFirtsUser);

        List<Map> usersWithIdGreaterThan10 = from(response).get("data.findAll {user -> user.id > 10 }");
        String email = usersWithIdGreaterThan10.get(0).get("email").toString();

        List<Map> user = from(response).get("data.findAll {user -> user.id > 10 && user.last_name == 'Howell'}");
        int id = Integer.valueOf(user.get(0).get("id").toString());

    }

    @Test
    public void createUsersTest() {
        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users")
                .then().extract().body().asString();
        User user = from(response).getObject("", User.class);
        System.out.println(user.getId());
        System.out.println(user.getJob());

    }

    @Test
    public void registerUserTest(){
        //Se crea el modelo
        CreateUserRequest user = new CreateUserRequest();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("piston");

        CreateUserResponse userResponse = given()
                .when()
                .body(user)
                .post("register")
                .then()
                .spec(defaultResponseSpecification())
                .statusCode(200)
                .contentType(equalTo("application/json; charset=utf-8"))
                .extract()
                .body()
                .as(CreateUserResponse.class);

        assertThat(userResponse.getId(),equalTo(4));
        assertThat(userResponse.getToken(),equalTo("QpwL5tke4Pnpja7X4"));
    }
}
