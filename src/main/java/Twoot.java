import java.util.Objects;

public class Twoot {

    private String id;
    private String senderId;
    private String content;
    private Position position;

    public Twoot(String id, String senderId, String content, Position position) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Twoot twoot = (Twoot) o;
        return Objects.equals(id, twoot.id) && Objects.equals(senderId, twoot.senderId) && Objects.equals(content, twoot.content) && Objects.equals(position, twoot.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, senderId, content, position);
    }

    @Override
    public String toString() {
        return "Twoot{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", position=" + position +
                '}';
    }

    public boolean isAfter(Position lastSeenPosition) {
        return this.position.getValue() > lastSeenPosition.getValue();
    }
}
