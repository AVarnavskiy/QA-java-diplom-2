package models.response;

public class CreateUserResponse {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;

    public CreateUserResponse() {

    }

    public String getAccessToken() {
        return accessToken;
    }
}
