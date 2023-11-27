import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TwootrTest {

    private Twootr twootr;
    private ReceiverEndPoint receiverEndPoint = mock(ReceiverEndPoint.class);
    private UserRepository userRepository = new InMemoryUserRepository();
    private TwootRepository twootRepository = new InMemoryTwootRepository();

    @BeforeEach
    public void setUp() {
        twootr = new Twootr(twootRepository,userRepository);
        userRepository.clear();
        twootRepository.clear();
        byte[] salt1 = KeyGenerator.newSalt();
        byte[] salt2 = KeyGenerator.newSalt();
        userRepository.add(new User("hyunwoo", KeyGenerator.hash("123456", salt1), salt1 , Position.INITIAL_POSITION));
        userRepository.add(new User("woohyun", KeyGenerator.hash("123456", salt2), salt2 , Position.INITIAL_POSITION));

    }
    @Test
    public void shouldBeAbleToAuthenticateUser() {

        logon();
    }

    @Test
    public void shouldNotAuthenticateUnknownUser() {

        String userId = "unknown user";
        String password = "123456";

        Optional<SenderEndPoint> senderEndPoint = twootr.onLogon(userId, password, receiverEndPoint);

        assertFalse(senderEndPoint.isPresent());
    }

    @Test
    public void shouldNotAuthenticateWithWrongPassword() {

        String userId = "hyunwoo";
        String password = "wrong password";

        Optional<SenderEndPoint> senderEndPoint = twootr.onLogon(userId, password, receiverEndPoint);

        assertFalse(senderEndPoint.isPresent());
    }

    @Test
    public void shouldFollowValidUser() {
        SenderEndPoint senderEndPoint = logon();

        // other user id
        String followerId = "woohyun";

        FollowStatus followStatus = senderEndPoint.onFollow(followerId);

        assertEquals(FollowStatus.SUCCESS, followStatus);
    }

    @Test
    public void shouldNotDuplicateFollowValidUser() {
        SenderEndPoint senderEndPoint = logon();

        //other user id that already follow
        String followerId = "woohyun";

        FollowStatus firstFollow = senderEndPoint.onFollow(followerId);
        FollowStatus secondFollow = senderEndPoint.onFollow(followerId);

        assertEquals(FollowStatus.SUCCESS, firstFollow);
        assertEquals(FollowStatus.ALREADY_FOLLOWING, secondFollow);
    }

    @Test
    public void shouldNotInvalidUser() {
        SenderEndPoint senderEndPoint = logon();

        //other invalid user id
        String followerId = "invalid user";

        FollowStatus followStatus = senderEndPoint.onFollow(followerId);

        assertEquals(FollowStatus.INVALID_USER, followStatus);
    }

    @Test
    public void shouldReceiveTwootsFromFollowedUser() {

        // twoot string id
        String twootId = "1";

        SenderEndPoint receiveEndPoint = logon();
        // other user logon
        SenderEndPoint senderEndPoint = otherLogon();

        receiveEndPoint.onFollow(senderEndPoint.getUser().getUserId());
        senderEndPoint.onSendTwoot(twootId, "hello!");

        verify(receiverEndPoint).onTwoot(new Twoot(twootId, senderEndPoint.getUser().getUserId() ,"hello!", new Position(0)));

    }

    @Test
    public void shouldNotReceiveTwootsAfterLogoff() {
        final String twootId = "1";

        SenderEndPoint receiveEndPoint = logon();
        // other user logon
        SenderEndPoint senderEndPoint = otherLogon();

        receiveEndPoint.onFollow(senderEndPoint.getUser().getUserId());
        receiveEndPoint.onLogOff();

        senderEndPoint.onSendTwoot(twootId, "hello!");

        verify(receiverEndPoint, never()).onTwoot(new Twoot(twootId,senderEndPoint.getUser().getUserId(), "hello!", new Position(0)));
    }

    @Test
    public void shouldReceiveReplayOfTwootsAfterLogOff() {
        final String twootId = "1";

        SenderEndPoint receiveEndPoint = logon();
        // other user logon
        SenderEndPoint senderEndPoint = otherLogon();

        receiveEndPoint.onFollow(senderEndPoint.getUser().getUserId());
        receiveEndPoint.onLogOff();

        senderEndPoint.onSendTwoot(twootId, "hello!");
        logon();

        verify(receiverEndPoint).onTwoot(new Twoot(twootId,senderEndPoint.getUser().getUserId(), "hello!", new Position(0)));
    }

    private SenderEndPoint logon() {
        //유효 사용자의 로그온 메시지 수신
        String userId = "hyunwoo";
        String password = "123456";

        //로그온 메서드는 새 엔드포인트 반환
        Optional<SenderEndPoint> senderEndPoint = twootr.onLogon(userId, password, receiverEndPoint);

        //엔드포인트 유효성을 확인하는 어서션
        assertTrue(senderEndPoint.isPresent(),"logon failed");
        return senderEndPoint.get();
    }

    private SenderEndPoint otherLogon() {

        String otherId = "woohyun";
        String password = "123456";

        Optional<SenderEndPoint> senderEndPoint = twootr.onLogon(otherId, password, mock(ReceiverEndPoint.class));
        assertTrue(senderEndPoint.isPresent(), "logon failed");

        return senderEndPoint.get();
    }
}
