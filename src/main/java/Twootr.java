import java.util.Optional;

public class Twootr {

    public Optional<SenderEndPoint> onLogon(String userId, String password, ReceiverEndPoint receiverEndPoint) {
        return Optional.of(new SenderEndPoint(new User(userId,password,Position.INITIAL_POSITION), this));
    }

    public FollowStatus onFollow(User user, String userIdToFollow) {
        return FollowStatus.SUCCESS;
    }
}
