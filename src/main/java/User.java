import java.util.HashSet;
import java.util.Set;

public class User {

    private String userId;
    private String password;
    private Set<User> followers = new HashSet<>();
    private Set<User> following = new HashSet<>();

    private Position lastSeenPosition;

    public User(String userId, String password, Position lastSeenPosition) {
        this.userId = userId;
        this.password = password;
        this.lastSeenPosition = lastSeenPosition;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public boolean isLoggedOn() {
        return true;
    }

    public void receiveTwoot(Twoot twoot) {

    }
}
