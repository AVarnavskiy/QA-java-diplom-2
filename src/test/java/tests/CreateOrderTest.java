package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import models.request.CreateOrderRequest;
import models.request.CreateUserRequest;
import models.response.CreateUserResponse;
import org.junit.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateOrderTest extends BaseTest {

    private String userEmail;
    private String userPassword;
    private String userName;
    String token;
    public static final String CREATE_USER_ENDPOINT = "/api/auth/register";
    public static final String ENDPOINT = "/api/orders";

    @Test
    @DisplayName("Создать заказ c авторизацией")
    public void createOrderWithAuthorization() {
        createUser();
        CreateOrderRequest order = new CreateOrderRequest(getIngredients());
        sendRequestCreateOrderWithAuthorization(order);
        response.then()
                .statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("order.owner.name", equalTo(userName));
    }

    @Test
    @DisplayName("Создать заказ без авторизации")
    public void createOrderWithoutAuthorization() {
        CreateOrderRequest order = new CreateOrderRequest(getIngredients());
        sendRequestCreateOrderWithoutAuthorization(order);
        response.then()
                .statusCode(200)
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создать заказ без ингредиентов")
    public void createOrderWithoutIngredients() {
        String[] ingredients = {};
        CreateOrderRequest order = new CreateOrderRequest(ingredients);
        sendRequestCreateOrderWithoutAuthorization(order);
        response.then()
                .statusCode(400)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создать заказ с неверным массивом ингредиентов")
    public void createOrderWithInvalidIngredients() {
        String[] ingredients = {"123", "A345"};
        CreateOrderRequest order = new CreateOrderRequest(ingredients);
        sendRequestCreateOrderWithoutAuthorization(order);
        response.then()
                .statusCode(500);
    }
    @Step("Отправить запрос на создание заказа без авторизации")
    public void sendRequestCreateOrderWithoutAuthorization(CreateOrderRequest order) {
        response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(ENDPOINT);
    }

    @Step("Отправить запрос на создание заказа с авторизацией")
    public void sendRequestCreateOrderWithAuthorization(CreateOrderRequest order) {
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

}
