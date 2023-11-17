import java.util.Optional;

public class Twootr {

    public Optional<SenderEndPoint> onLogon(String userId, String password, ReceiverEndPoint receiverEndPoint) {
        return Optional.of(new SenderEndPoint());
    }
}
