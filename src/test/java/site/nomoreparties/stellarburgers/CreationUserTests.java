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

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class CreationUserTests {
    private User user;
    private UserResponse userResponse;
    private String token;

    @Before
    public void setUp() {
        new BaseUrl().start();
        userResponse = new UserResponse();
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    @Description("Проверка регистрации нового пользователя на платформе")
    public void createUser() {
        user = UserGenerator.randomUser();
        Response response = userResponse.create(user);
        token = userResponse.login(user).path("accessToken");

        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        assertEquals("Что-то пошло не так", true, response.path("success"));
        assertEquals("Имя пользователя не совпадает", user.getName(), response.path("user.name"));
        assertEquals("Email не совпадает", user.getEmail(), response.path("user.email"));
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить поле логина")
    @Description("Проверка невозможности регистрации нового пользователя без заполнения email")
    public void createUserWithoutEmail() {
        user = new User()
                .withPassword(RandomUtils.randomPassword(10))
                .withName(RandomUtils.cyrillic(10));
        Response response = userResponse.create(user);

        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
        assertEquals("Описание ответа не соответствует ожидаемому", "Email, password and name are required fields", response.path("message"));
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить поле пароля")
    @Description("Проверка невозможности регистрации нового пользователя без заполнения пароля")
    public void createUserWithoutPassword() {
        user = new User()
                .withEmail(RandomUtils.randomEmail(10))
                .withName(RandomUtils.cyrillic(10));
        Response response = userResponse.create(user);

        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
        assertEquals("Описание ответа не соответствует ожидаемому", "Email, password and name are required fields", response.path("message"));
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить поле имени")
    @Description("Проверка невозможности регистрации нового пользователя без заполнения имени")
    public void createUserWithoutName() {
        user = new User()
                .withEmail(RandomUtils.randomEmail(10))
                .withPassword(RandomUtils.randomPassword(10));
        Response response = userResponse.create(user);

        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
        assertEquals("Описание ответа не соответствует ожидаемому", "Email, password and name are required fields", response.path("message"));
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    @Description("Проверка невозможности повторной регистрации пользователя на платформе")
    public void createUserWithDuplicateLogin() {
        user = UserGenerator.randomUser();
        userResponse.create(user);
        token = userResponse.login(user).path("accessToken");
        Response response = userResponse.create(user);

        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
        assertEquals("Описание ответа не соответствует ожидаемому", "User already exists", response.path("message"));
    }

    @After
    public void tearDown() {
        if (token != null) {userResponse.delete(token);}
    }
}
