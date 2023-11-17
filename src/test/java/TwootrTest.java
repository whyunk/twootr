import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TwootrTest {

    private Twootr twootr = new Twootr();
    private ReceiverEndPoint receiverEndPoint = new ReceiverEndPoint() {};

    @Test
    public void shouldBeAbleToAuthenticateUser() {

        //유효 사용자의 로그온 메시지 수신
        String userId = "hyunwoo";
        String password = "123456";

        //로그온 메서드는 새 엔드포인트 반환
        Optional<SenderEndPoint> senderEndPoint = twootr.onLogon(userId, password, receiverEndPoint);

        //엔드포인트 유효성을 확인하는 어서션
        assertTrue(senderEndPoint.isPresent(),"logon failed");

    }

    @Test
    public void shouldNotAuthenticateUnknownUser() {

        String userId = "unknown user";
        String password = "123456";

        Optional<SenderEndPoint> senderEndPoint = twootr.onLogon(userId, password, receiverEndPoint);

        assertFalse(senderEndPoint.isPresent(), "there is no user");
    }

    @Test
    public void shouldNotAuthenticateWithWrongPassword() {

        String userId = "hyunwoo";
        String password = "wrong password";

        Optional<SenderEndPoint> senderEndPoint = twootr.onLogon(userId, password, receiverEndPoint);

        assertFalse(senderEndPoint.isPresent(), "wrong password");
    }
}
