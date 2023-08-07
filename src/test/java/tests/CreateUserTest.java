package tests;

import helpers.DeleteUser;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import models.request.CreateUserRequest;
import models.response.CreateUserResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest extends BaseTest {
    private String userEmail;
    private String userPassword;
    private String userName;
    public static final String ENDPOINT = "/api/auth/register";

    @Before
    public void generateTestData() {
        userEmail = faker.pokemon().name() + faker.number().digits(5) + "@mail.ru";
        userPassword = faker.number().digits(8);
        userName = faker.name().firstName();
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    public void createUser() {
        CreateUserRequest user = new CreateUserRequest(userEmail, userPassword, userName);
        sendRequestCreateUser(user);
        response.then()
                .statusCode(200)
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать пользователя, который уже зарегистрирован")
    public void createExistingUser() {
        CreateUserRequest user = new CreateUserRequest(userEmail, userPassword, userName);
        sendRequestCreateUser(user);
        response.then()
                .statusCode(200);

        sendRequestCreateUser(user);
        response.then()
                .statusCode(403)
                .assertThat().body("message", equalTo("User already exists"));
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

    @After
    public void deleteUser() {
        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            String token = response.as(CreateUserResponse.class).getAccessToken();
            DeleteUser.deleteUser(userEmail, userPassword, userName, token);
        } else {
            System.out.println("Пользователь в рамках теста не был создан. Удаление не требуется");
        }
    }
}
