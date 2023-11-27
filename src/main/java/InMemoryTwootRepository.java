import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class InMemoryTwootRepository implements TwootRepository{

    private final List<Twoot> twoots = new ArrayList<>();
    private Position currentPosition = Position.INITIAL_POSITION;

    @Override
    public Twoot add(String id, String userId, String content) {
        currentPosition = currentPosition.next();

        Position twootPosition = currentPosition;
        Twoot twoot = new Twoot(id, userId, content, twootPosition);
        twoots.add(twoot);
        return twoot;
    }

    @Override
    public Optional<Twoot> get(String id) {
        return twoots.stream()
                .filter(twoot -> twoot.getId().equals(id))
                .findFirst();
    }

    @Override
    public void delete(Twoot twoot) {
        twoots.remove(twoot);
    }

    @Override
    public void query(TwootQuery twootQuery, Consumer<Twoot> callback) {
        if (!twootQuery.hasUsers()) {
            return;
        }

        Position lastSeenPosition = twootQuery.getLastSeenPosition();
        Set<String> inUsers = twootQuery.getInUsers();

        twoots.stream()
                .filter(twoot -> inUsers.contains(twoot.getSenderId()))
                .filter(twoot -> twoot.isAfter(lastSeenPosition))
                .forEach(callback);
    }

    @Override
    public void clear() {
        twoots.clear();
    }
}
