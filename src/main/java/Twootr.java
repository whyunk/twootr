import java.util.*;
import java.util.stream.Collectors;

public class Twootr {

    static final Map<String, User> USER_DB = new HashMap<>();
    static final List<Twoot> TWOOTS = new ArrayList<>();
    private Position currentPosition = Position.INITIAL_POSITION;

    public Optional<SenderEndPoint> onLogon(String userId, String password, ReceiverEndPoint receiverEndPoint) {

        if (USER_DB.containsKey(userId)) {
            if (USER_DB.get(userId).getPassword() == password) {
                User user = USER_DB.get(userId);
                user.onLogon(receiverEndPoint);

                List<Twoot> notReceiveTwoots = TWOOTS.stream().filter(twoot -> user.getFollowing().contains(USER_DB.get(twoot.getSenderId())))
                        .filter(twoot -> user.getLastSeenPosition().getValue() < twoot.getPosition().getValue())
                        .collect(Collectors.toList());
                for (Twoot twoot : notReceiveTwoots) {
                    user.receiveTwoot(twoot);
                }

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
            if (my.getFollowing().add(follower)) {
                follower.getFollowers().add(my);
                return FollowStatus.SUCCESS;
            } else {
                return FollowStatus.ALREADY_FOLLOWING;
            }
        }
        return FollowStatus.INVALID_USER;
    }

    public void onSendTwoot(String id, User user, String content) {

        final String senderId = user.getUserId();

        currentPosition = currentPosition.next();
        final Twoot twoot = new Twoot(id, senderId, content, currentPosition);
        TWOOTS.add(twoot);

        user.getFollowers().stream()
                .filter(User::isLoggedOn)
                .forEach(follower -> follower.receiveTwoot(twoot));
    }
}
