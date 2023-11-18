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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Twoot twoot = (Twoot) o;
        return id.equals(twoot.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
