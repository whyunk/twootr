import java.util.HashSet;
import java.util.Set;

public class User {

    private String userId;
    private String password;
    private Set<User> followers = new HashSet<>();
    private Set<User> following = new HashSet<>();

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
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
