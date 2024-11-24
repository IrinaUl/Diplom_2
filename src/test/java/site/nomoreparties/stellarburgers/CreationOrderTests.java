package site.nomoreparties.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Link;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.generators.UserGenerator;
import site.nomoreparties.stellarburgers.models.Order;
import site.nomoreparties.stellarburgers.models.User;
import site.nomoreparties.stellarburgers.response.IngredientResponse;
import site.nomoreparties.stellarburgers.response.OrderResponse;
import site.nomoreparties.stellarburgers.response.UserResponse;
import site.nomoreparties.stellarburgers.url.BaseUrl;
import site.nomoreparties.stellarburgers.utils.RandomUtils;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreationOrderTests {
    private final String FAIL_MESSAGE_NO_INGREDIENT = "Ingredient ids must be provided";

    private Order order;
    private UserResponse userResponse;
    private String token;
    private List<String> id;
    private OrderResponse orderResponse;
    private Response response;

    @Before
    public void setUp() {
        new BaseUrl().start();
        IngredientResponse ingredientResponse = new IngredientResponse();
        id = ingredientResponse.getIngredients().jsonPath().getList("data._id");
        User user = UserGenerator.randomUser();
        userResponse = new UserResponse();
        userResponse.create(user);
        token = userResponse.login(user).path("accessToken");
        orderResponse = new OrderResponse();
    }

    @Test
    @DisplayName("Создать заказ с ингредиентами")
    @Description("Проверка создания заказа авторизованным пользователем")
    public void createOrder() {
        order = new Order().withIngredients(List.of(id.get(0), id.get(RandomUtils.randomNumber(1, id.size()))));
        response = orderResponse.create(order, token);

        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        assertEquals("Что-то пошло не так", true, response.path("success"));
    }

    @Test
    @DisplayName("Создать заказ без ингредиентов")
    @Description("Проверка создания заказа без ингредиентов авторизованным пользователем")
    public void createOrderWithoutIngredients() {
        order = new Order().withIngredients(List.of());
        response = orderResponse.create(order, token);

        assertEquals("Неверный статус код", SC_BAD_REQUEST, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
        assertEquals("Описание ответа не соответствует ожидаемому", FAIL_MESSAGE_NO_INGREDIENT, response.path("message"));
    }

    @Test
    @DisplayName("Создать заказ с неверным хешем ингредиентов")
    @Description("Проверка создания заказ с неверным хешем ингредиентов авторизованным пользователем")
    public void createOrderWithInvalidIngredients() {
        order = new Order().withIngredients(List.of(RandomUtils.cyrillic(4)));
        response = orderResponse.create(order, token);
        response.body().print();

        assertEquals("Неверный статус код", SC_INTERNAL_SERVER_ERROR, response.statusCode());
    }

    @Test
    @DisplayName("Создать заказ неавторизованная зона")
    @Description("Проверка создания заказа неавторизованным пользователем")
    @Issue("BUG-TEST") //todo Найден баг "Создается заказ из неавторизованной зоны"
    @Link(name = "Требования", url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf") /*Только авторизованные пользователи могут делать заказы. Структура эндпоинтов
    не меняется, но нужно предоставлять токен при запросе к серверу в поле*/
    public void createOrderUnauthorized() {
        order = new Order().withIngredients(List.of(id.get(0), id.get(RandomUtils.randomNumber(1, id.size()))));
        response = orderResponse.create(order, "");

        assertEquals("Неверный статус код", SC_UNAUTHORIZED, response.statusCode());
        assertEquals("Что-то пошло не так", false, response.path("success"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            userResponse.delete(token);
        }
    }
}
