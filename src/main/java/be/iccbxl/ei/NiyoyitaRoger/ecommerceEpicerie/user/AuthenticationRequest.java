package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

public class AuthenticationRequest {
    private String username;
    private String password;

    // Constructeurs
    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters et Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
