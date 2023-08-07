package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import models.request.CreateUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserParameterizedTest extends BaseTest {
    private String email;
    private String password;
    private String name;
    public static final String ENDPOINT = "/api/auth/register";

    public CreateUserParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][] {
                {  "test@mail.ru", "qwerty123", null },
                {  "test@mail.ru", null, "UserTest" },
                {  null, "qwerty123", "UserTest" },
        };
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей")
    public void createUserWithoutOneRequiredField() {
        CreateUserRequest user = new CreateUserRequest(email, password, name);
        sendRequestCreateUser(user);
        response.then()
                .statusCode(403)
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Отправить запрос на создание пользователя")
    public void sendRequestCreateUser(CreateUserRequest user) {
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(ENDPOINT);
    }
}
