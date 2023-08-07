package tests;

import helpers.DeleteUser;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import models.request.CreateOrderRequest;
import models.request.CreateUserRequest;
import models.response.CreateUserResponse;
import org.junit.After;
import org.junit.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetUserOrderTest extends BaseTest {
    private String userEmail;
    private String userPassword;
    private String userName;
    String token;
    public static final String ENDPOINT = "/api/orders";
    public static final String CREATE_USER_ENDPOINT = "/api/auth/register";

    @Test
    @DisplayName("Получить список заказов пользователя с авторизацией")
    public void getOrderListAuthorizedUser() {
        generateUserData();
        createUser();
        createOrder();
        sendRequestGetUserOrdersWithAuthorization();
        response.then()
                .statusCode(200)
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получить список заказов без авторизации")
    public void getOrderListWithoutAuthorization() {
        sendRequestGetUserOrdersWithoutAuthorization();
        response.then()
                .statusCode(401)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("You should be authorised"));
    }

    @Step("Отправить запрос на получение заказов пользователя с авторизацией")
    public void sendRequestGetUserOrdersWithAuthorization() {
        Map<String,Object> headerMap = Map.of("authorization", token);
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .headers(headerMap)
                .when()
                .get(ENDPOINT);
    }

    @Step("Отправить запрос на получение заказов без авторизации")
    public void sendRequestGetUserOrdersWithoutAuthorization() {
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get(ENDPOINT);
    }

    @Step("Создать заказ на пользователя")
    public void createOrder() {
        CreateOrderRequest order = new CreateOrderRequest(getIngredients());
        Map<String,Object> headerMap = Map.of("authorization", token);
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .headers(headerMap)
                .body(order)
                .when()
                .post(ENDPOINT);
    }

    @Step("Создать уникального пользователя")
    public void createUser() {
        generateUserData();
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

    @Step("Сгенерировать данные пользователя")
    public void generateUserData() {
        userEmail = faker.pokemon().name() + faker.number().digits(4) + "@mail.ru";
        userPassword = faker.number().digits(8);
        userName = faker.name().firstName();
    }

    private String[] getIngredients() {
        return new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72"};
    }

    @After
    public void deleteUser() {
        if (token != null) {
            DeleteUser.deleteUser(userEmail, userPassword, userName, token);
        } else {
            System.out.println("Пользователь в рамках теста не был создан. Удаление не требуется");
        }
    }
}
