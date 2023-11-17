import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TwootrTest {

    private Twootr twootr = new Twootr();
    private ReceiverEndPoint receiverEndPoint = mock(ReceiverEndPoint.class);

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
        String followerId = "kim";

        FollowStatus followStatus = senderEndPoint.onFollow(followerId);

        assertEquals(FollowStatus.ALREADY_FOLLOWING, followStatus);
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
        senderEndPoint.onSendTwoot(twootId,senderEndPoint.getUser(),"hello!");

        verify(receiverEndPoint).onTwoot(new Twoot(twootId, senderEndPoint.getUser().getUserId() ,"hello!"));

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
