import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Twootr {

    static final Map<String, User> USER_DB = new HashMap<>();

    public Optional<SenderEndPoint> onLogon(String userId, String password, ReceiverEndPoint receiverEndPoint) {

        if (USER_DB.containsKey(userId)) {
            if (USER_DB.get(userId).getPassword() == password) {
                return Optional.of(new SenderEndPoint(USER_DB.get(userId), this));
            }
        }
        return Optional.empty();
    }

    public FollowStatus onFollow(User user, String userIdToFollow) {

        //유저랑 팔로우유저를 맵에서 찾고 유저는 팔로잉 추가 팔로유저는 팔로잉 추가
        if (USER_DB.containsValue(user) && USER_DB.containsKey(userIdToFollow)) {
            User my = USER_DB.get(user.getUserId());
            User follower = USER_DB.get(userIdToFollow);
            if (my.getFollowers().add(follower)) {
                follower.getFollowing().add(my);
                return FollowStatus.SUCCESS;
            } else {
                return FollowStatus.ALREADY_FOLLOWING;
            }
        }
        return FollowStatus.INVALID_USER;
    }
}
