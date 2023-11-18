import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Twootr {

    private static final Map<String, User> USER_DB = new HashMap<>();

    static {
        USER_DB.put("hyunwoo", new User("hyunwoo", "123456", Position.INITIAL_POSITION));
        USER_DB.put("woohyun", new User("woohyun", "123456", Position.INITIAL_POSITION));

    }

    public Optional<SenderEndPoint> onLogon(String userId, String password, ReceiverEndPoint receiverEndPoint) {

        if (USER_DB.containsKey(userId)) {
            if (USER_DB.get(userId).getPassword() == password) {
                return Optional.of(new SenderEndPoint(USER_DB.get(userId), this));
            }
        }
        return Optional.empty();
    }

    public FollowStatus onFollow(User user, String userIdToFollow) {
        return FollowStatus.SUCCESS;
    }
}
