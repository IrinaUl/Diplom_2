package site.nomoreparties.stellarburgers;

import io.qameta.allure.Description;
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

public class GetOrderTests {
    private final String FAIL_MESSAGE_UNAUTHORISED = "You should be authorised";

    private UserResponse userResponse;
    private String token;
    private OrderResponse orderResponse;


    @Before
    public void setUp() {
        new BaseUrl().start();
        IngredientResponse ingredientResponse = new IngredientResponse();
        List<String> id = ingredientResponse.getIngredients().jsonPath().getList("data._id");
        User user = UserGenerator.randomUser();
        userResponse = new UserResponse();
        userResponse.create(user);
        token = userResponse.login(user).path("accessToken");
        Order order = new Order().withIngredients(List.of(id.get(0), id.get(RandomUtils.randomNumber(1, id.size()))));
        orderResponse = new OrderResponse();
        orderResponse.create(order, token);
    }

    @Test
    @DisplayName("Получение заказов конкретного авторизованного пользователя")
    @Description("Проверка получения заказов авторизованным пользователем")
    public void getOrder() {
        Response response = orderResponse.get(token);

        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        assertEquals("Что-то пошло не так", true, response.path("success"));
        assertNotNull("Заказы не найдены", response.path("total"));
    }

    @Test
    @DisplayName("Получение заказов конкретного неавторизованного пользователя")
    @Description("Проверка невозможности получения списка заказов неавторизованного пользователя")
    public void getOrderUnauthorized() {
        Response response = orderResponse.get("");

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
