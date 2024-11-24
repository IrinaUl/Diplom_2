package site.nomoreparties.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.generators.UserGenerator;
import site.nomoreparties.stellarburgers.models.User;
import site.nomoreparties.stellarburgers.response.UserResponse;
import site.nomoreparties.stellarburgers.url.BaseUrl;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class LoginUserTests {
    private final String FAIL_MESSAGE_INVALID = "email or password are incorrect";

    private User user;
    private UserResponse userResponse;
    private String token;
    private Response response;

    @Before
    public void setUp() {
        new BaseUrl().start();
        user = UserGenerator.randomUser();
        userResponse = new UserResponse();
        response = userResponse.create(user);
        token = userResponse.login(user).path("accessToken");
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка авторизации существующего пользователя на платформе")
    public void loginUser() {
        response = userResponse.login(user);
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        assertEquals("Что-то пошло не так", true, response.path("success"));
        assertNotNull("Отсутствует токен", response.path("accessToken"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка авторизации с неверным паролем существующего пользователя")
    public void loginWithoutPassword() {
        user.setPassword("");
        response = userResponse.login(user);

        assertEquals("Неверный статус код", SC_UNAUTHORIZED, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
        assertEquals("Описание ответа не соответствует ожидаемому", FAIL_MESSAGE_INVALID, response.path("message"));
    }

    @Test
    @DisplayName("Логин с неверным логином")
    @Description("Проверка авторизации с неверным логином существующего пользователя")
    public void loginWithoutLogin() {
        user.setEmail("");
        response = userResponse.login(user);

        assertEquals("Неверный статус код", SC_UNAUTHORIZED, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
        assertEquals("Описание ответа не соответствует ожидаемому", FAIL_MESSAGE_INVALID, response.path("message"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            userResponse.delete(token);
        }
    }
}
