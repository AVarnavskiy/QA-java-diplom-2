package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import models.request.CreateUserRequest;
import models.request.UpdateUserDataRequest;
import models.response.CreateUserResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserDataTest extends BaseTest{
    private String userEmail;
    private String userPassword;
    private String userName;
    String token;
    public static final String ENDPOINT = "/api/auth/user";
    public static final String CREATE_USER_ENDPOINT = "/api/auth/register";
    @Before
    public void generateTestData() {
        userEmail = faker.pokemon().name() + faker.number().digits(5) + "@mail.ru";
        userPassword = faker.number().digits(8);
        userName = faker.name().firstName();
        createUser();
    }

    @Test
    @DisplayName("Изменить email пользователя")
    public void editEmailUser() {
        String userEditEmail = faker.animal().name() + faker.number().digits(5) + "@rambler.ru";
        UpdateUserDataRequest updateUser = new UpdateUserDataRequest(userEditEmail, userPassword, userName);
        sendRequestUpdateDataUserWithToken(updateUser);
        response.then()
                .statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(userEditEmail));
    }

    @Test
    @DisplayName("Изменить пароль пользователя")
    public void editPasswordUser() {
        String userEditPassword = faker.number().digits(10);
        UpdateUserDataRequest updateUser = new UpdateUserDataRequest(userEmail, userEditPassword, userName);
        sendRequestUpdateDataUserWithToken(updateUser);
        response.then()
                .statusCode(200)
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменить имя пользователя")
    public void editNameUser() {
        String userEditName = faker.name().firstName();
        UpdateUserDataRequest updateUser = new UpdateUserDataRequest(userEmail, userPassword, userEditName);
        sendRequestUpdateDataUserWithToken(updateUser);
        response.then()
                .statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.name", equalTo(userEditName));
    }

    @Test
    @DisplayName("Изменить email пользователя без передачи токена")
    public void editEmailUserWithoutToken() {
        String userEditEmail = faker.animal().name() + "@rambler.ru";
        UpdateUserDataRequest updateUser = new UpdateUserDataRequest(userEditEmail, userPassword, userName);
        sendRequestUpdateDataUserWithoutToken(updateUser);
        checkUserDataCanNotEdited();
    }

    @Test
    @DisplayName("Изменить пароль пользователя без передачи токена")
    public void editPasswordUserWithoutToken() {
        String userEditPassword = faker.number().digits(10);
        UpdateUserDataRequest updateUser = new UpdateUserDataRequest(userEmail, userEditPassword, userName);
        sendRequestUpdateDataUserWithoutToken(updateUser);
        checkUserDataCanNotEdited();
    }

    @Test
    @DisplayName("Изменить имя пользователя без передачи токена")
    public void editNameUserWithoutToken() {
        String userEditName = faker.name().firstName();
        UpdateUserDataRequest updateUser = new UpdateUserDataRequest(userEmail, userPassword, userEditName);
        sendRequestUpdateDataUserWithoutToken(updateUser);
        checkUserDataCanNotEdited();
    }

    @Step("Отсутствует возможность изменить данные пользователя без передачи токена")
    public void checkUserDataCanNotEdited() {
        response.then()
                .statusCode(401)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("You should be authorised"));
    }

    @Step("Создать уникального пользователя")
    public void createUser() {
        CreateUserRequest user = new CreateUserRequest(userEmail, userPassword, userName);
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(CREATE_USER_ENDPOINT);

        response.then()
                .statusCode(200);

        token = response.as(CreateUserResponse.class).getAccessToken();
    }

    @Step("Отправить запрос на изменение данных пользователя")
    public void sendRequestUpdateDataUserWithToken(UpdateUserDataRequest updateUser) {
        Map<String,Object> headerMap = Map.of("authorization", token);
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .headers(headerMap)
                .body(updateUser)
                .when()
                .patch(ENDPOINT);
    }

    @Step("Отправить запрос на изменение данных пользователя без токена")
    public void sendRequestUpdateDataUserWithoutToken(UpdateUserDataRequest updateUser) {
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updateUser)
                .when()
                .patch(ENDPOINT);
    }
}
