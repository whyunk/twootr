import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Twootr {

    private final TwootRepository twootRepository;
    private final UserRepository userRepository;

    public Twootr(TwootRepository twootRepository, UserRepository userRepository) {
        this.twootRepository = twootRepository;
        this.userRepository = userRepository;   }

    public Optional<SenderEndPoint> onLogon(String userId, String password, ReceiverEndPoint receiverEndPoint) {

        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(password, "password");

        Optional<User> authenticatedUser = userRepository
                .get(userId)
                .filter(userOfSameId -> {
                    byte[] hashedPassword = KeyGenerator.hash(password, userOfSameId.getSalt());
                    return Arrays.equals(hashedPassword, userOfSameId.getPassword());
                });

        authenticatedUser.ifPresent(user ->
        {
            user.onLogon(receiverEndPoint);
            twootRepository.query(
                    new TwootQuery()
                            .inUsers(user.getFollowing())
                            .lastSeenPosition(user.getLastSeenPosition()) , user::receiveTwoot);
            userRepository.update(user);
        });

        return authenticatedUser.map(user -> new SenderEndPoint(user,this));
    }

    public FollowStatus onFollow(User user, String userIdToFollow) {

        return userRepository.get(userIdToFollow)
                .map(userToFollow -> userRepository.follow(user,userToFollow))
                .orElse(FollowStatus.INVALID_USER);

    }

    public Position onSendTwoot(String id, User user, String content) {

        String senderId = user.getUserId();
        Twoot twoot = twootRepository.add(id, senderId, content);

        user.getFollowers().stream().filter(User::isLoggedOn).forEach(follower -> {
            follower.receiveTwoot(twoot);
            userRepository.update(follower);
        });

        return twoot.getPosition();

    }

    public RegistrationStatus onRegisterUser(String userId, String password) {

        byte[] salt = KeyGenerator.newSalt();
        byte[] hashedPassword = KeyGenerator.hash(password, salt);
        User user = new User(userId, hashedPassword, salt, Position.INITIAL_POSITION);

        return userRepository.add(user) ? RegistrationStatus.SUCCESS : RegistrationStatus.DUPLICATE;
    }

    DeleteStatus onDeleteTwoot(final String userId, final String id) {
        return twootRepository
                .get(id)
                .map(twoot ->
                {
                    var canDeleteTwoot = twoot.getSenderId().equals(userId);
                    if (canDeleteTwoot) {
                        twootRepository.delete(twoot);
                    }
                    return canDeleteTwoot ? DeleteStatus.SUCCESS : DeleteStatus.NOT_YOUR_TWOOT;
                })
                .orElse(DeleteStatus.UNKNOWN_TWOOT);
    }
}
