package site.nomoreparties.stellarburgers.response;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.models.Order;

import static io.restassured.RestAssured.given;

public class OrderResponse {
    private static final String CREATE_PATH = "/api/orders";
    private static final String GET_PATH = "/api/orders";

    @Step("Создание нового заказа")
    public Response create(Order order, String token) {
        return given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .body(order)
                .post(CREATE_PATH);
    }

    @Step("Получение списка заказов пользователя")
    public Response get(String token) {
        return given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .get(GET_PATH);
    }
}
