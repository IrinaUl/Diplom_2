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
import site.nomoreparties.stellarburgers.utils.RandomUtils;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class UpdateUserTests {
    private final String FAIL_MESSAGE_UNAUTHORISED = "You should be authorised";

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
    @DisplayName("Изменение имени и логина пользователя")
    @Description("Проверка изменения данных авторизованного пользователя")
    public void updateAllInformationUser() {
        user.setEmail(RandomUtils.randomEmail(7));
        user.setName(RandomUtils.cyrillic(5));
        response = userResponse.update(user, token);
        String information = response.path("user").toString();
        System.out.printf(information);

        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        assertEquals("Что-то пошло не так", true, response.path("success"));
        assertTrue("Неверное имя", information.contains(user.getName()));
        assertTrue("Неверный логин", information.contains(user.getEmail()));
    }

    @Test
    @DisplayName("Изменение пароля пользователя")
    @Description("Проверка изменения пароля авторизованного пользователя")
    public void updatePasswordUser() {
        user.setPassword(RandomUtils.randomPassword(7));
        response = userResponse.update(user, token);

        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        assertEquals("Что-то пошло не так", true, response.path("success"));
        response = userResponse.login(user);
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
    }

    @Test
    @DisplayName("Изменение имени неавторизованного пользователя")
    @Description("Проверка изменения пароля неавторизованного пользователя")
    public void updateNamedUserUnauthorized() {
        user.setPassword(RandomUtils.randomPassword(7));
        response = userResponse.update(user, "");

        assertEquals("Неверный статус код", SC_UNAUTHORIZED, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
        assertEquals("Описание ответа не соответствует ожидаемому", FAIL_MESSAGE_UNAUTHORISED, response.path("message"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            userResponse.delete(token);
        }
    }
}
