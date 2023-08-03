package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import models.request.CreateUserRequest;
import models.request.LoginUserRequest;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest extends BaseTest {
    private String userEmail;
    private String userPassword;
    private String userName;
    public static final String ENDPOINT = "/api/auth/login";
    public static final String CREATE_USER_ENDPOINT = "/api/auth/register";

    @Before
    public void generateTestData() {
        userEmail = faker.pokemon().name() + faker.number().digits(5) + "@mail.ru";
        userPassword = faker.number().digits(8);
        userName = faker.name().firstName();
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void login() {
        createUser();
        LoginUserRequest user = new LoginUserRequest(userEmail, userPassword, userName);
        sendRequestLoginUser(user);
        response.then()
                .statusCode(200)
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин с неверным email и паролем")
    public void loginWithInvalidEmailAndPassword() {
        createUser();
        userEmail = faker.country().name() + "@gmail.com";
        userPassword = faker.number().digits(10);
        LoginUserRequest user = new LoginUserRequest(userEmail, userPassword, userName);
        sendRequestLoginUser(user);
        response.then()
                .statusCode(401)
                .assertThat().body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Создать уникального пользователя")
    public void createUser() {
        CreateUserRequest user = new CreateUserRequest(userEmail, userPassword, userName);
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .statusCode(200);
    }

    @Step("Отправить запрос на авторизацию пользователя")
    public void sendRequestLoginUser(LoginUserRequest user) {
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(ENDPOINT);
    }
}
