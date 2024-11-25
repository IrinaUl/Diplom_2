package site.nomoreparties.stellarburgers.response;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientResponse {
    private static final String GET_PATH = "/api/ingredients";

    @Step("Регистрации нового пользователя")
    public Response getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .get(GET_PATH);
    }
}
