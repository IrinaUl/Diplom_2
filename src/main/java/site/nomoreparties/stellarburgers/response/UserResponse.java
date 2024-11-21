package site.nomoreparties.stellarburgers.response;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.models.User;

import static io.restassured.RestAssured.given;

public class UserResponse {
    public static final String CREATE_PATH = "/api/auth/register";
    public static final String LOGIN_PATH = "/api/auth/login";
    private static final String DELETE_PATH = "/api/auth/user";

    @Step("Регистрации нового пользователя")
    public Response create(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(CREATE_PATH);
    }

    @Step("Логин пользователя")
    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(LOGIN_PATH);
    }

    @Step("Удаление пользователя")
    public Response delete(String token) {
        return given()
                .header("Authorization", token)
                .delete(DELETE_PATH);
    }
}
