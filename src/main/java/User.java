import java.util.HashSet;
import java.util.Set;

public class User {

    private String userId;
    private String password;
    private Set<User> followers = new HashSet<>();
    private Set<User> following = new HashSet<>();
    private ReceiverEndPoint receiverEndPoint;
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

    public Set<User> getFollowing() {
        return following;
    }

    public Position getLastSeenPosition() {
        return lastSeenPosition;
    }

    public boolean isLoggedOn() {
        return receiverEndPoint != null;
    }

    public void onLogon(ReceiverEndPoint receiverEndPoint) {
        this.receiverEndPoint = receiverEndPoint;
    }

    public void onLogOff() {
        this.receiverEndPoint = null;
    }

    public void receiveTwoot(Twoot twoot) {
        if (isLoggedOn()) {
            receiverEndPoint.onTwoot(twoot);
            this.lastSeenPosition = twoot.getPosition();
        }
    }
}
