import java.util.HashSet;
import java.util.Set;

public class User {

    private String userId;
    private String password;
    private Set<User> followers = new HashSet<>();
    private Set<User> following = new HashSet<>();
}
