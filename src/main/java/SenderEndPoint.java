import java.util.Objects;

public class SenderEndPoint {

    private final User user;
    private final Twootr twootr;

    public User getUser() {
        return user;
    }

    public SenderEndPoint(User user, Twootr twootr) {

        Objects.requireNonNull(user,"user");
        Objects.requireNonNull(twootr,"twootr");

        this.user = user;
        this.twootr = twootr;
    }

    public FollowStatus onFollow(String userIdToFollow) {

        Objects.requireNonNull(userIdToFollow, "userId");

        return twootr.onFollow(user, userIdToFollow);
    }

    public Position onSendTwoot(String id, String content) {

        return twootr.onSendTwoot(id, this.user, content);
    }

    public void onLogOff() {
        user.onLogOff();
    }

    public DeleteStatus onDeleteTwoot(final String id) {
        return twootr.onDeleteTwoot(user.getUserId(), id);
    }
}
