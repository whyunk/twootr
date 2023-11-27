import java.util.HashSet;
import java.util.Set;

public class User {

    private String userId;
    private byte[] password;
    private byte[] salt;
    private Set<User> followers = new HashSet<>();
    private Set<String> following = new HashSet<>();
    private ReceiverEndPoint receiverEndPoint;
    private Position lastSeenPosition;

    public User(String userId, byte[] password, byte[] salt, Position lastSeenPosition) {
        this.userId = userId;
        this.password = password;
        this.salt = salt;
        this.lastSeenPosition = lastSeenPosition;
    }

    public String getUserId() {
        return userId;
    }

    public byte[] getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public Set<String> getFollowing() {
        return following;
    }

    public Position getLastSeenPosition() {
        return lastSeenPosition;
    }

    public boolean isLoggedOn() {
        return receiverEndPoint != null;
    }

    public FollowStatus addFollower(User user) {
        if (followers.add(user)) {
            user.following.add(userId);
            return FollowStatus.SUCCESS;
        } else {
            return FollowStatus.ALREADY_FOLLOWING;
        }
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
