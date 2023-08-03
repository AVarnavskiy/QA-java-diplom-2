package helpers;

import io.restassured.http.ContentType;
import models.request.DeleteUserRequest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class DeleteUser {
    public static final String ENDPOINT = "/api/auth/user";

    public static void deleteUser(String userEmail, String userPassword, String userName, String token) {
        DeleteUserRequest user = new DeleteUserRequest(userEmail, userPassword, userName);

        Map<String,Object> headerMap = Map.of("authorization", token);

        given()
            .log().all()
            .contentType(ContentType.JSON)
            .headers(headerMap)
            .body(user)
            .when()
            .delete(ENDPOINT)
            .then()
            .statusCode(202)
            .assertThat().body("message", equalTo("User successfully removed"));

        System.out.println("Пользователь успешно удален");
    }
}
