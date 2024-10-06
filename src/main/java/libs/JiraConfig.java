package libs;

public class JiraConfig {
    private String url;
    private String username;
    private String apiToken;

    public JiraConfig() {
    }

    // Getters
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getApiToken() {
        return apiToken;
    }

    //Setters
    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}