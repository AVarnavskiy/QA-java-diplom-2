package models.request;

public class CreateOrderRequest {
    private String[] ingredients;

    public CreateOrderRequest() {

    }

    public CreateOrderRequest(String[] ingredients) {
        this.ingredients = ingredients;
    }

}
