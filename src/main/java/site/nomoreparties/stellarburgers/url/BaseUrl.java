package site.nomoreparties.stellarburgers.url;

import io.restassured.RestAssured;

public class BaseUrl {
    public void start() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
}

