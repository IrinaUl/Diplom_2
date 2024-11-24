package site.nomoreparties.stellarburgers.models;

import java.util.List;

public class Order {
    private List<String> ingredients;

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Order withIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}
